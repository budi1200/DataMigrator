package si.budimir.dataMigrator.database

data class MigrationData(
    val uuid: String,
    val lastKnownName: String,
    val offlineName: String,
    val offlineUUID: String,
    val migrationLog: String,
    val status: Boolean,
    val migrationTime: Long,
    val id: Int? = null
)