package com.worker8.simplecurrency.ui.main.event

import com.worker8.simplecurrency.realValue
import com.worker8.simplecurrency.ui.main.MainContract
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class BackSpaceInputEvent(
    private val input: MainContract.Input,
    private val screenStateSubject: BehaviorSubject<MainContract.ScreenState>
) {
    val currentScreenState get() = screenStateSubject.realValue
    fun process(): Observable<String> {
        return input.backSpaceClick.map {
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
    }

    private fun currentInputString(): String {
        return currentScreenState.inputNumberStringState
    }
}
