package si.budimir.dataMigrator.enums

enum class Lang(private val path: String) {
    MISSING_PERMISSION("missing-permission"),
    MIGRATION_FAIL("migration-fail"),
    MIGRATION_SUCCESS("migration-success"),
    MIGRATION_START_AUTO("migration-start-auto"),
    ALREADY_MIGRATED("already-migrated");

    fun getPath(): String {
        return "lang.$path"
    }
}