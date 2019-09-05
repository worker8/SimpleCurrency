package com.worker8.simplecurrency

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.worker8.simplecurrency.db.SimpleCurrencyDatabase
import com.worker8.simplecurrency.db.dao.RoomConversionRateDao
import com.worker8.simplecurrency.db.entity.RoomConversionRate
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomConversionRateDaoTest() {
    private lateinit var roomConversionRateDao: RoomConversionRateDao
    private lateinit var db: SimpleCurrencyDatabase
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SimpleCurrencyDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        roomConversionRateDao = db.roomConversionRateDao()
    }

    @Test
    fun crudTest() {
        roomConversionRateDao.insert(
            RoomConversionRate("USDJPY", 106.25984),
            RoomConversionRate("USDKES", 103.910315),
            RoomConversionRate("USDKGS", 69.85001),
            RoomConversionRate("USDKHR", 4089.999831),
            RoomConversionRate("USDKMF", 447.050295)
        )

        val list = roomConversionRateDao.getRoomConversionRateList()
        Assert.assertNotNull(list)
        Assert.assertEquals(5, list.size)

        roomConversionRateDao.delete(RoomConversionRate("USDJPY", 106.25984))
        val deletedList = roomConversionRateDao.getRoomConversionRateList()
        Assert.assertEquals(4, deletedList.size)

        val foundList = roomConversionRateDao.findConversionRate("USDKMF")
        Assert.assertEquals(1, foundList.size)
    }
}

