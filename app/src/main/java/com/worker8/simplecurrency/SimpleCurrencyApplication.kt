package com.worker8.simplecurrency

import com.jakewharton.threetenabp.AndroidThreeTen
import com.worker8.simplecurrency.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class SimpleCurrencyApplication : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
        DebugSetting.init(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent
            .builder()
            .application(this)
            .build()
    }
}
