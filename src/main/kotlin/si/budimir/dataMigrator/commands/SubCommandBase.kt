package si.budimir.dataMigrator.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

interface SubCommandBase {

    fun execute(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean

    fun onTabComplete(sender: CommandSender, args: List<String>): List<String> {
        return emptyList()
    }

    fun getPermission(): String

    fun getDesc(): String
}