package si.budimir.dataMigrator

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

//            plugin.logger.info(plugin.autorankData[uuid.toString()].toString())
            return false
        }
    }
}