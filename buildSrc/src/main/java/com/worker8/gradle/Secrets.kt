package com.worker8.gradle

import java.io.File
import java.util.*

object Secrets {
    private const val CURRENCY_LAYER_ACCESS_TOKEN = "CURRENCY_LAYER_ACCESS_TOKEN"
    val currencyLayerAccessToken: String by lazy {
        apiKeysProperties().getProperty(CURRENCY_LAYER_ACCESS_TOKEN)
    }

    private fun apiKeysProperties(): Properties {
        val filename = "api_keys.properties"
        val file = File(filename)
        if (!file.exists()) {
            throw Error(
                "You need to prepare a file called $filename in the project root directory.\n" +
                    "and contain the Currency Layer API Access Key.\n" +
                    "The content of the file should look something like:\n\n" +
                    "(project root)$ cat $filename\n" +
                    "$CURRENCY_LAYER_ACCESS_TOKEN=d028f7h3hd.....h73h7hcj83\n"
            )
        }
        return file.toProperties()
    }

    fun File.toProperties() = Properties().apply {
        if (this@toProperties.exists()) {
            load(reader())
        }
    }
}
