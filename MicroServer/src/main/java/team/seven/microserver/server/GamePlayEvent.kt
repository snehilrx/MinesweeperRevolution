package team.seven.microserver.server

interface GamePlayEvent {

    fun plantMine(opponentId: String, coordinates: Pair<Int, Int>)

    fun revealTile()

    fun timeout()

}
