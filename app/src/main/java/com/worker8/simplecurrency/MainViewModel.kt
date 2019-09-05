package com.worker8.simplecurrency

import androidx.lifecycle.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class MainViewModel(private val input: MainContract.Input, private val repo: MainRepo) :
    ViewModel(), LifecycleObserver {
    private val screenStateSubject = BehaviorSubject.createDefault(MainContract.ScreenState())
    private val disposableBag = CompositeDisposable()
    val currentScreenState get() = screenStateSubject.realValue
    var screenState = screenStateSubject.hide().observeOn(repo.mainThread)
    val fakeExchangeRate = 0.0094

    fun outputNumberStringUseCase(){

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        input.apply {
            //val dotObsShared = dotClick.map { it.toString() }.share()
            val newInputStringObsShared = Observable.merge(
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
                        currentInputString() + newChar
                    } else if (currentInputString().length == 1 && currentInputString() == "0") {
                        newChar.toString()
                    } else {
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

            val processedInputDoubleObsShared = Observable.merge(newInputStringObsShared, backSpaceObsShared)
                .map { newInputString ->
                    val dotRemoved = if (newInputString.isNotEmpty() && newInputString.last() == '.') {
                        newInputString.removeRange(
                            newInputString.length - 1,
                            newInputString.length
                        )
                    } else {
                        newInputString
                    }
                    val tempDouble = dotRemoved.toDoubleOrNull()
                    return@map if (tempDouble != null) {
                        Result.success(tempDouble)
                    } else {
                        Result.failure(Exception())
                    }
                }
                .filter { it.isSuccess }
                .share()

            processedInputDoubleObsShared
                .map { it.getOrDefault(0.0) * fakeExchangeRate }
                .subscribe { outputCurrency ->
                    dispatch(
                        currentScreenState.copy(
                            outputNumberString =
                            outputCurrency.toReadableFormat().addCurrencySymbol('$')
                        )
                    )
                }
                .addTo(disposableBag)

            processedInputDoubleObsShared
                .subscribe { newInputDouble ->
                    dispatch(
                        currentScreenState.copy(
                            inputNumberString =
                            newInputDouble.getOrDefault(0.0)
                                .toReadableFormatInput()
                                .addCurrencySymbol('¥')
                        )
                    )
                }
                .addTo(disposableBag)

            Observable.merge(newInputStringObsShared, backSpaceObsShared)
                .subscribe { newInputString ->
                    dispatch(
                        currentScreenState.copy(
                            inputNumberStringState = newInputString,
                            isEnableDot = !newInputString.contains(".")
                        )
                    )
                }
                .addTo(disposableBag)
        }
    }

    private fun currentInputString(): String {
        return currentScreenState.inputNumberStringState
    }

    private fun Double.toReadableFormatInput(): String {
        val decimalFormat = DecimalFormat("#,###.#######################")
        decimalFormat.decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())
        val bigDecimal = this.toBigDecimal()
        return decimalFormat.format(bigDecimal)
    }

    private fun Double.toReadableFormat(): String {
        val decimalFormat = DecimalFormat("#,###.##")
        decimalFormat.decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())
        val bigDecimal = this.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        return decimalFormat.format(bigDecimal)
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
