package com.worker8.currencylayer

import com.squareup.moshi.Moshi
import com.worker8.currencylayer.response.UsdCurrencyResponse
import org.junit.Assert
import org.junit.Test
import java.io.BufferedReader

class CurrencyLayerMoshiTest {
    lateinit var moshi: Moshi
    val json: String by lazy {
        val inputStream = javaClass.classLoader.getResourceAsStream("usd_based_currencies.json")
        inputStream.bufferedReader().use(BufferedReader::readText)
    }

    @Test
    fun testCall() {
        val jsonAdapter = moshi.adapter(UsdCurrencyResponse::class.java)
        val response = jsonAdapter.fromJson(json)
        Assert.assertNotNull(response)
    }
}
