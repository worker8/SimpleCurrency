package com.worker8.simplecurrency.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.worker8.simplecurrency.db.entity.RoomUpdatedTimeStamp

@Dao
interface RoomUpdatedTimeStampDao : BaseDao<RoomUpdatedTimeStamp> {
    @Query("SELECT * FROM RoomUpdatedTimeStamp WHERE id='1'")
    fun findLatestTimeStamp(): List<RoomUpdatedTimeStamp>
}
