package si.budimir.dataMigrator.util

import si.budimir.dataMigrator.DataMigrator

class Logger {
    private val plugin = DataMigrator.instance
    private var logs = ""

    fun addLog(message: String) {
        plugin.logger.info(message)
        logs += message + "\n"
    }

    fun getLogs(): String {
        return logs;
    }
}