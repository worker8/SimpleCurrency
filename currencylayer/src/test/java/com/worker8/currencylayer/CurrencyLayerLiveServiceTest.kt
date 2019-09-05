package com.worker8.currencylayer

import com.worker8.currencylayer.network.CurrencyLayerLiveService
import com.worker8.currencylayer.network.CurrencyLayerRetrofit
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class CurrencyLayerLiveServiceTest {
    lateinit var service: CurrencyLayerLiveService
    lateinit var retrofit: Retrofit

    @Before
    fun setup() {
        retrofit = CurrencyLayerRetrofit.build()
        service = retrofit.create(CurrencyLayerLiveService::class.java)
    }

    @Test
    fun testCall() {
        val response = service.getCurrencies().blockingGet()
        Assert.assertNotNull(response)
    }
}
