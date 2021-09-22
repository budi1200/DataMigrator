package si.budimir.dataMigrator

import org.bukkit.plugin.java.JavaPlugin
import si.budimir.dataMigrator.commands.AsyncTabCompleteListener
import si.budimir.dataMigrator.commands.DataMigratorCommand
import si.budimir.dataMigrator.config.MainConfig
import si.budimir.dataMigrator.util.AutorankData

class DataMigrator: JavaPlugin() {
    private lateinit var mainConfig: MainConfig
    private lateinit var mainCommand: DataMigratorCommand
    lateinit var autorankData: Map<String, String>

    companion object {
        lateinit var instance: DataMigrator
    }

    override fun onEnable() {
        super.onEnable()

        // Set instance
        instance = this

        // Config init
        mainConfig = MainConfig(instance)

        // Init commands
        mainCommand = DataMigratorCommand()
        getCommand("dmig")?.setExecutor(mainCommand)

        // Register events
        server.pluginManager.registerEvents(AsyncTabCompleteListener(this), this)

        // Get Autorank data
        autorankData = AutorankData.parseAutorankData()
    }

    fun getMainCommand(): DataMigratorCommand {
        return mainCommand
    }

    fun getMainConfig(): MainConfig {
        return mainConfig
    }
}