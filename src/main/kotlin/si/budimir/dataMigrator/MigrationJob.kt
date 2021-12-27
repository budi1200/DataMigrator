package si.budimir.dataMigrator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import si.budimir.dataMigrator.database.DatabaseHelper
import si.budimir.dataMigrator.database.MigrationData
import si.budimir.dataMigrator.enums.Permission
import si.budimir.dataMigrator.util.*
import java.time.Instant

class MigrationJob(
    private val currentPlayer: Player,
    private val offlinePlayerName: String = currentPlayer.name,
    private val adminPlayer: CommandSender? = null
) {
    private val plugin = DataMigrator.instance
    private val migrationLog = Logger()

    private val currentPlayerName = currentPlayer.name
    private val currentPlayerUUID = currentPlayer.uniqueId
    private val offlineUUID: String = UUIDGenerator.generateOfflineUUID(offlinePlayerName).toString()
    private val commands: ArrayList<String> = arrayListOf()
    private var playtime: Int? = null
    private var claimblocks: Int? = null
    private var playtimeGroup: String? = null

    private val playTimeRankLevels = hashMapOf(
        "default" to 0,
        "začetnik" to 1,
        "popotnik" to 2,
        "pionir" to 3,
        "raziskovalec" to 4,
        "mojster" to 5,
        "velemojster" to 6,
        "vojak" to 7,
        "poveljnik" to 8
    )

    fun runMigration() {
        // Migration handler
        plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable {
            migrationLog.addLog("=== Processing player $currentPlayerName ===")
            migrationLog.addLog("  Using offline name: $offlinePlayerName")
            migrationLog.addLog("  Offline UUID: $offlineUUID")

            // -- Data gathering phase --

            // Find Luckperms user
            val luckPermsUser = getLuckPermsUser()
                ?: return@Runnable exitMigration("- Could not find luckperms user ($offlinePlayerName)", false)

            if (luckPermsUser.nodes.size < 1 || luckPermsUser.username == "null") {
                return@Runnable exitMigration("- Permission nodes missing for player $offlinePlayerName", false)
            }

            // Find playtime data
            playtime = getPlaytime()
            commands.add("ar set $currentPlayerName $playtime")

            // Calculate Griefprevention claimblocks
            claimblocks = getClaimblocks()
            commands.add("scb $currentPlayerName $claimblocks")

            // Check for nickname
            val nickname = getNickname()
            if (nickname != null) {
                commands.add("nickother $currentPlayerName $nickname")
            }

            // -- Permission collection phase --
            migrationLog.addLog("=== Permission commands for $currentPlayerName ===")

            // Find all user permission nodes
            luckPermsUser.nodes.forEach { node ->
                // Do not migrate suffix
                if (node.type == "suffix") return@forEach

                // Process only donator groups
                if (node.type == "inheritance") {
                    val strippedGroup = node.key.replace("group." , "")

                    if (playTimeRankLevels[strippedGroup] != null) {
                        playtimeGroup = strippedGroup
                    }

                    if (
                        node.key != "group.donator"
                        && node.key != "group.donator+"
                        && node.key != "group.vip"
                        && node.key != "group.vip+"
                    ) return@forEach
                }

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

                var c = "lp user $currentPlayerName permission set ${node.key} ${node.value} $server$world"

                if (node.expiry != null) {
                    c = "lp user $currentPlayerName permission settemp ${node.key} ${node.value} ${node.expiry} $server$world"
                }

                commands.add(c)
                migrationLog.addLog("+ $c")
            }

            // Handle user group
            val lpPlaytimeGroup = "lp user $currentPlayerName permission set datamigrator.legacy.$playtimeGroup"
            migrationLog.addLog("+ $lpPlaytimeGroup")
            commands.add(lpPlaytimeGroup)

            // Add migration attempted permission
            commands.add("lp user $currentPlayerName permission set ${Permission.MIGRATION_ATTEMPTED.getPerm()} true")

            // Add OG permission
            commands.add("lp user $currentPlayerName permission set slocraft.og true")

            migrationLog.addLog("=== Other commands $currentPlayerName ===")

            // Log other commands and ignore any lp commands as they were logged earlier
            for (command in commands) {
                if (command.startsWith("lp user")) continue

                migrationLog.addLog(" $command")
            }

            // Make sure we execute commands sync
            plugin.server.scheduler.callSyncMethod(plugin) {
                // Execute the commands list
                val console = Bukkit.getServer().consoleSender

                commands.forEach { command ->
                    Bukkit.dispatchCommand(console, command)
                }

                // Runs fcheck command x times
                if (playtimeGroup != null) {
                    val playtimeChecks = playTimeRankLevels[playtimeGroup]

                    if (playtimeChecks != null) { // Probably a redundant if
                        plugin.logger.info("sitll here")
                        for (i in 0 until playtimeChecks) {
                            plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
                                Bukkit.dispatchCommand(console, "ar fcheck $currentPlayerName")
                            }, (100 + i * 30).toLong())
                        }
                    }
                }
            }

            migrationLog.addLog("=== End of migration $currentPlayerName ===")
            exitMigration("Success", true)
        })
    }

    private fun getLuckPermsUser(): LuckPermsUser? {
        return plugin.luckpermsData.users[offlineUUID]
    }

    private fun getPlaytime(): Int {
        val playtimeString = plugin.autorankData[offlineUUID]

        if (playtimeString == null) {
            migrationLog.addLog("- Playtime for $offlinePlayerName not found!")
            return 0
        }

        migrationLog.addLog("  Found playtime $playtimeString")
        return playtimeString.toInt()
    }

    private fun getClaimblocks(): Int {
        if (playtime == -1) {
            migrationLog.addLog("- Missing playtime - not calculating claimblocks")
            return 100
        }

        val calc = (playtime!! / 60) * 100
        migrationLog.addLog("  Calculated $calc claimblocks")

        return calc
    }

    private fun getNickname(): String? {
        val playerNickname = plugin.nicknameData[offlineUUID]

        if (playerNickname == null) {
            migrationLog.addLog("- Nickname for $offlinePlayerName not found! Skipping")
            return null
        }

        migrationLog.addLog("  Found nickname - $playerNickname")
        return playerNickname
    }

    private fun processNode(key: String): String {
        val replacementTable = hashMapOf(
            "začetnik" to "zacetnik",
            "raziskovalec" to "pripravnik",
            "velemojster" to "bojevnik",
            "vojak" to "vodnik",
            "šef" to "veteran",
            "crazytags" to "tags"
        )

        return key.replace(replacementTable)
    }

    private fun String.replace(replacements: HashMap<String, String>): String {
        var result = this
        replacements.forEach { (l, r) -> result = result.replace(l, r) }

        return result
    }

    // Discord embed builder
    private fun buildEmbed(success: Boolean): EmbedContent {
        val title = "Migration results for $currentPlayerName"
        val thumbnail = Thumbnail("https://crafthead.net/cube/${currentPlayerUUID}/64.png")

        val description = "```diff\n${migrationLog.getLogs()}```"

        val successColor = 3381555
        val failColor = 12783109
        val fields = arrayListOf<Field>()

        val color: Int = if (success) {
            successColor

        } else {
            failColor
        }

        fields.add(Field("Playtime", "$playtime", true))
        fields.add(Field("Calculated Claim Blocks", "$claimblocks", true))

        fields.add(Field("Successful", success.toString(), true))

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

    private fun exitMigration(exitMessage: String, success: Boolean) {
        migrationLog.addLog(exitMessage)

        // Send discord embed
        val embeds = Embed(arrayListOf(buildEmbed(success)))
        val json = Json.encodeToJsonElement(embeds)

        WebHookHandler.send(json.toString(), plugin.mainConfig.discordWebHookUrl)

        // Save to db
        DatabaseHelper.addMigration(
            MigrationData(
                currentPlayerUUID.toString(),
                currentPlayerName,
                offlinePlayerName,
                offlineUUID,
                migrationLog.getLogs(),
                success,
                System.currentTimeMillis()
            )
        )

        val messageToPlayer = if (success) {
            plugin.mainConfig.lang.migrationSuccess
        } else {
            plugin.mainConfig.lang.migrationFail
        }

        MessageHelper.sendMessage(currentPlayer, messageToPlayer)
        if (adminPlayer != null && adminPlayer.name != currentPlayerName) {
            MessageHelper.sendMessage(adminPlayer, messageToPlayer)
        }

    }
}