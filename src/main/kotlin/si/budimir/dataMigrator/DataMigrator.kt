package si.budimir.dataMigrator

import org.bukkit.plugin.java.JavaPlugin
import si.budimir.dataMigrator.commands.AsyncTabCompleteListener
import si.budimir.dataMigrator.commands.DataMigratorCommand
import si.budimir.dataMigrator.config.MainConfig
import si.budimir.dataMigrator.config.MainConfigData
import si.budimir.dataMigrator.listeners.PlayerJoinListener
import si.budimir.dataMigrator.util.AutorankData
import si.budimir.dataMigrator.util.LuckPermsData
import si.budimir.dataMigrator.util.LuckPermsObject
import si.budimir.dataMigrator.util.MessageHelper

class DataMigrator: JavaPlugin() {
    lateinit var mainConfigObj: MainConfig
    lateinit var mainConfig: MainConfigData

    private lateinit var mainCommand: DataMigratorCommand
    lateinit var autorankData: Map<String, String>
    lateinit var luckpermsData: LuckPermsObject

    companion object {
        lateinit var instance: DataMigrator
    }

    override fun onEnable() {
        super.onEnable()

        // Set instance
        instance = this

        // Config init
        mainConfigObj = MainConfig(instance)
        mainConfig = mainConfigObj.getConfig()

        MessageHelper.load(this)

        // Init commands
        mainCommand = DataMigratorCommand()
        getCommand("dmig")?.setExecutor(mainCommand)

        // Register events
        server.pluginManager.registerEvents(AsyncTabCompleteListener(this), this)
        server.pluginManager.registerEvents(PlayerJoinListener(this), this)

        // Get Autorank data
        autorankData = AutorankData.parseAutorankData()

        // Get LuckPerms data
        luckpermsData = LuckPermsData.parseLuckPermsData()
    }

    fun getMainCommand(): DataMigratorCommand {
        return mainCommand
    }
}