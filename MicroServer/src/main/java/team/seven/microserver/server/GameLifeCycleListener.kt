package team.seven.microserver.server

import team.seven.microserver.obj.ClientCommands
import team.seven.microserver.obj.ClientErrorCodes

interface GameLifeCycleListener {

    /*
        [listeners, client] --- [
        server -> event -> send information to client using sockets -> client socket will receive these events and generate the same events
     *
     */

    fun waitingRoomStatusChanged(joinedPlayers: Int, totalPlayers: Int)

    fun onStart()

    // called when all other players had already won or lost and you are the last player still playing
    fun onEnd()

    fun throwError(code: ClientErrorCodes, s: String)

}
