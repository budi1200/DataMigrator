package si.budimir.dataMigrator.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import si.budimir.dataMigrator.DataMigrator

abstract class MessageHelper {
    companion object {
        private lateinit var plugin: DataMigrator
        lateinit var pluginPrefix: Component

        fun load(plugin: DataMigrator) {
            this.plugin = plugin
            pluginPrefix = getParsedString(plugin.mainConfig.pluginPrefix)
        }

        private val miniMessage = MiniMessage.builder().markdown().build()

        fun reloadPrefix() {
            pluginPrefix = getParsedString(plugin.mainConfig.pluginPrefix)
        }

        // Send message with string from config
        fun sendMessage(player: CommandSender, key: String, placeholders: MutableMap<String, String> = hashMapOf(), prefix: Boolean = true) {
            var tmp = Component.text("")

            if (prefix) {
                tmp = tmp.append(pluginPrefix)
            }

            tmp = tmp.append(getParsedString(key, placeholders))

            player.sendMessage(tmp)
        }

        fun getParsedString(key: String, placeholders: Map<String, String> = hashMapOf()): Component {
            return Component
                .text("")
                .decoration(TextDecoration.ITALIC, false)
                .append(
                    miniMessage.parse(key, placeholders)
                )
        }
    }
}