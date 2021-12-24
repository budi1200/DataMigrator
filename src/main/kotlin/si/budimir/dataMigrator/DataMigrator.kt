package si.budimir.dataMigrator

import org.bukkit.plugin.java.JavaPlugin
import si.budimir.dataMigrator.commands.AsyncTabCompleteListener
import si.budimir.dataMigrator.commands.DataMigratorCommand
import si.budimir.dataMigrator.config.MainConfig
import si.budimir.dataMigrator.config.MainConfigData
import si.budimir.dataMigrator.database.DatabaseManager
import si.budimir.dataMigrator.listeners.PlayerJoinListener
import si.budimir.dataMigrator.util.*
import java.time.Duration
import java.time.Instant
import kotlin.math.log

class DataMigrator: JavaPlugin() {
    lateinit var mainConfigObj: MainConfig
    lateinit var mainConfig: MainConfigData

    private lateinit var mainCommand: DataMigratorCommand
    lateinit var autorankData: Map<String, String>
    lateinit var luckpermsData: LuckPermsObject
    lateinit var nicknameData: Map<String, String>
    lateinit var dbManager: DatabaseManager

    companion object {
        lateinit var instance: DataMigrator
    }

    override fun onEnable() {
        val startTime = Instant.now()
        super.onEnable()

        // Set instance
        instance = this

        // Config init
        mainConfigObj = MainConfig(instance)
        mainConfig = mainConfigObj.getConfig()

        // Database
        dbManager = DatabaseManager(this)
        dbManager.connect()

        MessageHelper.load(this)

        // Init commands
        mainCommand = DataMigratorCommand()
        getCommand("migration")?.setExecutor(mainCommand)

        // Register events
        server.pluginManager.registerEvents(AsyncTabCompleteListener(this), this)
        server.pluginManager.registerEvents(PlayerJoinListener(this), this)

        // Get Autorank data
        autorankData = AutorankData.parseAutorankData()

        // Get LuckPerms data
        luckpermsData = LuckPermsData.parseLuckPermsData()

        // Get Nickname data
        nicknameData = NicknameData.parseNicknameData()

        val loadTime = Duration.between(startTime, Instant.now())
        logger.info("DataMigrator loaded (took ${loadTime.toMillis()}ms)")
    }

    fun getMainCommand(): DataMigratorCommand {
        return mainCommand
    }
}