package com.worker8.simplecurrency.di

import com.worker8.simplecurrency.di.scope.PerActivityScope
import com.worker8.simplecurrency.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @PerActivityScope
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}

