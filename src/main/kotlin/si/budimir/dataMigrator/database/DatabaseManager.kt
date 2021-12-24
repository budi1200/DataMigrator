package si.budimir.dataMigrator.database

import org.ktorm.database.Database
import org.ktorm.schema.*
import org.ktorm.support.sqlite.SQLiteDialect
import si.budimir.dataMigrator.DataMigrator
import java.io.File

class DatabaseManager(private val plugin: DataMigrator) {
    private lateinit var db: Database

    fun connect() {
        val dbFile = File(plugin.dataFolder, "database.db")
        var isNew = false

        try {
            if (!dbFile.exists()) {
                dbFile.createNewFile()
                isNew = true
            }

            val url: String = "jdbc:sqlite:" + dbFile.path

            db = Database.connect(
                url = url,
                driver = "si.budimir.dataMigrator.libs.sqlite.JDBC",
                dialect = SQLiteDialect()
            )

            if (isNew) {
                db.useConnection {
                    val sql = """
                        CREATE TABLE migration_entity (
                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                          uuid VARCHAR(254) NOT NULL,
                          last_known_name VARCHAR(254) NOT NULL,
                          offline_name VARCHAR(254) NOT NULL,
                          offline_uuid VARCHAR(254) NOT NULL,
                          migration_log TEXT NOT NULL,
                          status BOOLEAN NOT NULL,
                          migration_time BIGINT NOT NULL
                        );
                    """.trimIndent()

                    it.prepareStatement(sql).execute()
                }
            }
        } catch (e: Exception) {
            plugin.logger.severe("Failed to connect to database!")
            e.printStackTrace()
        }
    }

    fun getDatabase(): Database {
        return db
    }
}

object MigrationEntity: Table<Nothing>("migration_entity") {
    val id = int("id").primaryKey()
    val uuid = varchar("uuid")
    val lastKnownName = varchar("last_known_name")
    val offlineName = varchar("offline_name")
    val offlineUUID = varchar("offline_uuid")
    val migrationLog = text("migration_log")
    val status = boolean("status")
    val migrationTime = long("migration_time")
}