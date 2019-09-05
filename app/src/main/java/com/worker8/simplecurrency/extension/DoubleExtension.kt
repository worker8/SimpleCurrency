package com.worker8.simplecurrency.extension

import java.math.RoundingMode
import java.text.DecimalFormat

fun Double.roundOffDecimal(): Double? {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toDouble()
}
