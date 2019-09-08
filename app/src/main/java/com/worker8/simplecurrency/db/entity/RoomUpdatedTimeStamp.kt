package com.worker8.simplecurrency.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomUpdatedTimeStamp(@PrimaryKey val id: String, val unixTime: Long)
