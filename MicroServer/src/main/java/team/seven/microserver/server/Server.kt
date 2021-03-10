package team.seven.microserver.server

import team.seven.microserver.obj.ClientObject
import team.seven.microserver.obj.GameConfig
import team.seven.microserver.server.GameLifeCycleListener

interface Server {

    // To initialize the server socket
    fun initialize(configuration: GameConfig)

    fun join(clientObject: ClientObject, gameLifeCycleListener: GameLifeCycleListener)

    fun quit(clientObject: ClientObject, gameLifeCycleListener: GameLifeCycleListener)

    fun close()

}