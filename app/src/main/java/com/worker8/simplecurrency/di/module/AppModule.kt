package com.worker8.simplecurrency.di.module

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.squareup.moshi.Moshi
import com.worker8.currencylayer.network.CurrencyLayerLiveService
import com.worker8.currencylayer.network.CurrencyLayerMoshi
import com.worker8.currencylayer.network.CurrencyLayerRetrofit
import com.worker8.simplecurrency.BuildConfig
import com.worker8.simplecurrency.SimpleCurrencyApplication
import com.worker8.simplecurrency.db.SimpleCurrencyDatabase
import com.worker8.simplecurrency.di.scope.ScopeConstant
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton


@Module(includes = [AppModule.AppModuleInterface::class])
class AppModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): SimpleCurrencyDatabase {
        return Room.databaseBuilder(
            context,
            SimpleCurrencyDatabase::class.java,
            "SimpleCurrencyDatabase"
        ).build()
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return CurrencyLayerMoshi.build()
    }

    @Singleton
    @Provides
    fun provideOKHttp3(): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()
        } else {
            OkHttpClient.Builder()
                .build()
        }
    }

    @Singleton
    @Provides
    fun provideRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit {
        return CurrencyLayerRetrofit.build(moshi, okHttpClient)
    }

    @Singleton
    @Provides
    fun provideWorkManager(context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideCurrencyLayerLiveService(retrofit: Retrofit): CurrencyLayerLiveService {
        return retrofit.create(CurrencyLayerLiveService::class.java)
    }

    @Named(ScopeConstant.MainThreadScheduler)
    @Provides
    fun provideMainThreadScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    @Named(ScopeConstant.BackgroundThreadScheduler)
    @Provides
    fun provideBackgroundThreadScheduler(): Scheduler {
        return Schedulers.io()
    }

    @Module
    interface AppModuleInterface {
        @Binds
        fun provideContext(application: SimpleCurrencyApplication): Context
    }
}
