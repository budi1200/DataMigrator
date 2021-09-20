package si.budimir.dataMigrator.commands

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import si.budimir.dataMigrator.DataMigrator

class AsyncTabCompleteListener(private val plugin: DataMigrator): Listener {

    private val mainCommand = plugin.getMainCommand()
    private val subCommands = mainCommand.getSubCommands()
    private val subCommandsList = mainCommand.getSubCommandList()

    @EventHandler
    fun onAsyncTabComplete(event: AsyncTabCompleteEvent) {
        val buffer = event.buffer

        if ((!event.isCommand || !buffer.startsWith("/dmig")) || buffer.indexOf(' ') == -1 ) {
            return
        }

        var completions: List<String> = getCompletions(buffer, event.sender)

        // if we have no completion data, client will display an error, lets just send a space instead (https://bugs.mojang.com/browse/MC-165562)
        if (completions.size == 1 && completions[0] == "") {
            completions = listOf(" ")
        }
        event.completions = completions
        event.isHandled = true
    }

    private fun getCompletions(buffer: String, sender: CommandSender): List<String> {
        val args = buffer.split(" ").drop(1)

        return when {
            args[0] == "" -> {
                subCommandsList
            }
            args.size == 1 -> {
                subCommandsList.filter { it.contains(args[0], ignoreCase = true) }
            }
            else -> {
                val sc: SubCommandBase = subCommands[args[0]] ?: return emptyList()
                return sc.onTabComplete(sender, args)
            }
        }
    }
}