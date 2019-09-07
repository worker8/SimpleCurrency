package com.worker8.simplecurrency.ui.main

import android.content.Context
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
}
