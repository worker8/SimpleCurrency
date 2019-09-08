package com.worker8.simplecurrency.ui.picker

import android.content.Context
import com.worker8.simplecurrency.common.MainPreference
import com.worker8.simplecurrency.common.SchedulerSharedRepo
import com.worker8.simplecurrency.db.SimpleCurrencyDatabase
import com.worker8.simplecurrency.db.entity.RoomConversionRate
import com.worker8.simplecurrency.di.scope.PerActivityScope
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

@PerActivityScope
class PickerRepo @Inject constructor(
    private val context: Context,
    val db: SimpleCurrencyDatabase,
    val schedulerSharedRepo: SchedulerSharedRepo
) {
    fun getAllCurrenciesFromDb(searchText: String) =
        db.roomConversionRateDao().getRoomConversionRateFlowable("%USD${searchText}%")

    fun getBaseRate(): List<RoomConversionRate> {
        val baseCurrency = getSelectedBaseCurrencyCode() // "JPY"
        val foundList = db.roomConversionRateDao().findConversionRate("USD${baseCurrency}")
        return foundList
    }

    fun getSelectedBaseCurrencyCode() =
        MainPreference.getSelectedBaseCurrencyCode(context)

    fun setSelectedBaseCurrencyCode(currencyCode: String) =
        MainPreference.setSelectedBaseCurrencyCode(context, currencyCode)

    fun setSelectedTargetCurrencyCode(currencyCode: String) =
        MainPreference.setSelectedTargetCurrencyCode(context, currencyCode)
}
