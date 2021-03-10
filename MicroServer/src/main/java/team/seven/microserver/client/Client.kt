package team.seven.microserver.client

import team.seven.microserver.obj.ClientObject
import team.seven.microserver.obj.GameConfig
import team.seven.microserver.server.GameLifeCycleListener

interface Client {

    fun initialize()

    fun join(clientObject: ClientObject, gameLifeCycleListener: GameLifeCycleListener)

    fun quit(clientObject: ClientObject, gameLifeCycleListener: GameLifeCycleListener)

}