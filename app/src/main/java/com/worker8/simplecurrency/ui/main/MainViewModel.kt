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
    lateinit var backSpaceLongClickSharedObservable: Observable<String>
    lateinit var newNumberInputSharedObservable: Observable<String>
    lateinit var backSpaceInputEventSharedObservable: Observable<String>
    lateinit var calculateConversionRateSharedObservable: Observable<Result<Pair<Double, Double>>>
    lateinit var onTargetCurrencyClickedShared: Observable<Unit>
    private val triggerCalculateSubject: PublishSubject<String> = PublishSubject.create()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        setupInputEvents()
        processOutputEvents()
        triggerCalculateSubject.onNext(currentInputString())
    }

    fun formatInput(s: String): String {
        var dotIndex = s.indexOf('.')
        if (dotIndex == -1) {
            return s.toDouble().toComma()
        } else {
            return s.substring(0, dotIndex).toDouble().toComma() + s.substring(dotIndex)
        }
    }

    fun processOutputEvents() {
        Observable.merge(
            newNumberInputSharedObservable,
            backSpaceInputEventSharedObservable,
            backSpaceLongClickSharedObservable
        )
            .subscribe({ newInputString ->
                dispatch(
                    currentScreenState.copy(
                        inputNumberString = formatInput(newInputString),
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

//        backSpaceLongClickSharedObservable.subscribe { triggerCalculateSubject.onNext(it) }
//            .addTo(calculateDisposableBag)

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

        input.swapButtonClick
            .subscribe {
                val tempBaseCode = repo.getSelectedBaseCurrencyCode()
                val tempTargetCode = repo.getSelectedTargetCurrencyCode()
                repo.setSelectedBaseCurrencyCode(tempTargetCode)
                repo.setSelectedTargetCurrencyCode(tempBaseCode)
                onCreate()
            }
            .addTo(calculateDisposableBag)
//        input.backSpaceLongClick
//            .doOnNext { Log.d("ddw", "long press") }
//            .subscribe {
//                onCreate()
//            }
//            .addTo(disposableBag)
        repo.setupPeriodicUpdate()
    }

    fun setupInputEvents() {
        disposableBag.clear()
        calculateDisposableBag.clear()

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
