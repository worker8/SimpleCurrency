package com.worker8.simplecurrency

import okhttp3.OkHttpClient

fun provideOkHttpClient() =
    OkHttpClient.Builder()
        .build()
