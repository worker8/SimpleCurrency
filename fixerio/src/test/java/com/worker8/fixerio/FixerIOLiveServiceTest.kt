package com.worker8.fixerio

import com.worker8.fixerio.network.FixerIOLiveService
import com.worker8.fixerio.network.FixerIORetrofit
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class FixerIOLiveServiceTest {
    private lateinit var service: FixerIOLiveService
    private lateinit var retrofit: Retrofit

    @Before
    fun setup() {
        retrofit = FixerIORetrofit.build()
        service = retrofit.create(FixerIOLiveService::class.java)
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
