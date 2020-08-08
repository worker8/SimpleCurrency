package com.worker8.simplecurrency.networking

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {
    @GET("currency")
    fun getCurrencies(): Single<CurrencyResponse>
}
