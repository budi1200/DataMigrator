package si.budimir.dataMigrator.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import si.budimir.dataMigrator.DataMigrator

abstract class MessageHelper {
    companion object {
        private val plugin: DataMigrator = DataMigrator.instance
        private val config = plugin.getMainConfig()
        private val pluginPrefix = config.getParsedString("pluginPrefix")

        // Send message with string from config
        fun sendMessage(player: Player, key: String, placeholders: MutableMap<String, String>, prefix: Boolean = true) {
            var tmp = Component.text("")

            if (prefix) {
                tmp = tmp.append(pluginPrefix)
            }

            tmp = tmp.append(config.getParsedString(key, placeholders))

            player.sendMessage(tmp)
        }

        // Send message with provided string
        fun sendMessage(player: Player, message: String, prefix: Boolean = true) {
            var tmp = Component.text("")

            if (prefix) {
                tmp = tmp.append(pluginPrefix)
            }

            tmp = tmp.append(MiniMessage.markdown().parse(message))

            player.sendMessage(tmp)
        }
    }
}