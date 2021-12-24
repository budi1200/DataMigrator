package si.budimir.dataMigrator.commands.subcommands

import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import si.budimir.dataMigrator.DataMigrator
import si.budimir.dataMigrator.commands.SubCommandBase
import si.budimir.dataMigrator.database.DatabaseHelper
import si.budimir.dataMigrator.enums.Permission
import si.budimir.dataMigrator.util.MessageHelper
import si.budimir.dataMigrator.util.TimeUtils

class StatusSubCommand: SubCommandBase {
    private val plugin = DataMigrator.instance

    override fun execute(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        var targetPlayer = sender.name

        if (sender.hasPermission(Permission.ADMIN.getPerm()) && args.size == 2) {
            targetPlayer = args[1]
        }

        DatabaseHelper.getLatestPlayerMigrationAttempt(targetPlayer) {
            if (it == null) {
                MessageHelper.sendMessage(sender, plugin.mainConfig.lang.migrationDataMissing)
                return@getLatestPlayerMigrationAttempt
            }

            val migrationStatus = if (it.status) {
                "<green>Migrated"
            } else {
                "<red>Nisi Migrated!"
            }

            val message = StringBuilder()
                .append("\n  • <#bfbfbf>Ime: <white>${it.lastKnownName}")
                .append("\n  • <#bfbfbf>UUID: <white>${it.uuid}")
                .append("\n  • <#bfbfbf>Offline Ime: <white>${it.offlineName}")
                .append("\n  • <#bfbfbf>Offline UUID: <white>${it.offlineUUID}")
                .append("\n  • <#bfbfbf>Čas: <white>${TimeUtils.prettyPrintTimestamp(it.migrationTime)}")
                .append("\n  • <#bfbfbf>Status: $migrationStatus")
                .toString()

            MessageHelper.sendMessage(sender, message)
        }

        return true
    }

    override fun getPermission(): String {
        return Permission.STATUS.getPerm()
    }

    override fun getDesc(): String {
        return "Check migration status"
    }
}