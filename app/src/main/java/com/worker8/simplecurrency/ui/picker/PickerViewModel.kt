package com.worker8.simplecurrency.ui.picker

import androidx.lifecycle.*
import com.worker8.currencylayer.model.Currency
import com.worker8.simplecurrency.common.addTo
import com.worker8.simplecurrency.common.extension.toTwoDecimalWithComma
import com.worker8.simplecurrency.common.realValue
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class PickerViewModel(private val repo: PickerRepo) :
    ViewModel(), LifecycleObserver {
    private val screenStateSubject =
        BehaviorSubject.createDefault(PickerContract.ScreenState(linkedSetOf(), false, ""))
    private val currentScreenState: PickerContract.ScreenState get() = screenStateSubject.realValue
    var screenState: Observable<PickerContract.ScreenState> =
        screenStateSubject.hide().observeOn(repo.mainThread)
    private val disposableBag = CompositeDisposable()
    lateinit var input: PickerContract.Input
    lateinit var viewAction: PickerContract.ViewAction
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        input.apply {
            Observable.merge(
                Observable.just(""),
                onFilterTextChanged.debounce(300, TimeUnit.MILLISECONDS)
            )
                .flatMap { repo.getAllCurrenciesFromDb(it) }
                .subscribeOn(repo.backgroundThread)
                .observeOn(repo.backgroundThread)
                .map { it to repo.getBaseRate() }
                .map { (filteredCurrencyRates, baseCurrencyList) ->
                    val baseCurrency = baseCurrencyList[0].rate
                    val resultSet = linkedSetOf<PickerAdapter.PickerRowType>()
                    filteredCurrencyRates.forEach { roomConversionRate ->
                        resultSet.add(roomConversionRate.run {
                            val baseToTargetRate = (rate / baseCurrency)
                            PickerAdapter.PickerRowType(
                                currencyName = Currency.ALL[getCodeWithoutUSD()] ?: "",
                                currencyRate = "1 ${repo.getSelectedBaseCurrencyCode()} = ${rate.toTwoDecimalWithComma()} ${getCodeWithoutUSD()}",
                                currencyRateCalculated = "$inputAmount ${repo.getSelectedBaseCurrencyCode()} = ${(inputAmount * baseToTargetRate).toTwoDecimalWithComma()} ${getCodeWithoutUSD()}",
                                currencyCode = getCodeWithoutUSD()
                            )
                        })
                    }
                    resultSet
                }
                .subscribe({
                    dispatch(currentScreenState.copy(currencyList = it))
                }, {
                    viewAction.showTerminalError()
                })
                .addTo(disposableBag)

            repo.getLatestUpdatedDate()
                .subscribeOn(repo.backgroundThread)
                .observeOn(repo.mainThread)
                .subscribe({
                    dispatch(
                        currentScreenState.copy(
                            rateDetailVisibility = !isBase,
                            latestUpdatedString = it
                        )
                    )
                }, {
                    viewAction.showTerminalError()
                })
                .addTo(disposableBag)

        }
    }

    private fun dispatch(screenState: PickerContract.ScreenState) {
        screenStateSubject.onNext(screenState)
    }

    override fun onCleared() {
        super.onCleared()
        disposableBag.dispose()
    }

    @Suppress("UNCHECKED_CAST")
    class PickerViewModelFactory(private val repo: PickerRepo) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PickerViewModel(repo) as T
        }
    }
}

//TODO: style arrangement
