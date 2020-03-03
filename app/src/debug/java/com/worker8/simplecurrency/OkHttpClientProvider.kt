package com.worker8.simplecurrency

import okhttp3.OkHttpClient
import com.facebook.stetho.okhttp3.StethoInterceptor

fun provideOkHttpClient() =
    OkHttpClient.Builder()
        .addNetworkInterceptor(StethoInterceptor())
        .build()
