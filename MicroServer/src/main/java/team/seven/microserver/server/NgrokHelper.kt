package team.seven.microserver.server

import java.io.IOException

class NgrokHelper(private val port: Int){
    private lateinit var ngrok : Process

    fun openPortToInternet(): Pair<String, String>{
        var retVal = Pair("", "")
        if(this::ngrok.isInitialized) {
            if(if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    ngrok.isAlive
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            ){
                throw IllegalStateException("Ngrok is already running for port $port")
            }
        }
        try {
            ngrok = Runtime.getRuntime().exec("ngrok tcp $port")
            ngrok.waitFor()
            val text = ngrok.inputStream.reader().readText()
            val regex = "(tcp)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?".toRegex()
            regex.find(text)?.let {
                retVal = Pair(it.groupValues[1], it.groupValues[2])
            }

        } catch (e: IOException) {
            throw Exception(e)
        } catch (e: InterruptedException) {
            throw Exception(e)
        }
        return retVal
    }

    fun closePortToInternet(){
        if(this::ngrok.isInitialized) return
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ngrok.destroyForcibly()
        }else{
            ngrok.destroy()
        }
    }

}