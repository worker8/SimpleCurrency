package com.worker8.simplecurrency.common

import com.worker8.simplecurrency.di.scope.PerActivityScope
import com.worker8.simplecurrency.di.scope.ScopeConstant
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

@PerActivityScope
class SchedulerSharedRepo @Inject constructor(
    @Named(ScopeConstant.MainThreadScheduler)
    val mainThread: Scheduler,
    @Named(ScopeConstant.BackgroundThreadScheduler)
    val backgroundThread: Scheduler
)
