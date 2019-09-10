package com.worker8.simplecurrency.ui.main

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler

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
