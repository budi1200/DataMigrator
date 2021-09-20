package si.budimir.dataMigrator.commands.subcommands

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import si.budimir.dataMigrator.DataMigrator
import si.budimir.dataMigrator.commands.SubCommandBase

class ReloadSubCommand: SubCommandBase {
    override fun execute(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val config = DataMigrator.instance.getMainConfig()
        config.reloadConfig()
        sender.sendMessage(MiniMessage.get().parse("<green>Reload Complete!"))
        return true
    }

    override fun getPermission(): String {
        return "bcapture.admin"
    }

    override fun getDesc(): String {
        return "Reloads config"
    }
}