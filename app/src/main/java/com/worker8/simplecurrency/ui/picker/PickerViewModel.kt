package com.worker8.simplecurrency.ui.picker

import android.util.Log
import androidx.lifecycle.*
import com.worker8.currencylayer.model.Currency
import com.worker8.simplecurrency.addTo
import io.reactivex.disposables.CompositeDisposable

class PickerViewModel(private val input: PickerContract.Input, private val repo: PickerRepo) : ViewModel(),
    LifecycleObserver {
    private val disposableBag = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        input.apply {
            repo.getAllCurrenciesFromDb()
                .subscribeOn(repo.schedulerSharedRepo.backgroundThread)
                .observeOn(repo.schedulerSharedRepo.backgroundThread)
                .map {
                    it.map { roomConversionRate ->
                        roomConversionRate.run {
                            PickerContract.Row(
                                currencyName = Currency.ALL.get(getCodeWithoutUSD()) ?: "",
                                currencyRate = (1 / rate).toString(),
                                currencyCode = getCodeWithoutUSD()
                            )
                        }
                    }
                }
                .subscribe {
                    it.forEach {
                        Log.d("ddw", "row = ${it}")
                    }
                }
                .addTo(disposableBag)
        }
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
