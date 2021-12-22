package si.budimir.dataMigrator.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromStream
import si.budimir.dataMigrator.DataMigrator
import java.io.File
import java.io.FileInputStream

@Suppress("EXPERIMENTAL_API_USAGE")
abstract class LuckPermsData {
    companion object {
        private val plugin = DataMigrator.instance

        fun parseLuckPermsData(): LuckPermsObject {
            val file: FileInputStream

            try {
                file = File(plugin.dataFolder, "luckperms.json").inputStream()
            } catch (err: Exception) {
                plugin.logger.severe("Error opening luckperms data file!")
                err.printStackTrace()

                return LuckPermsObject(hashMapOf())
            }

            return try {
                val parser = Json { ignoreUnknownKeys = true }
                parser.decodeFromStream(file)
            } catch (err: Exception) {
                plugin.logger.severe("Error parsing LuckPerms data")
                err.printStackTrace()

                LuckPermsObject(hashMapOf())
            }
        }
    }
}

@Serializable
data class LuckPermsObject(
    val users: HashMap<String, LuckPermsUser>
)

@Serializable
data class LuckPermsUser(
    val username: String,
    val primaryGroup: String? = null,
    val nodes: ArrayList<LuckPermsNode>
)

@Serializable
data class LuckPermsNode(
    val type: String,
    var key: String,
    val value: Boolean,
    val context: HashMap<String, JsonElement> = hashMapOf()
)