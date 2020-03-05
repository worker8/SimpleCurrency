package com.worker8.fixerio.response

import com.worker8.fixerio.model.Rates

data class EuroCurrencyResponse(
    val success: Boolean,
    val timestamp: Long,
    val base: String,
    val date: String,
    val rates: Rates
)
