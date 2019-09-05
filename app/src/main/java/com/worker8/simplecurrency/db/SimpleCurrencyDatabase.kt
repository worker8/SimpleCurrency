package com.worker8.simplecurrency.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.worker8.simplecurrency.db.dao.RoomConversionRateDao
import com.worker8.simplecurrency.db.entity.RoomConversionRate


@Database(
    entities = [RoomConversionRate::class],
    version = 1
)
abstract class SimpleCurrencyDatabase : RoomDatabase() {
    abstract fun roomConversionRateDao(): RoomConversionRateDao
}
