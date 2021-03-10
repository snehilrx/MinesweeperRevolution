package team.seven.microserver.server

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import team.seven.microserver.obj.ClientCommands
import team.seven.microserver.obj.ClientErrorCodes
import team.seven.microserver.obj.ClientObject
import team.seven.microserver.obj.GameConfig
import team.seven.microserver.server.observers.NgrokObserver
import team.seven.microserver.server.observers.SocketObserver
import java.net.ServerSocket
import java.net.Socket

class ServerImpl : Server {

    private enum class GameState {
        PLAYING, LOBBY, FINISHED
    }

    private lateinit var config: GameConfig
    private lateinit var serverSocket: ServerSocket
    private lateinit var ngrokHelper: NgrokHelper

    private lateinit var ngrokObservable: Observable<Pair<String, String>>
    private lateinit var socketsObservable: Observable<Socket>
    private lateinit var gameState: GameState

    private val clientList = ArrayList<ClientObject>()
    private val clients = HashMap<String, GameLifeCycleListener>()

    private val socketObserver = SocketObserver(this)

    override fun initialize(configuration: GameConfig) {
        config = configuration
        gameState = GameState.LOBBY
        serverSocket = ServerSocket(0)
        ngrokHelper = NgrokHelper(serverSocket.localPort)
        ngrokObservable = getNgrokObservable()
        socketsObservable = getSocketObservable(serverSocket)

        ngrokObservable.firstElement().subscribeOn(Schedulers.io()).subscribe(
            NgrokObserver {
                socketsObservable.subscribeOn(Schedulers.io()).subscribe(socketObserver)
            }
        )
    }

    private fun getSocketObservable(serverSocket: ServerSocket): Observable<Socket> = Observable.create {
        while (true) {
            try {
                val socket = serverSocket.accept()
                it.onNext(socket)
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }

    private fun getNgrokObservable(): Observable<Pair<String, String>> = Observable.create {
        try {
            val item = ngrokHelper.openPortToInternet()
            it.onNext(item)
        } catch (e: Exception) {
            it.onError(e)
        }
    }

    override fun join(clientObject: ClientObject, gameLifeCycleListener: GameLifeCycleListener) {
        if(isPlaying()){
            gameLifeCycleListener.throwError(ClientErrorCodes.CANNOT_JOIN, "Cannot join the server when game is already going on")
        }else if(isFinished()) {
            gameLifeCycleListener.throwError(ClientErrorCodes.CANNOT_JOIN, "Cannot join the server when game is already finished")
        }else if(isFull()){
            gameLifeCycleListener.throwError(ClientErrorCodes.SERVER_FULL, "Server is all ready full")
        }else{
            clientList.add(clientObject)
            clients[clientObject.uid] = gameLifeCycleListener
            if(isFull()){
                TODO("Broadcast to all players")
            }
        }
    }

    private fun isFinished(): Boolean = gameState == GameState.FINISHED

    private fun isPlaying(): Boolean  = gameState == GameState.PLAYING

    private fun isFull() = clientList.size == config.playersCount

    override fun quit(clientObject: ClientObject, gameLifeCycleListener: GameLifeCycleListener) {

        TODO("Not yet implemented")
    }

    override fun close() {
        if(this::ngrokHelper.isInitialized)
            ngrokHelper.closePortToInternet()

        socketObserver.close()

        if(this::serverSocket.isInitialized)
            serverSocket.close()
    }

}