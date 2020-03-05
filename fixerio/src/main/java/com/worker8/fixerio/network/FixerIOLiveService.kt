package com.worker8.fixerio.network

import com.worker8.fixerio.BuildConfig
import com.worker8.fixerio.response.EuroCurrencyResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface FixerIOLiveService {
    @GET(BuildConfig.CURRENCY_API_URL_PATH)
    fun getCurrencies(
        @Query("access_key") accessKey: String = BuildConfig.FIXER_IO_ACCCES_KEY,
        @Query("base") source: String = "EUR",
        @Query("format") format: Int = 1
    ): Single<EuroCurrencyResponse>
}
