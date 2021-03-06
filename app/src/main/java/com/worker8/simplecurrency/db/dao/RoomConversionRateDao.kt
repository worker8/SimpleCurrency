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

    @Query("SELECT * FROM RoomConversionRate WHERE code=:currencyCode ORDER BY code ASC")
    fun findConversionRate(currencyCode: String): List<RoomConversionRate>

    @Query("SELECT * FROM RoomConversionRate")
    fun getRoomConversionRateList(): List<RoomConversionRate>

    @Query("SELECT * FROM RoomConversionRate WHERE code like :currencyCode OR name like :currenyName ORDER BY code ASC")
    fun findRoomConversionRateFlowable(
        currencyCode: String,
        currenyName: String
    ): Flowable<List<RoomConversionRate>>

    @Query("SELECT * FROM RoomConversionRate WHERE code=:currencyCode ORDER BY code ASC")
    fun findConversionRateFlowable(currencyCode: String): Flowable<List<RoomConversionRate>>
}
