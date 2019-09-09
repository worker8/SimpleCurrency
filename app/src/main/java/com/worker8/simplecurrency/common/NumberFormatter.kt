package com.worker8.simplecurrency.common

import com.worker8.simplecurrency.common.extension.toComma

class NumberFormatter {
    companion object {
        fun addComma(s: String): String {
            val dotIndex = s.indexOf('.')
            return if (dotIndex == -1) {
                s.toDouble().toComma()
            } else {
                s.substring(0, dotIndex).toDouble().toComma() + s.substring(dotIndex)
            }
        }
    }
}
