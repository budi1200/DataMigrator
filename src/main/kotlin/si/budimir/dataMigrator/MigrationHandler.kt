package si.budimir.dataMigrator

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import si.budimir.dataMigrator.util.UUIDGenerator
import kotlin.math.floor

abstract class MigrationHandler {
    companion object {
        private val plugin = DataMigrator.instance

        fun migratePlayer(playerName: String): Boolean {
            plugin.logger.info("Processing player $playerName")

            val autorankData = plugin.autorankData
            val playtime: Int?

            val uuid = UUIDGenerator.generateOfflineUUID("budi1200").toString()

            val playtimeString = autorankData[uuid]

            if (playtimeString == null) {
                plugin.logger.info("Player ($playerName) playtime not found!")
                return false
            }

            playtime = playtimeString.toIntOrNull()

            if (playtime == null) {
                plugin.logger.info("Error converting playtime to Int - $playtimeString")
                return false
            }

            plugin.logger.info("Found playtime $playtime for $playerName")

            val claimblocks: Int = (playtime/60)*100
            plugin.logger.info("Calculated $claimblocks claimblocks for $playerName")

            // LuckPerms Data
            val lpUser = plugin.luckpermsData.users[uuid]

            if (lpUser == null) {
                plugin.logger.info("Could not find luckperms user ($playerName)")
                return false
            }

            if (lpUser.nodes.size < 1) {
                plugin.logger.info("Nodes missing from player $playerName")
                return false
            }

            val commands = ArrayList<String>()

            lpUser.nodes.forEach { node ->
                val context = node.context
                var world = ""
                var server = ""
                // Handle servers in context
                if (context["server"] != null) {
                    val value = context["server"]
                    val isArray = value is JsonArray

                    if (isArray) {
                        value?.jsonArray?.forEach {
                            val stripped = it.toString().replace("\"", "")
                            server += "server=$stripped "
                        }
                    } else {
                        val stripped = value.toString().replace("\"", "")
                        server = "server=$stripped "
                    }
                }

                // Handle world in context
                if (context["world"] != null) {
                    val worldName = context["world"].toString().replace("\"", "")
                    world = "world=$worldName"
                }

                val c = "lp user $playerName permission set ${node.key} ${node.value} $server$world"
                plugin.logger.info(c)
                commands.add(c)
            }

//            plugin.logger.info(plugin.autorankData[uuid.toString()].toString())
            return false
        }
    }
}