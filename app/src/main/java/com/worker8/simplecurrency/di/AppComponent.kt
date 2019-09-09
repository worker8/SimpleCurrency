package com.worker8.simplecurrency.di

import com.worker8.simplecurrency.SimpleCurrencyApplication
import com.worker8.simplecurrency.di.module.ActivityModule
import com.worker8.simplecurrency.di.module.AppModule
import com.worker8.simplecurrency.di.module.RepoModule
import com.worker8.simplecurrency.worker.UpdateCurrencyWorker
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityModule::class,
        RepoModule::class
    ]
)
interface AppComponent : AndroidInjector<SimpleCurrencyApplication> {
    fun inject(updateCurrencyWorker: UpdateCurrencyWorker)

    @Component.Builder
    abstract class Builder {
        @BindsInstance
        abstract fun application(application: SimpleCurrencyApplication): Builder

        abstract fun build(): AppComponent
    }
}
