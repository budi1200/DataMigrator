package si.budimir.dataMigrator.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class MainConfigData(
    val pluginPrefix: String = "<#FC7B00><bold>DataMigration</bold> <white><bold>»</bold> ",
    val discordWebHookUrl: String = "",
    val lang: Lang = Lang()
)

@ConfigSerializable
data class Lang(
    val missingPermission: String = "<red>Missing permission",
    val migrationFail: String = "<red>Migration Failed! Contact an admin!",
    val migrationSuccess: String = "<green>Migration successful!",
    val migrationStartAuto: String = "<yellow>Automatic migration started!",
    val alreadyMigrated: String = "<white>You are already migrated",
    val migrationDataMissing: String = "<white>No migration data found",
    val migrationExists: String = "<red>Account already migrated!"
)