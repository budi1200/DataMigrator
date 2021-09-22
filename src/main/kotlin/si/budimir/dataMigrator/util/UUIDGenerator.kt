package si.budimir.dataMigrator.util

import si.budimir.dataMigrator.DataMigrator
import java.nio.charset.Charset
import java.util.*

abstract class UUIDGenerator {
    companion object {
        fun generateOfflineUUID(playerName: String): UUID {
            val generatedUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:$playerName").toByteArray(Charset.forName("UTF-8")))
            DataMigrator.instance.logger.info("Generated UUID: $generatedUUID for $playerName")

            return generatedUUID
        }
    }
}