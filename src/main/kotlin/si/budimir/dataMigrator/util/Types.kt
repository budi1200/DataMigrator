package si.budimir.dataMigrator.util

data class MigrationJobResult(
    val status: Boolean,
    val logs: Logger,
    val playtime: String = "/",
    val claimblocks: String = "/"
)