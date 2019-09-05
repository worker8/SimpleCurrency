package com.worker8.currencylayer.network

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class CurrencyLayerRetrofit {
    companion object {
        private const val BaseUrl = "http://apilayer.net/"
        fun build(moshi: Moshi = CurrencyLayerMoshi.build()) = Retrofit.Builder()
            .baseUrl(BaseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}

