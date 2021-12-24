package si.budimir.dataMigrator.enums

enum class Permission(private val value: String) {
    STATUS("status"),
    STATUS_OTHERS("status.others"),
    ADMIN("admin"),
    MIGRATION_ATTEMPTED("attempted");

    fun getPerm(): String {
        return "datamigrator.$value"
    }
}