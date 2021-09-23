package si.budimir.dataMigrator.util

import si.budimir.dataMigrator.DataMigrator
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.util.*

abstract class AutorankData {

    companion object {
        private val plugin = DataMigrator.instance

        fun parseAutorankData(): Map<String, String> {
            val file: FileInputStream

            try {
                 file = File(plugin.dataFolder, "autorank.yml").inputStream()
            } catch (err: Exception) {
                plugin.logger.severe("Error opening autorank data file!")
                err.printStackTrace()

                return mutableMapOf()
            }

            val props = Properties()

            file.use { stream ->
                props.load(stream)
            }

            return props.map { (key, value) -> key.toString() to value.toString() }.toMap()
        }
    }
}