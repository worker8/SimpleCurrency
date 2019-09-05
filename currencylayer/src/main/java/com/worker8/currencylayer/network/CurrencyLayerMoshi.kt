package com.worker8.currencylayer.network

import com.squareup.moshi.Moshi
import com.worker8.currencylayer.adapter.ConversionsFactory

class CurrencyLayerMoshi {
    companion object {
        fun build() = Moshi
            .Builder()
            .add(ConversionsFactory())
            .build()
    }
}
