package com.worker8.simplecurrency.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.worker8.simplecurrency.db.entity.RoomConversionRate
import io.reactivex.Flowable

@Dao
interface RoomConversionRateDao : BaseDao<RoomConversionRate> {
    @Query("SELECT * FROM RoomConversionRate WHERE code=:currencyCode")
    fun findConversionRate(currencyCode: String): List<RoomConversionRate>

    @Query("SELECT * FROM RoomConversionRate")
    fun getRoomConversionRateList(): List<RoomConversionRate>

    @Query("SELECT * FROM RoomConversionRate")
    fun getRoomConversionRateFlowable(): Flowable<List<RoomConversionRate>>
}
