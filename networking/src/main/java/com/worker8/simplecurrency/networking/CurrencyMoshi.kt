package com.worker8.simplecurrency.networking

import com.squareup.moshi.Moshi

class CurrencyMoshi {
    companion object {
        fun build(): Moshi = Moshi
            .Builder()
            .add(ConversionsFactory())
            .build()
    }
}
