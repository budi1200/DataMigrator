package si.budimir.dataMigrator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import org.bukkit.Bukkit
import si.budimir.dataMigrator.database.DatabaseHelper
import si.budimir.dataMigrator.database.MigrationData
import si.budimir.dataMigrator.enums.Permission
import si.budimir.dataMigrator.util.*
import java.time.Instant
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

abstract class MigrationHandler {
    companion object {
        private val plugin = DataMigrator.instance

        fun migratePlayer(playerName: String, playerUUID: String, oldPlayerName: String?, override: Boolean = false, callback: (Boolean) -> Unit) {
//            val bukkitPlayer = Bukkit.getPlayer(playerName)
//
//            if (bukkitPlayer == null) {
//                plugin.logger.info("Player $playerName not found/online")
//                return callback(false)
//            }
//
//            DatabaseHelper.isOfflineAccountMigrated(oldPlayerName ?: playerName) {
//                // Exit migration if player is already migrated, unless overridden
//                if (it && !override) {
//                    plugin.logger.info("Offline account is already migrated (${oldPlayerName ?: playerName} -> $playerName)")
//                    return@isOfflineAccountMigrated callback(false)
//                }
//
//                // Run migration handler
//                plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable {
//                    val result = executeMigration(playerUUID, playerName, oldPlayerName)
//
//                    if (result.sendEmbed) {
//                        val embeds = Embed(arrayListOf(buildEmbed(playerName, result)))
//                        val json = Json.encodeToJsonElement(embeds)
//
//                        WebHookHandler.send(json.toString(), plugin.mainConfig.discordWebHookUrl)
//                    }
//
//                    callback(result.status)
//                })
//            }
        }

//        private fun executeMigration(playerUUID: String, onlinePlayerName: String, oldPlayerName: String?): MigrationResult {
//            // Logger
//            val migrationLog = Logger()
//            // Prepare array for commands to be executed
//            val commands = ArrayList<String>()
//            migrationLog.addLog("=== Processing player $onlinePlayerName ===")
//
//            // Define old player name (same as current by default)
//            var offlinePlayerName = onlinePlayerName
//
//            // Use different offline player name if provided
//            if (oldPlayerName != null) {
//                migrationLog.addLog("  Using old name $oldPlayerName")
//                offlinePlayerName = oldPlayerName
//            }
//
//            var playtime: Int? = null
//            var claimblocks: Int? = null
//
//            val offlineUUID = UUIDGenerator.generateOfflineUUID(offlinePlayerName).toString()
//            migrationLog.addLog("  Offline UUID: $offlineUUID")
//
//            // Check if Luckperms user exists
//            val lpUser = plugin.luckpermsData.users[offlineUUID]
//
//            if (lpUser == null) {
//                migrationLog.addLog("- Could not find luckperms user ($offlinePlayerName)")
//                // Save migration to database
//                DatabaseHelper.addMigration(MigrationData(playerUUID, onlinePlayerName, offlinePlayerName, offlineUUID, migrationLog.getLogs(), false, System.currentTimeMillis()))
//                return MigrationResult(false, migrationLog)
//            }
//
//            // -- Autorank and claimblocks migration --
//            val playtimeString = plugin.autorankData[offlineUUID]
//
//            if (playtimeString == null) {
//                migrationLog.addLog("- Playtime for $onlinePlayerName ($offlinePlayerName) not found!")
//            }
//
//            if (playtimeString != null) {
//                playtime = playtimeString.toIntOrNull()
//            }
//
//            // If playtime is found add commands and calculate claimblocks
//            if (playtime != null) {
//                migrationLog.addLog("  Found playtime $playtime for $onlinePlayerName")
//                commands.add("ar set $onlinePlayerName $playtimeString")
//
//                // Claimblocks calculation
//                claimblocks = (playtime/60)*100
//                commands.add("scb $onlinePlayerName $claimblocks")
//                migrationLog.addLog("  Calculated $claimblocks claimblocks for $onlinePlayerName")
//            } else {
//                migrationLog.addLog("- Missing playtime - not calculating claimblocks")
//            }
//
//            // -- Nickname migration --
//            val playerNickname = plugin.nicknameData[offlineUUID]
//            val nicknameCommand: String?
//
//            if (playerNickname != null) {
//                nicknameCommand = "nickother $onlinePlayerName $playerNickname"
//                migrationLog.addLog("  Found nickname - $playerNickname")
//                commands.add(nicknameCommand)
//            } else {
//                migrationLog.addLog("- Nickname for $onlinePlayerName ($offlinePlayerName) not found! Skipping")
//            }
//
//            // -- Parse Luckperms permission nodes --
//            if (lpUser.nodes.size < 1) {
//                migrationLog.addLog("- Nodes missing from player $offlinePlayerName")
//                // Save migration to database
//                DatabaseHelper.addMigration(MigrationData(playerUUID, onlinePlayerName, offlinePlayerName, offlineUUID, migrationLog.getLogs(), false, System.currentTimeMillis()))
//                return MigrationResult(false, migrationLog)
//            }
//
//            migrationLog.addLog("=== Permission commands for $onlinePlayerName ===")
//
//            // Loop thru current users nodes
//            lpUser.nodes.forEach { node ->
//                val context = node.context
//                var world = ""
//                var server = ""
//                // Handle servers in context
//                if (context["server"] != null) {
//                    val value = context["server"]
//                    val isArray = value is JsonArray
//
//                    if (isArray) {
//                        value?.jsonArray?.forEach {
//                            val stripped = it.toString().replace("\"", "")
//                            server += "server=$stripped "
//                        }
//                    } else {
//                        val stripped = value.toString().replace("\"", "")
//                        server = "server=$stripped "
//                    }
//                }
//
//                // Handle world in context
//                if (context["world"] != null) {
//                    val worldName = context["world"].toString().replace("\"", "")
//                    world = "world=$worldName"
//                }
//
//                // Handle any node changes (rank names, tags, etc...)
//                node.key = processNode(node.key)
//
//                val c = "lp user $onlinePlayerName permission set ${node.key} ${node.value} $server$world"
//
//                commands.add(c)
//                migrationLog.addLog("+ $c")
//            }
//
//            migrationLog.addLog("=== Other commands for $onlinePlayerName ===")
//
//            // Add successful migration permission
//            commands.add("lp user $onlinePlayerName permission set ${Permission.MIGRATION_ATTEMPTED.getPerm()} true")
//
//            for (command in commands) {
//                // Log executed command, ignore any lp commands as they were logged earlier
//                if (command.startsWith("lp user")) continue
//
//                migrationLog.addLog(" $command")
//            }
//
//            // Make sure we execute commands sync
//            plugin.server.scheduler.callSyncMethod(plugin) {
//                // Execute the commands list
//                val console = Bukkit.getServer().consoleSender
//
//                commands.forEach { command ->
//                    Bukkit.dispatchCommand(console, command)
//                }
//            }
//
//            migrationLog.addLog("=== End of migration for $onlinePlayerName ===")
//
//            // Save migration to database
//            DatabaseHelper.addMigration(MigrationData(playerUUID, onlinePlayerName, offlinePlayerName, offlineUUID, migrationLog.getLogs(), true, System.currentTimeMillis()))
//
//            return MigrationResult(true, migrationLog, true, playtimeString.toString(), claimblocks.toString())
//        }
//
//        private fun String.replace(replacements: HashMap<String, String>): String {
//            var result = this
//            replacements.forEach { (l, r) -> result = result.replace(l, r) }
//
//            return result
//        }
//
//        private fun processNode(key: String): String {
//            val replacementTable = hashMapOf(
//                "raziskovalec" to "pripravnik",
//                "velemojster" to "bojevnik",
//                "vojak" to "vodnik",
//                "šef" to "veteran",
//                "crazytags" to "tags"
//            )
//
//            return key.replace(replacementTable)
//        }
//
//        // Discord embed builder
//        private fun buildEmbed(playerName: String, migrationResult: MigrationResult): EmbedContent {
//            val title = "Migration results for $playerName"
//            val thumbnail = Thumbnail("https://crafthead.net/cube/${playerName}/40.png")
//
//            val description = "```diff\n${migrationResult.logs.getLogs()}```"
//
//            val successColor = 3381555
//            val failColor = 12783109
//            val fields = arrayListOf<Field>()
//            var color: Int
//
//            if (migrationResult.status) {
//                color = successColor
//                fields.add(Field("Playtime", migrationResult.playtime, true))
//                fields.add(Field("Calculated Claim Blocks", migrationResult.claimblocks, true))
//            } else {
//                color = failColor
//            }
//
//            if (migrationResult.playtime == "null") {
//                color = 16753920
//            }
//
//            fields.add(Field("Successful", migrationResult.status.toString(), true))
//
//            return EmbedContent(
//                title,
//                description,
//                color,
//                Instant.now().toString(),
//                null,
//                thumbnail,
//                null,
//                fields
//            )
//
//        }
    }
}