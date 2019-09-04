package com.worker8.simplecurrency

import io.reactivex.Scheduler

class MainRepo(
    val mainThread: Scheduler,
    val backgroundThread: Scheduler
)
