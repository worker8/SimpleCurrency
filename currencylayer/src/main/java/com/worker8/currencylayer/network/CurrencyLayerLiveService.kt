package com.worker8.currencylayer.network

import com.worker8.currencylayer.BuildConfig
import com.worker8.currencylayer.model.Currency
import com.worker8.currencylayer.response.UsdCurrencyResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyLayerLiveService {
    @GET("api/live")
    fun getCurrencies(
        @Query("access_key") accessKey: String = BuildConfig.CURRENCY_LAYER_ACCCES_KEY,
        @Query("currencies") currencies: String = Currency.ALL_STRING,
        @Query("source") source: String = "USD",
        @Query("format") format: Int = 1
    ): Single<UsdCurrencyResponse>
}

//http://apilayer.net/api/live?
// access_key=d0bbf06c7ace118e5df7047d2e56ed6f
// &currencies=EUR,GBP,CAD,PLN,JPY
// &source=USD
// &format=1
