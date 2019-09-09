package com.worker8.simplecurrency.common

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

fun Disposable.addTo(composite: CompositeDisposable) = composite.add(this)

// convenience value to avoid using !! everywhere
val <T> BehaviorSubject<T>.realValue: T
    get() = value!!
