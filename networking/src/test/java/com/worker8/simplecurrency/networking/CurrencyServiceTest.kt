package com.worker8.simplecurrency.networking

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class CurrencyServiceTest {
    private lateinit var service: CurrencyService
    private lateinit var retrofit: Retrofit

    @Before
    fun setup() {
        retrofit = CurrencyRetrofit.build()
        service = retrofit.create(CurrencyService::class.java)
    }

    @Test
    fun testCall() {
        val response = service.getCurrencies().blockingGet()
        response?.let {
            System.out.println(it)
            it.rates.conversionRates.forEach {
                System.out.println(it)
            }
        }
        Assert.assertNotNull(response)
    }
}
