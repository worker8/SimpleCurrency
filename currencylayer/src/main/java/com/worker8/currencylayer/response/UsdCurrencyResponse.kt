package com.worker8.currencylayer.response

import com.worker8.currencylayer.model.Quotes

data class UsdCurrencyResponse(
    val privacy: String,
    val quotes: Quotes,
    val source: String,
    val success: Boolean,
    val terms: String,
    val timestamp: Int
)
