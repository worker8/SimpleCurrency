package com.worker8.simplecurrency.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomConversionRate(
    @PrimaryKey
    val code: String, val rate: Double
)
