package com.worker8.simplecurrency.ui.main

import android.content.Context
import com.squareup.moshi.Moshi
import com.worker8.currencylayer.network.SeedCurrencyLayerLiveService
import com.worker8.simplecurrency.common.MainPreference
import com.worker8.simplecurrency.common.SchedulerSharedRepo
import com.worker8.simplecurrency.db.SimpleCurrencyDatabase
import com.worker8.simplecurrency.db.entity.RoomConversionRate
import com.worker8.simplecurrency.di.scope.PerActivityScope
import io.reactivex.Observable
import javax.inject.Inject

@PerActivityScope
class MainRepo @Inject constructor(
    val context: Context,
    val db: SimpleCurrencyDatabase,
    val moshi: Moshi,
    val schedulerSharedRepo: SchedulerSharedRepo
) {
    fun populateDbIfFirstTime(): Observable<Unit> {
        return Observable.fromCallable {
            if (MainPreference.getFirstTime(context)) {
                // populate
                val quotes = SeedCurrencyLayerLiveService(moshi).getSeedCurrencies()
                val roomConversionRateList = quotes.conversionRates.map {
                    RoomConversionRate.fromConversionRate(it)
                }

                db.roomConversionRateDao().insert(roomConversionRateList)
                MainPreference.setFirstTimeFalse(context)
            }
        }
    }
}
