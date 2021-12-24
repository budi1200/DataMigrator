package si.budimir.dataMigrator.database

import org.ktorm.dsl.*
import si.budimir.dataMigrator.DataMigrator

class DatabaseHelper {
    companion object {
        private val plugin = DataMigrator.instance

        fun addMigration(data: MigrationData) {
            plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable {
                try {
                    plugin.dbManager.getDatabase().useTransaction {
                        plugin.dbManager.getDatabase().insert(MigrationEntity) { entity ->
                            set(entity.uuid, data.uuid)
                            set(entity.lastKnownName, data.lastKnownName)
                            set(entity.offlineName, data.offlineName)
                            set(entity.offlineUUID, data.offlineUUID)
                            set(entity.migrationLog, data.migrationLog)
                            set(entity.status, data.status)
                            set(entity.migrationTime, data.migrationTime)
                        }

                        it.commit()
                    }

                    plugin.logger.info("Inserted migration for ${data.lastKnownName}")
                } catch (e: Error) {
                    plugin.logger.severe("There was an error saving a migration - ${data.lastKnownName} (${data.uuid})")
                    e.printStackTrace()
                }
            })
        }

        fun isAccountMigrated(playerName: String, callback: (Boolean) -> Unit) {
            plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable {
                try {
                    val database = plugin.dbManager.getDatabase()

                    val result = database
                        .from(MigrationEntity)
                        .select()
                        .where((MigrationEntity.offlineName eq playerName) or (MigrationEntity.lastKnownName eq playerName))
                        .orderBy(MigrationEntity.migrationTime.desc())
                        .limit(1)
                        .mapNotNull {
                            if (it[MigrationEntity.id] == null) return@mapNotNull false

                            return@mapNotNull true
                        }

                    callback(result.firstOrNull() ?: false)
                } catch (e: Error) {
                    e.printStackTrace()
                    callback(true)
                }
            })
        }

        fun getLatestPlayerMigrationAttempt(playerName: String, callback: (MigrationData?) -> Unit) {
            plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable {
                try {
                    val database = plugin.dbManager.getDatabase()

                    val result = database
                        .from(MigrationEntity)
                        .select()
                        .where(MigrationEntity.lastKnownName eq playerName)
                        .orderBy(MigrationEntity.migrationTime.desc())
                        .limit(1)
                        .mapNotNull {
                            if (it[MigrationEntity.id]  == null) return@mapNotNull null

                            return@mapNotNull mapToMigrationData(it)
                        }

                    callback(result.firstOrNull())
                } catch (e: Error) {
                    e.printStackTrace()
                    callback(null)
                }
            })
        }

        private fun mapToMigrationData(it: QueryRowSet) = MigrationData(
            it[MigrationEntity.uuid]!!,
            it[MigrationEntity.lastKnownName]!!,
            it[MigrationEntity.offlineName]!!,
            it[MigrationEntity.offlineUUID]!!,
            it[MigrationEntity.migrationLog]!!,
            it[MigrationEntity.status]!!,
            it[MigrationEntity.migrationTime]!!
        )
    }
}