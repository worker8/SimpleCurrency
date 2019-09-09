package com.worker8.simplecurrency.ui.main

import androidx.lifecycle.*
import com.worker8.simplecurrency.common.NumberFormatter
import com.worker8.simplecurrency.common.addTo
import com.worker8.simplecurrency.common.realValue
import com.worker8.simplecurrency.common.extension.toTwoDecimalWithComma
import com.worker8.simplecurrency.ui.main.event.BackSpaceInputEvent
import com.worker8.simplecurrency.ui.main.event.CalculateConversionRateEvent
import com.worker8.simplecurrency.ui.main.event.NewNumberInputEvent
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class MainViewModel(private val repo: MainRepoInterface) :
    ViewModel(), LifecycleObserver {
    private val screenStateSubject = BehaviorSubject.createDefault(MainContract.ScreenState())
    private val disposableBag = CompositeDisposable()
    private val currentScreenState: MainContract.ScreenState get() = screenStateSubject.realValue
    var screenState: Observable<MainContract.ScreenState> = screenStateSubject.hide()

    lateinit var input: MainContract.Input
    lateinit var viewAction: MainContract.ViewAction
    private lateinit var seedDatabaseSharedObservable: Observable<Boolean>
    private lateinit var backSpaceLongClickSharedObservable: Observable<String>
    private lateinit var newNumberInputSharedObservable: Observable<String>
    private lateinit var backSpaceInputEventSharedObservable: Observable<String>
    private lateinit var calculateConversionRateSharedObservable: Observable<Result<Pair<Double, Double>>>
    private lateinit var onTargetCurrencyClickedShared: Observable<Unit>
    private val triggerCalculateSubject: PublishSubject<String> = PublishSubject.create()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        setupInputEvents()
        processOutputEvents()
        triggerCalculateSubject.onNext(currentInputString())
    }

    private fun processOutputEvents() {
        Observable.merge(
            newNumberInputSharedObservable,
            backSpaceInputEventSharedObservable,
            backSpaceLongClickSharedObservable
        )
            .subscribe({ newInputString ->
                dispatch(
                    currentScreenState.copy(
                        inputNumberString = NumberFormatter.addComma(newInputString),
                        inputNumberStringState = newInputString,
                        isEnableDot = !newInputString.contains(".")
                    )
                )
            }, {
                it.printStackTrace()
            })
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
                        outputNumberString = outputCurrency.toTwoDecimalWithComma()
                    )
                )
            }
            .addTo(disposableBag)

        onTargetCurrencyClickedShared
            .subscribeOn(repo.mainThread)
            .withLatestFrom(calculateConversionRateSharedObservable,
                BiFunction<Unit, Result<Pair<Double, Double>>, Double> { _, result ->
                    val (input, _) = result.getOrDefault(Pair(0.0, 0.0))
                    input
                })
            .observeOn(repo.mainThread)
            .subscribe { viewAction.navigateToSelectTargetCurrency(it) }
            .addTo(disposableBag)

        input.swapButtonClick
            .subscribe {
                val tempBaseCode = repo.getSelectedBaseCurrencyCode()
                val tempTargetCode = repo.getSelectedTargetCurrencyCode()
                repo.setSelectedBaseCurrencyCode(tempTargetCode)
                repo.setSelectedTargetCurrencyCode(tempBaseCode)
                onCreate()
            }
            .addTo(disposableBag)

        repo.setupPeriodicUpdate()
    }

    private fun setupInputEvents() {
        disposableBag.clear()

        onTargetCurrencyClickedShared = input.onTargetCurrencyClicked.share()
        backSpaceLongClickSharedObservable = input.backSpaceLongClick.map { "0" }.share()
        newNumberInputSharedObservable =
            NewNumberInputEvent(input, screenStateSubject).process()
        backSpaceInputEventSharedObservable =
            BackSpaceInputEvent(input, screenStateSubject).process()
        seedDatabaseSharedObservable = repo.populateDbIfFirstTime().share()
        calculateConversionRateSharedObservable = CalculateConversionRateEvent(
            newNumberInputSharedObservable = newNumberInputSharedObservable,
            backSpaceInputEventSharedObservable = backSpaceInputEventSharedObservable,
            backSpaceLongClickSharedObservable = backSpaceLongClickSharedObservable,
            triggerCalculateSubject = triggerCalculateSubject,
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
    }

    private fun dispatch(screenState: MainContract.ScreenState) {
        screenStateSubject.onNext(screenState)
    }

    @Suppress("UNCHECKED_CAST")
    class MainViewModelFactory(private val repo: MainRepoInterface) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repo) as T
        }
    }
}
