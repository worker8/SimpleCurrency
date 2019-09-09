package com.worker8.currencylayer.network

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


class CurrencyLayerRetrofit {
    companion object {
        private const val BaseUrl = "http://apilayer.net/"
        fun build(
            moshi: Moshi = CurrencyLayerMoshi.build(),
            okHttpClient: OkHttpClient =
                OkHttpClient.Builder().build()
        ) =
            Retrofit.Builder()
                .baseUrl(BaseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
    }
}

