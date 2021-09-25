package si.budimir.dataMigrator.commands.subcommands

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
        val migResult = MigrationHandler.migratePlayer("budi1200")
        if (migResult) {
            MessageHelper.sendMessage(sender as Player, Lang.MIGRATION_SUCCESS.path, mutableMapOf())
        } else {
            MessageHelper.sendMessage(sender as Player, Lang.MIGRATION_FAIL.path, mutableMapOf())
        }
        return true
    }

    override fun getPermission(): String {
        return Permission.ADMIN.getPerm()
    }

    override fun getDesc(): String {
        return "Manually attempt user migration"
    }
}