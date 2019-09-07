package com.worker8.simplecurrency.ui.main

import androidx.lifecycle.*
import com.worker8.simplecurrency.addTo
import com.worker8.simplecurrency.extension.toComma
import com.worker8.simplecurrency.extension.toTwoDecimalWithComma
import com.worker8.simplecurrency.realValue
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class MainViewModel(private val repo: MainRepo) :
    ViewModel(), LifecycleObserver {
    private val screenStateSubject = BehaviorSubject.createDefault(MainContract.ScreenState())
    private val disposableBag = CompositeDisposable()
    private val calculateDisposableBag = CompositeDisposable()
    val currentScreenState get() = screenStateSubject.realValue
    var screenState = screenStateSubject.hide().observeOn(repo.schedulerSharedRepo.mainThread)

    lateinit var input: MainContract.Input
    lateinit var concatObsShared: Observable<String>
    lateinit var seedObsShared: Observable<Boolean>
    lateinit var backSpaceObsShared: Observable<String>

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        disposableBag.clear()
        setupInputEvents()

        input.apply {
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

            onBaseCurrencyChanged
                .subscribe {
                    repo.setSelectedBaseCurrencyCode(it)
                    refreshCalculateStream()
                }
                .addTo(disposableBag)

            onTargetCurrencyChanged
                .subscribe {
                    repo.setSelectedTargetCurrencyCode(it)
                    refreshCalculateStream()
                }
                .addTo(disposableBag)
        }
    }

    private fun refreshCalculateStream() {
        calculateDisposableBag.clear()
        val calculateObsShared = Flowable.combineLatest(
            Observable.merge(concatObsShared, backSpaceObsShared).toFlowable(BackpressureStrategy.LATEST),
            seedObsShared.subscribeOn(repo.schedulerSharedRepo.backgroundThread).toFlowable(BackpressureStrategy.DROP).flatMap { repo.getLatestSelectedRateFlowable() },
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
                input to input * rate
            }
            .subscribe { (input, outputCurrency) ->
                dispatch(
                    currentScreenState.copy(
                        inputNumberString = input.toComma().addCurrencySymbol('¥'),
                        outputNumberString = outputCurrency.toTwoDecimalWithComma().addCurrencySymbol('$')
                    )
                )
            }
            .addTo(calculateDisposableBag)
    }

    fun setupInputEvents() {
        concatObsShared =
            Observable.merge(
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
                    input.onNumpad9Click,
                    input.dotClick
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
        seedObsShared =
            repo.populateDbIfFirstTime()
                .share()
        backSpaceObsShared =
            input.backSpaceClick.map {
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
        refreshCalculateStream()
    }

    private fun currentInputString(): String {
        return currentScreenState.inputNumberStringState
    }

    private fun String.addCurrencySymbol(char: Char): String {
        // TODO: API is not returning symbol, need to map it externally (not doing anything now)
        return this
        // return "${char} ${this}"
    }

    override fun onCleared() {
        super.onCleared()
        disposableBag.dispose()
        calculateDisposableBag.dispose()
    }

    fun dispatch(screenState: MainContract.ScreenState) {
        screenStateSubject.onNext(screenState)
    }

    @Suppress("UNCHECKED_CAST")
    class MainViewModelFactory(private val repo: MainRepo) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repo) as T
        }
    }
}
