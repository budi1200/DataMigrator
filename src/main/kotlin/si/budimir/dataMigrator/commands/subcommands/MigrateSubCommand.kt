package si.budimir.dataMigrator.commands.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import si.budimir.dataMigrator.DataMigrator
import si.budimir.dataMigrator.MigrationHandler
import si.budimir.dataMigrator.commands.SubCommandBase
import si.budimir.dataMigrator.util.MessageHelper

class MigrateSubCommand: SubCommandBase {
    private val plugin = DataMigrator.instance

    override fun execute(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val migResult = MigrationHandler.migratePlayer("budi1200")
        if (migResult) {
            // TODO
        } else {
            // TODO
        }
        return true
    }

    override fun getPermission(): String {
        return "datamigrator.admin"
    }

    override fun getDesc(): String {
        return "Manually attempt user migration"
    }
}