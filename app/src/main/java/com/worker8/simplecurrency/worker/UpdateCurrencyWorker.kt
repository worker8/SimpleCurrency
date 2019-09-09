package com.worker8.simplecurrency.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.worker8.currencylayer.network.CurrencyLayerLiveService
import com.worker8.simplecurrency.SimpleCurrencyApplication
import com.worker8.simplecurrency.db.SimpleCurrencyDatabase
import com.worker8.simplecurrency.db.entity.RoomConversionRate
import com.worker8.simplecurrency.db.entity.RoomUpdatedTimeStamp
import com.worker8.simplecurrency.di.DaggerAppComponent
import javax.inject.Inject

class UpdateCurrencyWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    @Inject
    lateinit var service: CurrencyLayerLiveService
    @Inject
    lateinit var db: SimpleCurrencyDatabase

    override fun doWork(): Result {
        DaggerAppComponent.builder()
            .application(applicationContext as SimpleCurrencyApplication)
            .build()
            .inject(this)

        val response = service.getCurrencies()
            .blockingGet()

        val roomConversionRateList = response.quotes.conversionRates.map {
            RoomConversionRate.fromConversionRate(it)
        }
        db.roomConversionRateDao().insert(roomConversionRateList)
        db.roomUpdatedTimeStampDao()
            .insert(RoomUpdatedTimeStamp("1", response.timestamp))

        return Result.success()
    }
}
