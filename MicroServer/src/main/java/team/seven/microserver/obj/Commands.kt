package team.seven.microserver.obj

// client to server
enum class ServerCommands {
    JOIN, LEAVE, PLANT, MINE_MAP
}

// server to client
enum class ClientCommands {
    WAITING_ROOM_STATUS_CHANGED
}

enum class ClientErrorCodes{
    SERVER_FULL, CANNOT_JOIN
}