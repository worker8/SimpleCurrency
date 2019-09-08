package com.worker8.currencylayer.model

data class ConversionRate(val code: String, val rate: Double) {
    fun getCodeWithoutUSD(): String {
        return code.substring(3)
    }
}
