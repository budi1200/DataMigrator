package si.budimir.dataMigrator.config

import si.budimir.dataMigrator.DataMigrator

class MainConfig(plugin: DataMigrator) : ConfigBase<MainConfigData>(plugin, "config.conf", MainConfigData::class.java) {
}