package com.worker8.simplecurrency.di.module

import com.worker8.simplecurrency.ui.main.MainRepo
import com.worker8.simplecurrency.ui.main.MainRepoInterface
import dagger.Binds
import dagger.Module

@Module
abstract class RepoModule {
    @Binds
    abstract fun provideMainRepoInterface(mainRepo: MainRepo): MainRepoInterface
}
