package si.budimir.dataMigrator.util

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import si.budimir.dataMigrator.DataMigrator
import java.io.IOException

class WebHookHandler {
    companion object {
        var client: OkHttpClient = OkHttpClient()

        fun send(data: String, url: String) {
            val body: RequestBody = data.toRequestBody("application/json".toMediaType())
            val request: Request = Request.Builder().url(url).post(body).build()
            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    DataMigrator.instance.logger.info("Failed to send Discord embed")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code != 204 && response.code != 200) {
                        DataMigrator.instance.logger.info("Failed to send Discord embed - ${response.code}")
                    }
                }
            })
        }
    }
}