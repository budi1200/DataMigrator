package si.budimir.dataMigrator.commands.subcommands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.ktorm.database.Database
import si.budimir.dataMigrator.DataMigrator
import si.budimir.dataMigrator.MigrationHandler
import si.budimir.dataMigrator.MigrationJob
import si.budimir.dataMigrator.commands.SubCommandBase
import si.budimir.dataMigrator.database.DatabaseHelper
import si.budimir.dataMigrator.enums.Permission
import si.budimir.dataMigrator.util.MessageHelper

class MigrateSubCommand: SubCommandBase {
    private val plugin = DataMigrator.instance

    override fun execute(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 3 || args.size > 4) {
            return false
        }

        val onlinePlayerName = args[1]
        val oldPlayerName = args[2]
        val override = if (args.size == 4) {
            args[3] == "true"
        } else {
            false
        }

        val targetPlayer = Bukkit.getPlayer(onlinePlayerName)

        if (targetPlayer == null) {
            MessageHelper.sendMessage(sender, "Target player not online")
            return true
        }

        DatabaseHelper.isAccountMigrated(onlinePlayerName) { isMigrated ->
            if (isMigrated && !override) {
                MessageHelper.sendMessage(sender, "Player is already migrated, append true to command to override")
                return@isAccountMigrated
            }

            MigrationJob(targetPlayer, oldPlayerName, sender).runMigration()

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