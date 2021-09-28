package si.budimir.dataMigrator.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import si.budimir.dataMigrator.DataMigrator
import si.budimir.dataMigrator.MigrationHandler
import si.budimir.dataMigrator.enums.Lang
import si.budimir.dataMigrator.enums.Permission
import si.budimir.dataMigrator.util.MessageHelper

class PlayerJoinListener(private val plugin: DataMigrator): Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (player.hasPermission(Permission.MIGRATED.getPerm())) {
            plugin.logger.info("Has perm skipping")
            return
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
            val playerName = player.name

            plugin.logger.info("Attempting automatic migration for $playerName")

            // Inform player
            MessageHelper.sendMessage(player, Lang.MIGRATION_START_AUTO)

            // Execute migration
            val result = MigrationHandler.migratePlayer(playerName, null)

            if (result) {
                MessageHelper.sendMessage(player, Lang.MIGRATION_SUCCESS)
            } else {
                MessageHelper.sendMessage(player, Lang.MIGRATION_FAIL)
            }
        }, 100)
    }
}