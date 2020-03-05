package com.worker8.simplecurrency.ui.picker

import android.content.Context
import com.worker8.simplecurrency.common.sharedPreference.MainPreference
import com.worker8.simplecurrency.db.SimpleCurrencyDatabase
import com.worker8.simplecurrency.db.entity.RoomConversionRate
import com.worker8.simplecurrency.di.scope.PerActivityScope
import com.worker8.simplecurrency.di.scope.ScopeConstant
import io.reactivex.Observable
import io.reactivex.Scheduler
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named

@PerActivityScope
class PickerRepo @Inject constructor(
    private val context: Context,
    val db: SimpleCurrencyDatabase,
    @Named(ScopeConstant.MainThreadScheduler)
    val mainThread: Scheduler,
    @Named(ScopeConstant.BackgroundThreadScheduler)
    val backgroundThread: Scheduler
) {
    fun getAllCurrenciesFromDb(searchText: String): Observable<List<RoomConversionRate>> =
        db.roomConversionRateDao().findRoomConversionRateFlowable(
            "%$searchText%",
            "%$searchText%"
        ).toObservable()

    fun getBaseRate(): List<RoomConversionRate> {
        val baseCurrency = getSelectedBaseCurrencyCode()
        return db.roomConversionRateDao().findConversionRate("$baseCurrency")
    }

    fun getLatestUpdatedDate(): Observable<String> {
        return Observable.fromCallable {
            val foundList = db.roomUpdatedTimeStampDao().findLatestTimeStamp()

            if (foundList.isNotEmpty()) {
                val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm a")
                val zonedDateTime = ZonedDateTime.ofInstant(
                    Instant.ofEpochSecond(foundList[0].timeStamp),
                    ZoneId.systemDefault()
                )
                zonedDateTime.format(formatter)
            } else ""
        }
    }

    fun getSelectedBaseCurrencyCode() =
        MainPreference.getSelectedBaseCurrencyCode(context)
}
