package com.worker8.gradle

import java.io.File
import java.util.*

object Secrets {
    private const val FIXER_IO_ACCESS_TOKEN = "FIXER_IO_ACCESS_TOKEN"
    val ENV by lazy {
        System.getenv()
    }
    val fixerIOAccessToken: String by lazy {
        var fixerIOToken = ""
        System.out.println("----------------ENV--------------")
        System.out.println(ENV)
        System.out.println("---------------------------------")
        if (isGithubActionsCI()) {
            // running on: Github Actions CI
            if (!ENV.containsKey(FIXER_IO_ACCESS_TOKEN)) {
                throw Error(
                    "If you are running Github Actions,\n" +
                        "You need to define a FIXER_IO_ACCESS_TOKEN environment variable.\n"
                )
            }
            fixerIOToken = ENV.get(FIXER_IO_ACCESS_TOKEN) ?: ""
        } else {
            // running on: local
            fixerIOToken = apiKeysProperties().getProperty(FIXER_IO_ACCESS_TOKEN)
        }
        fixerIOToken
    }

    private fun isGithubActionsCI() = ENV.containsKey("GITHUB_ACTIONS")

    private fun apiKeysProperties(): Properties {
        val filename = "api_keys.properties"
        val file = File(filename)
        if (!file.exists()) {
            throw Error(
                "You need to prepare a file called $filename in the project root directory.\n" +
                    "and contain the Fixer IO API Access Key.\n" +
                    "The content of the file should look something like:\n\n" +
                    "(project root)$ cat $filename\n" +
                    "$FIXER_IO_ACCESS_TOKEN=360f....17bf\n"
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
