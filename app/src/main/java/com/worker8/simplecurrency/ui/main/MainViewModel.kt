package com.worker8.simplecurrency.ui.main

import androidx.lifecycle.*
import com.worker8.simplecurrency.addTo
import com.worker8.simplecurrency.extension.toTwoDecimalWithComma
import com.worker8.simplecurrency.extension.toComma
import com.worker8.simplecurrency.realValue
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class MainViewModel(private val input: MainContract.Input, private val repo: MainRepo) :
    ViewModel(), LifecycleObserver {
    private val screenStateSubject = BehaviorSubject.createDefault(MainContract.ScreenState())
    private val disposableBag = CompositeDisposable()
    val currentScreenState get() = screenStateSubject.realValue
    var screenState = screenStateSubject.hide().observeOn(repo.schedulerSharedRepo.mainThread)

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        input.apply {

            val seedObs = repo.populateDbIfFirstTime()
                .subscribeOn(repo.schedulerSharedRepo.backgroundThread)

            val concatObsShared = Observable.merge(
                arrayListOf(
                    onNumpad0Click,
                    onNumpad1Click,
                    onNumpad2Click,
                    onNumpad3Click,
                    onNumpad4Click,
                    onNumpad5Click,
                    onNumpad6Click,
                    onNumpad7Click,
                    onNumpad8Click,
                    onNumpad9Click,
                    dotClick
                )
            )
                .map { newChar ->
                    if (newChar == '.') {
                        // handling '.' as input
                        // before: "0"  --> after: "0."
                        // before: "123 --> after: "123.
                        currentInputString() + newChar
                    } else if (currentInputString().length == 1 && currentInputString() == "0") {
                        // handle case when input is "0" (beginning)
                        // before: "0" --> after: "2"
                        newChar.toString()
                    } else {
                        // handle normal case
                        // before "123", after "1234"
                        currentInputString() + newChar
                    }
                }
                .share()

            val backSpaceObsShared = backSpaceClick.map {
                if (currentInputString().isEmpty() || currentInputString() == "0") {
                    currentInputString()
                } else if (currentInputString().length == 1) {
                    "0"
                } else {
                    currentInputString().removeRange(
                        currentInputString().length - 1,
                        currentInputString().length
                    )
                }
            }.share()

            Observable.merge(concatObsShared, backSpaceObsShared)
                .subscribe { newInputString ->
                    dispatch(
                        currentScreenState.copy(
                            inputNumberStringState = newInputString,
                            isEnableDot = !newInputString.contains(".")
                        )
                    )
                }
                .addTo(disposableBag)

            val calculateObsShared = Flowable.combineLatest(
                Observable.merge(concatObsShared, backSpaceObsShared).toFlowable(BackpressureStrategy.LATEST),
                seedObs.toFlowable(BackpressureStrategy.DROP).flatMap { repo.getLatestSelectedRateFlowable() },
                BiFunction<String, Double, Result<Pair<Double, Double>>> { numberString, rate ->
                    val dotRemoved = if (numberString.isNotEmpty() && numberString.last() == '.') {
                        numberString.removeRange(
                            numberString.length - 1,
                            numberString.length
                        )
                    } else {
                        numberString
                    }
                    val input = dotRemoved.toDoubleOrNull()
                    return@BiFunction if (input != null) {
                        Result.success(input to rate)
                    } else {
                        Result.failure(Exception())
                    }
                })

            calculateObsShared
                .map { result ->
                    val (input, rate) = result.getOrDefault(Pair(0.0, 0.0))
                    input * rate
                }
                .subscribe { outputCurrency ->
                    dispatch(
                        currentScreenState.copy(
                            outputNumberString =
                            outputCurrency.toTwoDecimalWithComma().addCurrencySymbol('$')
                        )
                    )
                }
                .addTo(disposableBag)

            calculateObsShared
                .map { result ->
                    val (input, rate) = result.getOrDefault(Pair(0.0, 0.0))
                    input
                }
                .subscribe { newInputDouble ->
                    dispatch(
                        currentScreenState.copy(
                            inputNumberString =
                            newInputDouble
                                .toComma()
                                .addCurrencySymbol('¥')
                        )
                    )
                }
                .addTo(disposableBag)
        }
    }

    private fun currentInputString(): String {
        return currentScreenState.inputNumberStringState
    }

    private fun String.addCurrencySymbol(char: Char): String {
        return "${char} ${this}"
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
