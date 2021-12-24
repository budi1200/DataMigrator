package si.budimir.dataMigrator.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import si.budimir.dataMigrator.DataMigrator
import java.io.File
import java.io.FileInputStream

abstract class NicknameData {
    companion object {
        private val plugin = DataMigrator.instance

        fun parseNicknameData(): Map<String, String> {
            val file: FileInputStream

            try {
                file = File(plugin.dataFolder, "nicknames.json").inputStream()
            } catch (err: Exception) {
                plugin.logger.severe("Error opening nicknames data file!")
                err.printStackTrace()

                return mapOf()
            }

            val parsedData: List<NicknameDataObj> = try {
                val parser = Json { ignoreUnknownKeys = true }
                parser.decodeFromStream(file)
            } catch (err: Exception) {
                plugin.logger.severe("Error parsing nicknames data")
                err.printStackTrace()

                listOf()
            }

            return parsedData.associate { el -> el.id to el.f_nick }
        }
    }
}

@Serializable
data class NicknameDataObj(
    val id: String,
    val f_nick: String
)