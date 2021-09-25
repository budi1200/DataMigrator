package si.budimir.dataMigrator.enums

enum class Permission(private val value: String) {
    ADMIN("admin"),
    MIGRATED("migrated");

    fun getPerm(): String {
        return "datamigrator.$value"
    }
}