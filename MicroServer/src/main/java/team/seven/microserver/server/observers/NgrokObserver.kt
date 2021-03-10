package team.seven.microserver.server.observers

import io.reactivex.MaybeObserver
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.net.Socket

class NgrokObserver(private val callback: () -> Unit) : MaybeObserver<Pair<String, String>> {


    override fun onSubscribe(d: Disposable) {

    }

    override fun onError(e: Throwable) {
        Timber.e(e,"Failed to execute ngrok :: ")
    }

    override fun onComplete() {
        TODO("Not yet implemented")
    }

    override fun onSuccess(t: Pair<String, String>) {
        callback()
        TODO("Upload port and server Info")
    }

}
