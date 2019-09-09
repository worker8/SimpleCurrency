package com.worker8.simplecurrency.ui.main

import android.content.Context
import androidx.work.*
import com.squareup.moshi.Moshi
import com.worker8.currencylayer.network.SeedCurrencyLayerLiveService
import com.worker8.simplecurrency.common.MainPreference
import com.worker8.simplecurrency.common.SchedulerSharedRepo
import com.worker8.simplecurrency.db.SimpleCurrencyDatabase
import com.worker8.simplecurrency.db.entity.RoomConversionRate
import com.worker8.simplecurrency.db.entity.RoomUpdatedTimeStamp
import com.worker8.simplecurrency.di.scope.PerActivityScope
import com.worker8.simplecurrency.worker.UpdateCurrencyWorker
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@PerActivityScope
class MainRepo @Inject constructor(
    private val context: Context,
    val db: SimpleCurrencyDatabase,
    val moshi: Moshi,
    val workManager: WorkManager,
    val schedulerSharedRepo: SchedulerSharedRepo
) {
    fun populateDbIfFirstTime(): Observable<Boolean> {
        return Observable.fromCallable {
            if (MainPreference.getFirstTime(context)) {
                // populate
                val (quotes, timestamp) = SeedCurrencyLayerLiveService(moshi).getSeedCurrencies()
                val roomConversionRateList = quotes.conversionRates.map {
                    RoomConversionRate.fromConversionRate(it)
                }
                db.roomConversionRateDao().insert(roomConversionRateList)
                db.roomUpdatedTimeStampDao().insert(RoomUpdatedTimeStamp("1", timestamp))
                return@fromCallable MainPreference.setFirstTimeFalse(context)
            } else {
                return@fromCallable true
            }
        }
    }

    fun getSelectedBaseCurrencyCode() =
        MainPreference.getSelectedBaseCurrencyCode(context)

    fun getSelectedTargetCurrencyCode() =
        MainPreference.getSelectedTargetCurrencyCode(context)

    fun setSelectedBaseCurrencyCode(currencyCode: String) =
        MainPreference.setSelectedBaseCurrencyCode(context, currencyCode)

    fun setSelectedTargetCurrencyCode(currencyCode: String) =
        MainPreference.setSelectedTargetCurrencyCode(context, currencyCode)

    fun getBaseRateFlowable(): Flowable<List<RoomConversionRate>> {
        val baseCurrency = getSelectedBaseCurrencyCode() // "JPY"
        return db.roomConversionRateDao().findConversionRateFlowable("USD${baseCurrency}")
    }

    fun getTargetRateFlowable(): Flowable<List<RoomConversionRate>> {
        val targetCurrency = getSelectedTargetCurrencyCode() // "JPY"
        return db.roomConversionRateDao().findConversionRateFlowable("USD${targetCurrency}")
    }

    fun getLatestSelectedRateFlowable(): Flowable<Double> {
        return Flowable.combineLatest(
            getBaseRateFlowable(),
            getTargetRateFlowable(),
            BiFunction<List<RoomConversionRate>, List<RoomConversionRate>, Double> { baseRateList, targetRateList ->
                val rate = if (baseRateList.isNotEmpty() && targetRateList.isNotEmpty()) {
                    targetRateList.first().rate / baseRateList.first().rate
                } else {
                    -1.0
                }
                return@BiFunction rate
            })
    }

    fun setupPeriodicUpdate() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED).build()
        val updateCurrencyWorker =
            PeriodicWorkRequest.Builder(UpdateCurrencyWorker::class.java, 30, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
        workManager.enqueueUniquePeriodicWork(
            uniqueWorkerName,
            ExistingPeriodicWorkPolicy.KEEP,
            updateCurrencyWorker
        )
        /* uncomment the following for testing purpose */
//        val oneTimeCurrencyWorker = OneTimeWorkRequest.Builder(UpdateCurrencyWorker::class.java)
//            .setConstraints(constraints)
//            .build()
//        workManager.enqueue(oneTimeCurrencyWorker)
    }

    companion object {
        val uniqueWorkerName = "get_latest_currency"
    }
}
