package com.worker8.simplecurrency.ui.main

import io.reactivex.Scheduler

class MainRepo(
    val mainThread: Scheduler,
    val backgroundThread: Scheduler
)
