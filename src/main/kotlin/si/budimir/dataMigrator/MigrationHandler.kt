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

        fun migratePlayer(playerName: String): Boolean {
            val result = executeMigration(playerName)

            if (result.sendEmbed) {
                val embeds = Embed(arrayListOf(buildEmbed(playerName, result)))
                val json = Json.encodeToJsonElement(embeds)

                WebHookHandler.send(json.toString(), plugin.getMainConfig().getString("discordWebhookUrl"))
            }

            return result.status
        }

        // TODO: Add option to allow no autorank data
        private fun executeMigration(playerName: String): MigrationResult {
            val logger = Logger()
            logger.addLog("=== Processing player $playerName ===")

            val bukkitPlayer = Bukkit.getPlayer(playerName)

            if (bukkitPlayer == null) {
                logger.addLog("Player $playerName not found/online")
                return MigrationResult(false, logger, false)
            }

            if (bukkitPlayer.hasPermission(Permission.MIGRATED.getPerm())) {
                logger.addLog("Player $playerName is already migrated!")
                return MigrationResult(false, logger, false)
            }

            val autorankData = plugin.autorankData
            var playtime: Int? = null
            var claimblocks: Int? = null

            val uuid = UUIDGenerator.generateOfflineUUID("budi1200").toString()
            logger.addLog("  Offline UUID: $uuid")

            val playtimeString = autorankData[uuid]

            if (playtimeString == null) {
                logger.addLog("- Playtime for $playerName not found!")
            }

            if (playtimeString != null) {
                playtime = playtimeString.toIntOrNull()
            }

            if (playtime != null) {
                logger.addLog("  Found playtime $playtime for $playerName")
                claimblocks = (playtime/60)*100
                logger.addLog("  Calculated $claimblocks claimblocks for $playerName")
            } else {
                logger.addLog("- Missing playtime - not calculating claimblocks")
            }

            // LuckPerms Data
            val lpUser = plugin.luckpermsData.users[uuid]

            if (lpUser == null) {
                logger.addLog("- Could not find luckperms user ($playerName)")
                return MigrationResult(false, logger)
            }

            // Exit if there are no nodes
            if (lpUser.nodes.size < 1) {
                logger.addLog("- Nodes missing from player $playerName")
                return MigrationResult(false, logger)
            }

            // Prepare array for commands to be executed
            val commands = ArrayList<String>()

            logger.addLog("=== Permission commands for $playerName ===")

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

                node.key = node.key.replace("raziskovalec", "pripravnik")
                node.key = node.key.replace("velemojster", "bojevnik")
                node.key = node.key.replace("vojak", "vodnik")
                node.key = node.key.replace("šef", "veteran")

                // TODO: Replace gkit permissions?

                val c = "lp user $playerName permission set ${node.key} ${node.value} $server$world"

                commands.add(c)
                logger.addLog("+ $c")
            }

            logger.addLog("=== Other commands for $playerName ===")

            if (playtime != null) {
                val autorankCommand = "ar set $playerName $playtimeString"
                val gpComand = "scb $playerName $claimblocks"

                commands.add(autorankCommand)
                commands.add(gpComand)

                logger.addLog(" $autorankCommand")
                logger.addLog(" $gpComand")
            }

            val addMigratedPerm = "lp user $playerName permission set ${Permission.MIGRATED.getPerm()} true"

            commands.add(addMigratedPerm)
            logger.addLog(" $addMigratedPerm")

            // TODO: Execute the commands list

            logger.addLog("=== End of migration for $playerName ===")
            return MigrationResult(true, logger, true, playtimeString.toString(), claimblocks.toString())
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