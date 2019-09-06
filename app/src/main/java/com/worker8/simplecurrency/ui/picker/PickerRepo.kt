package com.worker8.simplecurrency.ui.picker

import android.content.Context
import com.worker8.simplecurrency.common.SchedulerSharedRepo
import com.worker8.simplecurrency.db.SimpleCurrencyDatabase
import com.worker8.simplecurrency.di.scope.PerActivityScope
import javax.inject.Inject

@PerActivityScope
class PickerRepo @Inject constructor(
    private val context: Context,
    val db: SimpleCurrencyDatabase,
    val schedulerSharedRepo: SchedulerSharedRepo
) {
    fun getAllCurrenciesFromDb() =
        db.roomConversionRateDao().getRoomConversionRateFlowable()

}
