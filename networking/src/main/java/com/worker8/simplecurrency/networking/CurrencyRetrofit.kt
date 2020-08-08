package com.worker8.simplecurrency.networking

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class CurrencyRetrofit {
    companion object {
        private const val BaseUrl = "https://simple-currency-backend.herokuapp.com/";

        fun build(
            moshi: Moshi = CurrencyMoshi.build(),
            okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
        ) =
            Retrofit.Builder()
                .baseUrl(BaseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
    }
}
