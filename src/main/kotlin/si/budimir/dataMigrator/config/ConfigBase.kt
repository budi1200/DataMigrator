package si.budimir.dataMigrator.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

open class ConfigBase(plugin: JavaPlugin, _fileName: String) {
    private val main: JavaPlugin = plugin

    private var mainConfig: FileConfiguration? = null
    private var configFile: File? = null
    private val fileName: String = _fileName
    private val logger = main.logger

    fun reloadConfig(): Boolean {
        try{
            if (!main.dataFolder.exists() || !configFile!!.exists()){
                logger.warning("Config file not found, making a new one")
                saveDefaultConfig()
            }

            if (configFile == null){
                logger.warning("CONFIG NULL")
                configFile = File(main.dataFolder, fileName)
            }

            mainConfig = YamlConfiguration.loadConfiguration(configFile!!)
            return true
        }catch (e: IOException){
            logger.severe("Failed to reload config!")
            return false
        }
    }

    protected fun getConfig(): FileConfiguration? {
        if (mainConfig == null) reloadConfig()
        return mainConfig
    }

    private fun saveDefaultConfig() {
        if (!main.dataFolder.exists())
            main.dataFolder.mkdir()

        if (configFile == null)
            configFile = File(main.dataFolder, fileName)

        if (!configFile!!.exists()) {
            main.saveResource(fileName, false)
        }
    }

    init {
        saveDefaultConfig()
    }
}