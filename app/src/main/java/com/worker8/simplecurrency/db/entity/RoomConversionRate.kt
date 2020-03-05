package com.worker8.simplecurrency.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.worker8.fixerio.model.ConversionRate
import com.worker8.fixerio.model.Currency

@Entity
data class RoomConversionRate(
    @PrimaryKey
    val code: String, val rate: Double, val name: String
) {
    companion object {
        fun fromConversionRate(conversionRate: ConversionRate): RoomConversionRate {
            return RoomConversionRate(
                conversionRate.code,
                conversionRate.rate,
                Currency.ALL[conversionRate.code] ?: ""
            )
        }
    }
}
