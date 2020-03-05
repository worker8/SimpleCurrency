package com.worker8.fixerio

import com.squareup.moshi.Moshi
import com.worker8.fixerio.model.Currency
import com.worker8.fixerio.network.FixerIOMoshi
import com.worker8.fixerio.response.EuroCurrencyResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.BufferedReader

class FixerIOMoshiTest {
    private lateinit var moshi: Moshi
    private val json: String by lazy {
        val inputStream =
            javaClass.classLoader.getResourceAsStream("usd_based_currencies_fixerio.json")
        inputStream.bufferedReader().use(BufferedReader::readText)
    }

    @Before
    fun setup() {
        moshi = FixerIOMoshi.build()
    }

    @Test
    fun testCall() {
        val jsonAdapter = moshi.adapter(EuroCurrencyResponse::class.java)
        val response = jsonAdapter.fromJson(json)
        response?.let {
            System.out.println(it)
            it.rates.conversionRates.forEach {
                System.out.println(it)
            }
        }
        Assert.assertNotNull(response)
    }

    @Test
    fun testTest() {
        System.out.println("Currency.ALL.count: ${Currency.ALL.count()}")
        System.out.println("Currency.ALL.TEMP: ${Currency.TEMP.count()}")
        var count = 0
        Currency.ALL.forEach { (code, name) ->
            if (Currency.TEMP.contains(code)) {
                count++
            }
        }

        System.out.println("Total match: ${count}")
    }
}
