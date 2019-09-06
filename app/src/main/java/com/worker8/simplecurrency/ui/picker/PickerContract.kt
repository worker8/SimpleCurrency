package com.worker8.simplecurrency.ui.picker

class PickerContract {
    interface Input {

    }

    data class ScreenState(
        val currencyList: List<Row>
    )

    data class Row(val currencyName: String, val currencyRate: String, val currencyCode: String)
}
