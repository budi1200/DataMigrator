package si.budimir.dataMigrator.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import si.budimir.dataMigrator.commands.subcommands.ReloadSubCommand
import si.budimir.dataMigrator.DataMigrator
import si.budimir.dataMigrator.commands.subcommands.MigrateSubCommand
import si.budimir.dataMigrator.util.Lang
import si.budimir.dataMigrator.util.MessageHelper

class DataMigratorCommand : CommandExecutor, Listener {
    private val subCommands: MutableMap<String, SubCommandBase> = HashMap()
    private var subCommandsList: List<String> = emptyList()
    private val plugin = DataMigrator.instance
    private val config = plugin.getMainConfig()

    init {
        subCommands["reload"] = ReloadSubCommand()
        subCommands["migrate"] = MigrateSubCommand()

        subCommandsList = subCommands.keys.toList()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if (args.isNotEmpty()) run {
            val sc: SubCommandBase = subCommands[args[0]] ?: return false
            val reqPerm: String = sc.getPermission()

            if (reqPerm == "" || sender.hasPermission(reqPerm)) {
                sc.execute(sender, command, label, args)
            } else {
                MessageHelper.sendMessage(sender as Player, Lang.MISSING_PERMISSION.path, mutableMapOf())
                plugin.logger.info("${sender.name} is missing permission $reqPerm")
            }
        } else {
            // TODO: Base command
        }

        return true
    }

    fun getSubCommandList(): List<String> {
        return subCommandsList
    }

    fun getSubCommands(): MutableMap<String, SubCommandBase> {
        return subCommands
    }
}