package com.worker8.simplecurrency

import androidx.lifecycle.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class MainViewModel(private val input: MainContract.Input, private val repo: MainRepo) :
    ViewModel(), LifecycleObserver {
    private val screenStateSubject = BehaviorSubject.createDefault(MainContract.ScreenState())
    private val disposableBag = CompositeDisposable()
    val currentScreenState get() = screenStateSubject.realValue
    var screenState = screenStateSubject.hide().observeOn(repo.mainThread)
    val fakeExchangeRate = 2

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        input.apply {
            val newInputStringObsShared = Observable.merge(
                arrayListOf(
                    input.onNumpad0Click,
                    input.onNumpad1Click,
                    input.onNumpad2Click,
                    input.onNumpad3Click,
                    input.onNumpad4Click,
                    input.onNumpad5Click,
                    input.onNumpad6Click,
                    input.onNumpad7Click,
                    input.onNumpad8Click,
                    input.onNumpad9Click
                )
            )
                .map { newChar ->
                    if (currentScreenState.inputNumberString.length == 1 && currentScreenState.inputNumberString == "0") {
                        newChar.toString()
                    } else {
                        currentScreenState.inputNumberString + newChar
                    }
                }
                .share()

            val backSpaceObsShared: Observable<String> = backSpaceClick.map {
                if (currentScreenState.inputNumberString.isEmpty() || currentScreenState.inputNumberString == "0") {
                    currentScreenState.inputNumberString
                } else if (currentScreenState.inputNumberString.length == 1) {
                    "0"
                } else {
                    currentScreenState.inputNumberString.removeRange(
                        currentScreenState.inputNumberString.length - 1,
                        currentScreenState.inputNumberString.length
                    )
                }
            }.share()

            Observable.merge(newInputStringObsShared, backSpaceObsShared)
                .map { newInputString ->
                    try {
                        Result.success<Int>(Integer.valueOf(newInputString))
                    } catch (exception: NumberFormatException) {
                        Result.failure<Int>(exception)
                    }
                }
                .filter { it.isSuccess }
                .map { it.getOrDefault(0) * fakeExchangeRate }
                .subscribe { outputCurrency ->
                    dispatch(currentScreenState.copy(outputNumberString = outputCurrency.toString()))
                }
                .addTo(disposableBag)

            Observable.merge(newInputStringObsShared, backSpaceObsShared).subscribe { newInputString ->
                dispatch(currentScreenState.copy(inputNumberString = newInputString))
            }
                .addTo(disposableBag)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposableBag.dispose()
    }

    fun dispatch(screenState: MainContract.ScreenState) {
        screenStateSubject.onNext(screenState)
    }

    @Suppress("UNCHECKED_CAST")
    class MainViewModelFactory(val input: MainContract.Input, private val repo: MainRepo) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(input, repo) as T
        }
    }
}
