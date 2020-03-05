package com.worker8.fixerio.network

import com.squareup.moshi.Moshi
import com.worker8.fixerio.adapter.ConversionsFactory

class FixerIOMoshi {
    companion object {
        fun build(): Moshi = Moshi
            .Builder()
            .add(ConversionsFactory())
            .build()
    }
}
