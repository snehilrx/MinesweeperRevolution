package team.seven.microserver.server

import com.google.gson.Gson
import com.google.gson.JsonElement
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import team.seven.microserver.obj.ClientCommands
import team.seven.microserver.obj.ClientErrorCodes
import team.seven.microserver.obj.ClientObject
import team.seven.microserver.obj.ServerCommands
import team.seven.microserver.obj.ServerCommands.*
import team.seven.microserver.util.Utils
import timber.log.Timber
import java.io.DataInputStream
import java.io.DataOutput
import java.io.DataOutputStream
import java.net.Socket
import java.text.ParseException

class ClientHandler(private val server: Server, private val client: Socket) : GameLifeCycleListener, Observer<String> {

    private val inputStream = DataInputStream(client.getInputStream())
    private val outputStream = DataOutputStream(client.getOutputStream())

    private lateinit var clientObject: ClientObject

    private var shouldProcessGameCommands = false

    private val reader = Observable.create<String> {
        while (true){
            try {
                val command = inputStream.readUTF()
                it.onNext(command)
            }catch (e: Exception){
                it.onError(Exception("Failed to read command from input stream", e))
                onDisconnect()
            }
        }
    }

    fun write(value: String) = Observable.create<Any> {
        while (true){
            try {
                outputStream.writeUTF(value)
            }catch (e: Exception){
                it.onError(Exception("Failed to read command from input stream", e))
            }
        }
    }.subscribeOn(Schedulers.io()).subscribe()

    init {
        reader.subscribeOn(Schedulers.io()).subscribe(this)
    }


    fun close() {
        client.close()
    }

    override fun waitingRoomStatusChanged(joinedPlayers: Int, totalPlayers: Int) {
        val map = HashMap<String, Any>()
        map["code"] = ClientCommands.WAITING_ROOM_STATUS_CHANGED
        map["joinedPlayers"] = joinedPlayers
        map["totalPlayers"] = totalPlayers
        write(Utils.gson.toJson(map))
    }

    override fun onStart() {
        shouldProcessGameCommands = true
    }

    override fun onEnd() {
        shouldProcessGameCommands = false
    }

    override fun throwError(code: ClientErrorCodes, s: String) {
        val map = HashMap<String, Any>()
        map["error"] = code
        map["message"] = s
        write(Utils.gson.toJson(map))
        Timber.e(Exception(s), "The following error has occurred with client ${clientObject.uid} :: ")
    }

    override fun onSubscribe(d: Disposable) {
        TODO("Not yet implemented")
    }

    override fun onNext(t: String) {
        val map = Utils.gson.fromJson(t, Map::class.java)
        when {
            map["code"] as? Int ?: Timber.e(ParseException("Command not found", 0)) == JOIN -> {
                val clientObject =  map["clientObject"] as? ClientObject?
                if(clientObject == null) Timber.e(ParseException("Failed to parse client object", 10))
                clientObject?.let {
                    this.clientObject = it
                    server.join(it, this)
                }
            }
            shouldProcessGameCommands -> {

            }
            else -> {
                // Go and fuck yourself
                
            }
        }
    }

    override fun onError(e: Throwable) {
        Timber.e(e)
    }

    override fun onComplete() {
        TODO("Not yet implemented")
    }

    private fun onDisconnect(){

    }
}