package si.budimir.dataMigrator.commands.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import si.budimir.dataMigrator.DataMigrator
import si.budimir.dataMigrator.commands.SubCommandBase
import si.budimir.dataMigrator.util.MessageHelper

class ReloadSubCommand: SubCommandBase {
    override fun execute(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val config = DataMigrator.instance.getMainConfig()
        config.reloadConfig()
        MessageHelper.sendMessage(sender as Player, "<green>Reload Complete!")
        return true
    }

    override fun getPermission(): String {
        return "datamigrator.admin"
    }

    override fun getDesc(): String {
        return "Reloads config"
    }
}