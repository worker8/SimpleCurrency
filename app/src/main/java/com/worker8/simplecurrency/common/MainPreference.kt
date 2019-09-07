package com.worker8.simplecurrency.common

import android.content.Context

class MainPreference {
    companion object {
        private const val FIRST_TIME = "FIRST_TIME"
        private const val BASE_CURRENCY = "BASE_CURRENCY"
        private const val TARGET_CURRENCY = "TARGET_CURRENCY"

        fun setFirstTimeFalse(context: Context) =
            context.defaultPrefs().save(FIRST_TIME, false)

        fun getFirstTime(context: Context) =
            context.defaultPrefs().get(FIRST_TIME, true)

        fun setSelectedBaseCurrencyCode(context: Context, currencyCode: String) =
            context.defaultPrefs().save(BASE_CURRENCY, currencyCode)

        fun getSelectedBaseCurrencyCode(context: Context) =
            context.defaultPrefs().get(BASE_CURRENCY, "JPY")

        fun setSelectedTargetCurrencyCode(context: Context, currencyCode: String) =
            context.defaultPrefs().save(TARGET_CURRENCY, currencyCode)

        fun getSelectedTargetCurrencyCode(context: Context) =
            context.defaultPrefs().get(TARGET_CURRENCY, "USD")

    }
}
