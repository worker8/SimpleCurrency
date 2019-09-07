package com.worker8.simplecurrency.ui.picker

import android.util.Log
import androidx.lifecycle.*
import com.worker8.currencylayer.model.Currency
import com.worker8.simplecurrency.addTo
import com.worker8.simplecurrency.extension.toTwoDecimalWithComma
import com.worker8.simplecurrency.realValue
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class PickerViewModel(private val input: PickerContract.Input, private val repo: PickerRepo) :
    ViewModel(), LifecycleObserver {
    private val screenStateSubject = BehaviorSubject.createDefault(PickerContract.ScreenState(listOf(), false))
    val currentScreenState get() = screenStateSubject.realValue
    var screenState = screenStateSubject.hide().observeOn(repo.schedulerSharedRepo.mainThread)
    private val disposableBag = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        input.apply {
            Flowable.merge(Observable.just("").toFlowable(BackpressureStrategy.DROP), onFilterTextChanged)
                .flatMap { repo.getAllCurrenciesFromDb(it) }
                .subscribeOn(repo.schedulerSharedRepo.backgroundThread)
                .observeOn(repo.schedulerSharedRepo.backgroundThread)
                .map {
                    Log.d("ddw", "size = ${it.size}")
                    it.map { roomConversionRate ->
                        roomConversionRate.run {
                            PickerAdapter.PickerRowType(
                                currencyName = Currency.ALL.get(getCodeWithoutUSD()) ?: "",
                                currencyRate = "1 ${getCodeWithoutUSD()} = ${(1 / rate).toTwoDecimalWithComma()} USD",
                                currencyCode = getCodeWithoutUSD()
                            )
                        }
                    }
                }
                .subscribe {
                    dispatch(currentScreenState.copy(it))
                }
                .addTo(disposableBag)

            dispatch(currentScreenState.copy(rateDetailVisibility = !isBase))
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
