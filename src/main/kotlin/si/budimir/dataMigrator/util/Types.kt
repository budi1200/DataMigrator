package si.budimir.dataMigrator.util

data class MigrationResult(
    val status: Boolean,
    val logs: Logger,
    val sendEmbed: Boolean = true,
    val playtime: String = "/",
    val claimblocks: String = "/"
)