package com.worker8.simplecurrency.ui.picker

import io.reactivex.Flowable
import io.reactivex.Observable


class PickerContract {
    interface Input {
        val isBase: Boolean
        val onFilterTextChanged: Flowable<String>
        val inputAmount: Double
    }

    data class ScreenState(
        val currencyList: List<PickerAdapter.PickerRowType>,
        val rateDetailVisibility: Boolean
    )
}
