package si.budimir.dataMigrator.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import si.budimir.dataMigrator.DataMigrator
import si.budimir.dataMigrator.MigrationJob
import si.budimir.dataMigrator.database.DatabaseHelper
import si.budimir.dataMigrator.enums.Permission
import si.budimir.dataMigrator.util.MessageHelper

class PlayerJoinListener(private val plugin: DataMigrator): Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (player.hasPermission(Permission.MIGRATION_ATTEMPTED.getPerm())) {
            plugin.logger.info("${player.name} is already migrated - Skipping")
            return
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
            val playerName = player.name
            val playerUUID = player.uniqueId.toString()

            // Prevent migrating players twice, allows for admin override
            if (player.hasPermission(Permission.MIGRATION_ATTEMPTED.getPerm())) {
                plugin.logger.info("Migration already attempted for $playerName - skipping")
                return@scheduleSyncDelayedTask
            }

            DatabaseHelper.isAccountMigrated(playerName) { isMigrated ->
                if (isMigrated) {
                    plugin.logger.info("Account is already migrated $playerName")
                    return@isAccountMigrated
                }

                plugin.logger.info("Attempting automatic migration for $playerName ($playerUUID)")

                // Inform player
                MessageHelper.sendMessage(player, plugin.mainConfig.lang.migrationStartAuto)

                MigrationJob(player).runMigration()
            }
        }, 120)
    }
}