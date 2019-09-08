package com.worker8.simplecurrency.ui.main

import io.reactivex.Observable

class MainContract {
    interface Input {
        val onNumpad0Click: Observable<Char>
        val onNumpad1Click: Observable<Char>
        val onNumpad2Click: Observable<Char>
        val onNumpad3Click: Observable<Char>
        val onNumpad4Click: Observable<Char>
        val onNumpad5Click: Observable<Char>
        val onNumpad6Click: Observable<Char>
        val onNumpad7Click: Observable<Char>
        val onNumpad8Click: Observable<Char>
        val onNumpad9Click: Observable<Char>
        val backSpaceClick: Observable<Unit>
        val dotClick: Observable<Char>
        val onBaseCurrencyChanged: Observable<String>
        val onTargetCurrencyChanged: Observable<String>
        val onTargetCurrencyClicked: Observable<Unit>
    }

    interface ViewAction {
        fun navigateToSelectTargetCurrency(inputAmount: Double)
    }

    data class ScreenState(
        val inputNumberStringState: String = "0",
        val inputNumberString: String = "0",
        val outputNumberString: String = "0",
        val baseCurrencyCode: String = "",
        val targetCurrencyCode: String = "",
        val isEnableDot: Boolean = true
    )
}
