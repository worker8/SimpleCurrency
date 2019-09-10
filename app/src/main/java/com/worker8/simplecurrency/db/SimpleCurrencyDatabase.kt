package com.worker8.simplecurrency.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.worker8.simplecurrency.db.dao.RoomConversionRateDao
import com.worker8.simplecurrency.db.dao.RoomUpdatedTimeStampDao
import com.worker8.simplecurrency.db.entity.RoomConversionRate
import com.worker8.simplecurrency.db.entity.RoomUpdatedTimeStamp

@Database(
    entities = [RoomConversionRate::class, RoomUpdatedTimeStamp::class],
    version = 1,
    exportSchema = false /* TODO: export schema when the app is launched */
)
abstract class SimpleCurrencyDatabase : RoomDatabase() {
    abstract fun roomConversionRateDao(): RoomConversionRateDao
    abstract fun roomUpdatedTimeStampDao(): RoomUpdatedTimeStampDao
}
