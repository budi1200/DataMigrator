package si.budimir.dataMigrator.config

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import si.budimir.dataMigrator.DataMigrator


class MainConfig(private val plugin: DataMigrator) : ConfigBase(plugin, "config.yml") {
    fun getString(key: String): String {
        var value: String? = getConfig()!!.getString(key)

        if (value == null) {
            plugin.logger.warning("Missing config value for $key")
            value = ""
        }

        return value
    }

    fun getParsedString(key: String, placeholders: Map<String, String> = hashMapOf()): Component {
        return MiniMessage.markdown().parse(getString(key), placeholders).decoration(TextDecoration.ITALIC, false)
    }

    fun getParsedList(key: String, placeholders: Map<String, String> = hashMapOf()): ArrayList<Component> {
        val list = getList(key)
        val output = arrayListOf<Component>()

        list.forEach {
            output.add(
                Component.text("").decoration(TextDecoration.ITALIC, false)
                    .append(MiniMessage.markdown().parse(it, placeholders))
            )
        }

        return output
    }

    fun getBoolean(key: String): Boolean {
        return getConfig()!!.getBoolean(key)
    }

    fun getList(key: String): MutableList<String> {
        return getConfig()!!.getStringList(key)
    }

    fun getConfigurationSection(key: String): MutableMap<String, Any>? {
        return getConfig()!!.getConfigurationSection(key)?.getValues(false)
    }

    fun getInt(key: String): Int {
        return getConfig()!!.getInt(key)
    }
}