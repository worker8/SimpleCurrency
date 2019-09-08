package com.worker8.simplecurrency.ui.picker

import androidx.lifecycle.*
import com.worker8.currencylayer.model.Currency
import com.worker8.simplecurrency.addTo
import com.worker8.simplecurrency.extension.toTwoDecimalWithComma
import com.worker8.simplecurrency.realValue
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class PickerViewModel(private val input: PickerContract.Input, private val repo: PickerRepo) :
    ViewModel(), LifecycleObserver {
    private val screenStateSubject =
        BehaviorSubject.createDefault(PickerContract.ScreenState(linkedSetOf(), false, ""))
    val currentScreenState get() = screenStateSubject.realValue
    var screenState = screenStateSubject.hide().observeOn(repo.schedulerSharedRepo.mainThread)
    private val disposableBag = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        input.apply {
            Observable.merge(
                Observable.just(""),
                onFilterTextChanged.debounce(300, TimeUnit.MILLISECONDS)
            )
                .flatMap { repo.getAllCurrenciesFromDb(it) }
                .subscribeOn(repo.schedulerSharedRepo.backgroundThread)
                .observeOn(repo.schedulerSharedRepo.backgroundThread)
                .map { it to repo.getBaseRate() }
                .map { (filteredCurrencyRates, baseCurrencyList) ->
                    val baseCurrency = baseCurrencyList.get(0).rate
                    val resultSet = linkedSetOf<PickerAdapter.PickerRowType>()
                    filteredCurrencyRates.forEach { roomConversionRate ->
                        resultSet.add(roomConversionRate.run {
                            val baseToTargetRate = (rate / baseCurrency)
                            PickerAdapter.PickerRowType(
                                currencyName = Currency.ALL.get(getCodeWithoutUSD()) ?: "",
                                currencyRate = "1 ${repo.getSelectedBaseCurrencyCode()} = ${rate.toTwoDecimalWithComma()} ${getCodeWithoutUSD()}",
                                currencyRateCalculated = "${inputAmount} ${repo.getSelectedBaseCurrencyCode()} = ${(inputAmount * baseToTargetRate).toTwoDecimalWithComma()} ${getCodeWithoutUSD()}",
                                currencyCode = getCodeWithoutUSD()
                            )
                        })
                    }
                    resultSet
                }
                .subscribe {
                    dispatch(currentScreenState.copy(it))
                }
                .addTo(disposableBag)

            repo.getLatestUpdatedDate()
                .subscribeOn(repo.schedulerSharedRepo.backgroundThread)
                .observeOn(repo.schedulerSharedRepo.mainThread)
                .subscribe {
                    dispatch(
                        currentScreenState.copy(
                            rateDetailVisibility = !isBase,
                            latestUpdatedString = it
                        )
                    )
                }
                .addTo(disposableBag)

        }
    }

    fun dispatch(screenState: PickerContract.ScreenState) {
        screenStateSubject.onNext(screenState)
    }

    override fun onCleared() {
        super.onCleared()
        disposableBag.dispose()
    }

    @Suppress("UNCHECKED_CAST")
    class PickerViewModelFactory(val input: PickerContract.Input, private val repo: PickerRepo) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PickerViewModel(input, repo) as T
        }
    }
}

//TODO: handle errors for .subscribe in all Rx stream
//TODO: correctly scope things - public/private
//TODO: lint check
//TODO: style arrangement

