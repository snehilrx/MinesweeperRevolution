import org.junit.Test
import team.seven.microserver.obj.ClientCommands
import team.seven.microserver.obj.ClientObject
import team.seven.microserver.util.Utils


class ConceptCheck {
    @Test
    fun gsonCheck(){
        val map = HashMap<String, Any>()
        map["code"] = ClientCommands.WAITING_ROOM_STATUS_CHANGED
        map["clientObject"] = ClientObject("sdfjsfk")
        val items = Utils.gson.toJson(map)
        println(items)

        val tmap = Utils.gson.fromJson(items, Map::class.java)
        println(map["code"])
        println(map["clientObject"] as? ClientObject)
    }
}