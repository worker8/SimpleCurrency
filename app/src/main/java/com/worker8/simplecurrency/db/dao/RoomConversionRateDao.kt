package com.worker8.simplecurrency.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.worker8.simplecurrency.db.entity.RoomConversionRate
import io.reactivex.Flowable

@Dao
interface RoomConversionRateDao : BaseDao<RoomConversionRate> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<RoomConversionRate>)

    @Query("SELECT * FROM RoomConversionRate WHERE code=:currencyCode")
    fun findConversionRate(currencyCode: String): List<RoomConversionRate>

    @Query("SELECT * FROM RoomConversionRate")
    fun getRoomConversionRateList(): List<RoomConversionRate>

    @Query("SELECT * FROM RoomConversionRate")
    fun getRoomConversionRateFlowable(): Flowable<List<RoomConversionRate>>
}
