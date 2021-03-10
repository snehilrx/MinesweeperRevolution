package team.seven.microserver.server.observers

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import team.seven.microserver.server.ClientHandler
import team.seven.microserver.server.Server
import timber.log.Timber
import java.net.Socket

class SocketObserver(private val server: Server) : Observer<Socket> {

    private val clientHandles = ArrayList<ClientHandler>()

    override fun onSubscribe(d: Disposable) {
        TODO("Not yet implemented")
    }

    override fun onError(e: Throwable) {
        Timber.e(e, "Failed to connect to client :: ")
    }

    override fun onComplete() {
        TODO("Not yet implemented")
    }

    override fun onNext(t: Socket) {
        clientHandles.add(ClientHandler(server, t))
    }

    fun close(){
        clientHandles.forEach {
            it.close()
        }
    }
}