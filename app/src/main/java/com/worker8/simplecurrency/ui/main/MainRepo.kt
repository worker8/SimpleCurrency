package com.worker8.simplecurrency.ui.main

import android.content.Context
import android.util.Log
import com.squareup.moshi.Moshi
import com.worker8.currencylayer.network.SeedCurrencyLayerLiveService
import com.worker8.simplecurrency.common.MainPreference
import com.worker8.simplecurrency.common.SchedulerSharedRepo
import com.worker8.simplecurrency.db.SimpleCurrencyDatabase
import com.worker8.simplecurrency.db.entity.RoomConversionRate
import com.worker8.simplecurrency.di.scope.PerActivityScope
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

@PerActivityScope
class MainRepo @Inject constructor(
    private val context: Context,
    val db: SimpleCurrencyDatabase,
    val moshi: Moshi,
    val schedulerSharedRepo: SchedulerSharedRepo
) {
    fun populateDbIfFirstTime(): Observable<Boolean> {
        return Observable.fromCallable {
            if (MainPreference.getFirstTime(context)) {
                // populate
                val quotes = SeedCurrencyLayerLiveService(moshi).getSeedCurrencies()
                val roomConversionRateList = quotes.conversionRates.map {
                    RoomConversionRate.fromConversionRate(it)
                }
                db.roomConversionRateDao().insert(roomConversionRateList)
                return@fromCallable MainPreference.setFirstTimeFalse(context)
            } else {
                return@fromCallable true
            }
        }
    }

    fun getSelectedBaseCurrencyCode(context: Context) =
        MainPreference.getSelectedBaseCurrencyCode(context)

    fun getSelectedTargetCurrencyCode(context: Context) =
        MainPreference.getSelectedTargetCurrencyCode(context)

    fun setSelectedBaseCurrencyCode(currencyCode: String) =
        MainPreference.setSelectedBaseCurrencyCode(context, currencyCode)

    fun setSelectedTargetCurrencyCode(currencyCode: String) =
        MainPreference.setSelectedTargetCurrencyCode(context, currencyCode)

    fun getBaseRateFlowable(): Flowable<List<RoomConversionRate>> {
        val base = getSelectedBaseCurrencyCode(context) // "JPY"
        return db.roomConversionRateDao().findConversionRateFlowable("USD${base}")
    }

    fun getTargetRateFlowable(): Flowable<List<RoomConversionRate>> {
        val target = getSelectedTargetCurrencyCode(context) // "JPY"
        return db.roomConversionRateDao().findConversionRateFlowable("USD${target}")
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
                Log.d("ddw", "rate: " + rate)
                return@BiFunction rate
            })
    }
}
