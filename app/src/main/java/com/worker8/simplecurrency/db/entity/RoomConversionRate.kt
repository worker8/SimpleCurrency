package com.worker8.simplecurrency.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.worker8.currencylayer.model.ConversionRate

@Entity
data class RoomConversionRate(
    @PrimaryKey
    val code: String, val rate: Double
) {
    fun getCodeWithoutUSD(): String {
        return code.substring(3)
    }

    companion object {
        fun fromConversionRate(conversionRate: ConversionRate): RoomConversionRate {
            return RoomConversionRate(conversionRate.code, conversionRate.rate)
        }
    }
}
