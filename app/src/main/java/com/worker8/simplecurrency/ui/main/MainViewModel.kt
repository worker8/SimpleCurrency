package com.worker8.simplecurrency.ui.main

import androidx.lifecycle.*
import com.worker8.simplecurrency.addTo
import com.worker8.simplecurrency.extension.toComma
import com.worker8.simplecurrency.extension.toTwoDecimalWithComma
import com.worker8.simplecurrency.realValue
import com.worker8.simplecurrency.ui.main.event.BackSpaceInputEvent
import com.worker8.simplecurrency.ui.main.event.CalculateConversionRateEvent
import com.worker8.simplecurrency.ui.main.event.NewNumberInputEvent
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class MainViewModel(private val repo: MainRepo) :
    ViewModel(), LifecycleObserver {
    private val screenStateSubject = BehaviorSubject.createDefault(MainContract.ScreenState())
    private val disposableBag = CompositeDisposable()
    private val calculateDisposableBag = CompositeDisposable()
    val currentScreenState get() = screenStateSubject.realValue
    var screenState = screenStateSubject.hide()

    lateinit var input: MainContract.Input
    lateinit var viewAction: MainContract.ViewAction
    lateinit var seedDatabaseSharedObservable: Observable<Boolean>
    lateinit var newNumberInputSharedObservable: Observable<String>
    lateinit var backSpaceInputEventSharedObservable: Observable<String>
    lateinit var calculateConversionRateSharedObservable: Observable<Result<Pair<Double, Double>>>
    lateinit var onTargetCurrencyClickedShared: Observable<Unit>
    private val initializeSubject: PublishSubject<String> = PublishSubject.create()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        setupInputEvents()
        processOutputEvents()
        initializeSubject.onNext(currentInputString())
    }

    fun processOutputEvents() {
        Observable.merge(newNumberInputSharedObservable, backSpaceInputEventSharedObservable)
            .subscribe { newInputString ->
                dispatch(
                    currentScreenState.copy(
                        inputNumberStringState = newInputString,
                        isEnableDot = !newInputString.contains(".")
                    )
                )
            }
            .addTo(disposableBag)

        input.onBaseCurrencyChanged
            .subscribe {
                repo.setSelectedBaseCurrencyCode(it)
                onCreate()
            }
            .addTo(disposableBag)

        input.onTargetCurrencyChanged
            .subscribe {
                repo.setSelectedTargetCurrencyCode(it)
                onCreate()
            }
            .addTo(disposableBag)

        calculateConversionRateSharedObservable
            .map { result ->
                val (input, rate) = result.getOrDefault(Pair(0.0, 0.0))
                input to input * rate
            }
            .subscribe { (input, outputCurrency) ->
                dispatch(
                    currentScreenState.copy(
                        baseCurrencyCode = repo.getSelectedBaseCurrencyCode(),
                        targetCurrencyCode = repo.getSelectedTargetCurrencyCode(),
                        inputNumberString = input.toComma(),
                        outputNumberString = outputCurrency.toTwoDecimalWithComma()
                    )
                )
            }
            .addTo(calculateDisposableBag)

        onTargetCurrencyClickedShared
            .subscribeOn(repo.schedulerSharedRepo.mainThread)
            .withLatestFrom(calculateConversionRateSharedObservable,
                BiFunction<Unit, Result<Pair<Double, Double>>, Double> { _, result ->
                    val (input, _) = result.getOrDefault(Pair(0.0, 0.0))
                    input
                })
            .observeOn(repo.schedulerSharedRepo.mainThread)
            .subscribe { viewAction.navigateToSelectTargetCurrency(it) }
            .addTo(calculateDisposableBag)

        repo.setupPeriodicUpdate()
    }

    fun setupInputEvents() {
        disposableBag.clear()
        calculateDisposableBag.clear()

        onTargetCurrencyClickedShared = input.onTargetCurrencyClicked.share()
        newNumberInputSharedObservable = NewNumberInputEvent(input, screenStateSubject).process()
        backSpaceInputEventSharedObservable =
            BackSpaceInputEvent(input, screenStateSubject).process()
        seedDatabaseSharedObservable = repo.populateDbIfFirstTime().share()
        calculateConversionRateSharedObservable = CalculateConversionRateEvent(
            newNumberInputSharedObservable = newNumberInputSharedObservable,
            backSpaceInputEventSharedObservable = backSpaceInputEventSharedObservable,
            initializeSubject = initializeSubject,
            seedDatabaseSharedObservable = seedDatabaseSharedObservable,
            repo = repo
        ).process()
    }

    private fun currentInputString(): String {
        return currentScreenState.inputNumberStringState
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
