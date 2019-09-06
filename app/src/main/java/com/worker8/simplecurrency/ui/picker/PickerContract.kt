package com.worker8.simplecurrency.ui.picker

class PickerContract {
    interface Input {

    }

    data class ScreenState(
        val currencyList: List<PickerAdapter.PickerRowType>
    )

}
