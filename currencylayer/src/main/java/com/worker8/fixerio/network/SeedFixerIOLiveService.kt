package com.worker8.fixerio.network

import com.squareup.moshi.Moshi
import com.worker8.fixerio.model.Rates
import com.worker8.fixerio.response.EuroCurrencyResponse
import java.io.BufferedReader

class SeedFixerIOLiveService(val moshi: Moshi) {
    private val json: String by lazy {
        val inputStream =
            javaClass.classLoader.getResourceAsStream("usd_based_currencies_fixerio.json")
        inputStream.bufferedReader().use(BufferedReader::readText)
    }

    fun getSeedCurrencies(): Pair<Rates, Long> {
        val jsonAdapter = moshi.adapter(EuroCurrencyResponse::class.java)
        val response = jsonAdapter.fromJson(json)
        val rates = response?.rates ?: Rates()
        val timestamp = response?.timestamp ?: 0
        return rates to timestamp
    }
}
