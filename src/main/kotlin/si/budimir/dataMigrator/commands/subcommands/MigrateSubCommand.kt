package si.budimir.dataMigrator.commands.subcommands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import si.budimir.dataMigrator.MigrationHandler
import si.budimir.dataMigrator.commands.SubCommandBase
import si.budimir.dataMigrator.enums.Lang
import si.budimir.dataMigrator.enums.Permission
import si.budimir.dataMigrator.util.MessageHelper

class MigrateSubCommand: SubCommandBase {
    override fun execute(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 2 || args.size > 3) {
            return false
        }

        val onlinePlayerName = args[1]
        val oldPlayerName = if (args.size != 3) {
            null
        } else {
            args[2]
        }

        if (sender.hasPermission(Permission.MIGRATED.getPerm())) {
            MessageHelper.sendMessage(sender as Player, Lang.ALREADY_MIGRATED)
            return true
        }

        val migResult = MigrationHandler.migratePlayer(onlinePlayerName, oldPlayerName)

        if (migResult) {
            MessageHelper.sendMessage(sender as Player, Lang.MIGRATION_SUCCESS)
        } else {
            MessageHelper.sendMessage(sender as Player, Lang.MIGRATION_FAIL)
        }
        return true
    }

    override fun getPermission(): String {
        return Permission.ADMIN.getPerm()
    }

    override fun getDesc(): String {
        return "Manually attempt user migration"
    }

    override fun onTabComplete(sender: CommandSender, args: List<String>): List<String> {
        val result = mutableListOf<String>()

        if (args.size == 2 && args[1].length < 2) {
            result.add("<current name> <old name>")
        }

        if (args.size == 3 && args[2].length < 2) {
            result.add("<old name>")
        }

        return result
    }
}