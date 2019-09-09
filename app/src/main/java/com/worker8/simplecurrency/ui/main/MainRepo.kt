package com.worker8.simplecurrency.ui.main

import android.content.Context
import androidx.work.*
import com.squareup.moshi.Moshi
import com.worker8.currencylayer.network.SeedCurrencyLayerLiveService
import com.worker8.simplecurrency.common.MainPreference
import com.worker8.simplecurrency.db.SimpleCurrencyDatabase
import com.worker8.simplecurrency.db.entity.RoomConversionRate
import com.worker8.simplecurrency.db.entity.RoomUpdatedTimeStamp
import com.worker8.simplecurrency.di.scope.ScopeConstant
import com.worker8.simplecurrency.worker.UpdateCurrencyWorker
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

interface MainRepoInterface {
    val mainThread: Scheduler
    val backgroundThread: Scheduler
    fun populateDbIfFirstTime(): Observable<Boolean>
    fun getSelectedBaseCurrencyCode(): String
    fun getSelectedTargetCurrencyCode(): String
    fun setSelectedBaseCurrencyCode(currencyCode: String): Boolean
    fun setSelectedTargetCurrencyCode(currencyCode: String): Boolean
    fun getLatestSelectedRateFlowable(): Flowable<Double>
    fun setupPeriodicUpdate()
}

class MainRepo @Inject constructor(
    private val context: Context,
    val db: SimpleCurrencyDatabase,
    private val moshi: Moshi,
    private val workManager: WorkManager,
    @Named(ScopeConstant.MainThreadScheduler)
    override val mainThread: Scheduler,
    @Named(ScopeConstant.BackgroundThreadScheduler)
    override val backgroundThread: Scheduler
) : MainRepoInterface {

    override fun populateDbIfFirstTime(): Observable<Boolean> {
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

    override fun getSelectedBaseCurrencyCode() =
        MainPreference.getSelectedBaseCurrencyCode(context)

    override fun getSelectedTargetCurrencyCode() =
        MainPreference.getSelectedTargetCurrencyCode(context)

    override fun setSelectedBaseCurrencyCode(currencyCode: String) =
        MainPreference.setSelectedBaseCurrencyCode(context, currencyCode)

    override fun setSelectedTargetCurrencyCode(currencyCode: String) =
        MainPreference.setSelectedTargetCurrencyCode(context, currencyCode)

    private fun getBaseRateFlowable(): Flowable<List<RoomConversionRate>> {
        val baseCurrency = getSelectedBaseCurrencyCode() // "JPY"
        return db.roomConversionRateDao().findConversionRateFlowable("USD$baseCurrency")
    }

    private fun getTargetRateFlowable(): Flowable<List<RoomConversionRate>> {
        val targetCurrency = getSelectedTargetCurrencyCode() // "JPY"
        return db.roomConversionRateDao().findConversionRateFlowable("USD$targetCurrency")
    }

    override fun getLatestSelectedRateFlowable(): Flowable<Double> {
        return Flowable.combineLatest(
            getBaseRateFlowable(),
            getTargetRateFlowable(),
            BiFunction<List<RoomConversionRate>, List<RoomConversionRate>, Double> { baseRateList, targetRateList ->
                return@BiFunction if (baseRateList.isNotEmpty() && targetRateList.isNotEmpty()) {
                    targetRateList.first().rate / baseRateList.first().rate
                } else {
                    -1.0
                }
            })
    }

    override fun setupPeriodicUpdate() {
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
        const val uniqueWorkerName = "get_latest_currency"
    }
}
