package si.budimir.dataMigrator.commands.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import si.budimir.dataMigrator.DataMigrator
import si.budimir.dataMigrator.commands.SubCommandBase
import si.budimir.dataMigrator.enums.Permission
import si.budimir.dataMigrator.util.AutorankData
import si.budimir.dataMigrator.util.LuckPermsData
import si.budimir.dataMigrator.util.MessageHelper

class ReloadSubCommand: SubCommandBase {
    override fun execute(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val plugin = DataMigrator.instance

        plugin.mainConfigObj.reloadConfig()
        plugin.mainConfig = plugin.mainConfigObj.getConfig()
        plugin.autorankData = AutorankData.parseAutorankData()
        plugin.luckpermsData = LuckPermsData.parseLuckPermsData()
        MessageHelper.reloadPrefix()

        MessageHelper.sendMessage(sender, "<green>Reload Complete!")
        return true
    }

    override fun getPermission(): String {
        return Permission.ADMIN.getPerm()
    }

    override fun getDesc(): String {
        return "Reloads config"
    }
}