package si.budimir.dataMigrator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import org.bukkit.Bukkit
import si.budimir.dataMigrator.enums.Permission
import si.budimir.dataMigrator.util.*
import java.time.Instant

abstract class MigrationHandler {
    companion object {
        private val plugin = DataMigrator.instance

        fun migratePlayer(playerName: String, oldPlayerName: String?): Boolean {
            val result = executeMigration(playerName, oldPlayerName)

            if (result.sendEmbed) {
                val embeds = Embed(arrayListOf(buildEmbed(playerName, result)))
                val json = Json.encodeToJsonElement(embeds)

                WebHookHandler.send(json.toString(), plugin.getMainConfig().getString("discordWebhookUrl"))
            }

            return result.status
        }

        private fun executeMigration(onlinePlayerName: String, oldPlayerName: String?): MigrationResult {
            val logger = Logger()
            logger.addLog("=== Processing player $onlinePlayerName ===")

            val bukkitPlayer = Bukkit.getPlayer(onlinePlayerName)

            if (bukkitPlayer == null) {
                logger.addLog("Player $onlinePlayerName not found/online")
                return MigrationResult(false, logger, false)
            }

            // Keeping this here just in case
            if (bukkitPlayer.hasPermission(Permission.MIGRATED.getPerm())) {
                logger.addLog("Player $onlinePlayerName is already migrated!")
                return MigrationResult(false, logger, false)
            }

            // Define old player name (same as current by default)
            var offlinePlayerName = onlinePlayerName

            // Use different offline player name if provided
            if (oldPlayerName != null) {
                logger.addLog("  Using old name $oldPlayerName")
                offlinePlayerName = oldPlayerName
            }

            val autorankData = plugin.autorankData
            var playtime: Int? = null
            var claimblocks: Int? = null

            val uuid = UUIDGenerator.generateOfflineUUID(offlinePlayerName).toString()
            logger.addLog("  Offline UUID: $uuid")

            val playtimeString = autorankData[uuid]

            if (playtimeString == null) {
                logger.addLog("- Playtime for $onlinePlayerName ($offlinePlayerName) not found!")
            }

            if (playtimeString != null) {
                playtime = playtimeString.toIntOrNull()
            }

            if (playtime != null) {
                logger.addLog("  Found playtime $playtime for $onlinePlayerName")
                claimblocks = (playtime/60)*100
                logger.addLog("  Calculated $claimblocks claimblocks for $onlinePlayerName")
            } else {
                logger.addLog("- Missing playtime - not calculating claimblocks")
            }

            // LuckPerms Data
            val lpUser = plugin.luckpermsData.users[uuid]

            if (lpUser == null) {
                logger.addLog("- Could not find luckperms user ($offlinePlayerName)")
                return MigrationResult(false, logger)
            }

            // Exit if there are no nodes
            if (lpUser.nodes.size < 1) {
                logger.addLog("- Nodes missing from player $offlinePlayerName")
                return MigrationResult(false, logger)
            }

            // Prepare array for commands to be executed
            val commands = ArrayList<String>()

            logger.addLog("=== Permission commands for $onlinePlayerName ===")

            // Loop thru current users nodes
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

                // Handle any node changes (rank names, tags, etc...)
                node.key = processNode(node.key)

                val c = "lp user $onlinePlayerName permission set ${node.key} ${node.value} $server$world"

                commands.add(c)
                logger.addLog("+ $c")
            }

            logger.addLog("=== Other commands for $onlinePlayerName ===")

            if (playtime != null) {
                val autorankCommand = "ar set $onlinePlayerName $playtimeString"
                val gpComand = "scb $onlinePlayerName $claimblocks"

                commands.add(autorankCommand)
                commands.add(gpComand)

                logger.addLog(" $autorankCommand")
                logger.addLog(" $gpComand")
            }

            val addMigratedPerm = "lp user $onlinePlayerName permission set ${Permission.MIGRATED.getPerm()} true"

            commands.add(addMigratedPerm)
            logger.addLog(" $addMigratedPerm")

            // Execute the commands list
            val console = Bukkit.getServer().consoleSender

            commands.forEach { command ->
                Bukkit.dispatchCommand(console, command)
            }

            logger.addLog("=== End of migration for $onlinePlayerName ===")
            return MigrationResult(true, logger, true, playtimeString.toString(), claimblocks.toString())
        }

        private fun String.replace(replacements: HashMap<String, String>): String {
            plugin.logger.info("org key $this")
            var result = this
            replacements.forEach { (l, r) -> result = result.replace(l, r) }

            return result
        }

        private fun processNode(key: String): String {
            val replacementTable = hashMapOf(
                "raziskovalec" to "pripravnik",
                "velemojster" to "bojevnik",
                "vojak" to "vodnik",
                "šef" to "veteran",
                "crazytags" to "tags"
            )

            return key.replace(replacementTable)
        }

        // Discord embed builder
        private fun buildEmbed(playerName: String, migrationResult: MigrationResult): EmbedContent {
            val title = "Migration results for $playerName"
            val thumbnail = Thumbnail("https://minotar.net/helm/${playerName}/40.png")

            val description = "```diff\n${migrationResult.logs.getLogs()}```"

            val successColor = 3381555
            val failColor = 12783109
            val fields = arrayListOf<Field>()
            var color: Int

            if (migrationResult.status) {
                color = successColor
                fields.add(Field("Playtime", migrationResult.playtime, true))
                fields.add(Field("Calculated Claim Blocks", migrationResult.claimblocks, true))
            } else {
                color = failColor
            }

            if (migrationResult.playtime == "null") {
                color = 16753920
            }

            fields.add(Field("Successful", migrationResult.status.toString(), true))

            return EmbedContent(
                title,
                description,
                color,
                Instant.now().toString(),
                null,
                thumbnail,
                null,
                fields
            )

        }
    }
}