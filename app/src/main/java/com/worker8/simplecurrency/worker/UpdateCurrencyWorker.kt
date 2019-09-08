package com.worker8.simplecurrency.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.worker8.currencylayer.network.CurrencyLayerLiveService
import com.worker8.simplecurrency.SimpleCurrencyApplication
import com.worker8.simplecurrency.db.SimpleCurrencyDatabase
import com.worker8.simplecurrency.db.entity.RoomConversionRate
import com.worker8.simplecurrency.di.DaggerAppComponent
import com.worker8.simplecurrency.ui.main.MainRepo
import retrofit2.Retrofit
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
        Log.d("ddw", "done work, unix time: ${response.timestamp}")
        // Indicate whether the task finished successfully with the Result
        return Result.success()
    }
}
