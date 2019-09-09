package com.worker8.simplecurrency.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomUpdatedTimeStamp(
    @PrimaryKey val id: String,
    /* TimeStamp from the API*/
    val timeStamp: Long,
    val updatedAt: Long = System.currentTimeMillis() / 1000
)
