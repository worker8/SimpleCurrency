package com.worker8.simplecurrency.common

import android.content.Context

class MainPreference {
    companion object {
        private const val FIRST_TIME = "FIRST_TIME"

        fun setFirstTimeFalse(context: Context) {
            context.defaultPrefs().save(FIRST_TIME, false)
        }

        fun getFirstTime(context: Context): Boolean {
            return context.defaultPrefs().get(FIRST_TIME, true)
        }
    }
}
