package com.worker8.simplecurrency.ui.main.event

import com.worker8.simplecurrency.common.realValue
import com.worker8.simplecurrency.ui.main.MainContract
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class NewNumberInputEvent(
    private val input: MainContract.Input,
    private val screenStateSubject: BehaviorSubject<MainContract.ScreenState>
) {
    private val currentScreenState get() = screenStateSubject.realValue
    fun process(): Observable<String> {
        return Observable.merge(
            arrayListOf(
                input.onNumpad0Click,
                input.onNumpad1Click,
                input.onNumpad2Click,
                input.onNumpad3Click,
                input.onNumpad4Click,
                input.onNumpad5Click,
                input.onNumpad6Click,
                input.onNumpad7Click,
                input.onNumpad8Click,
                input.onNumpad9Click,
                input.dotClick
            )
        )
            .map { newChar ->
                if (newChar == '.') {
                    // handling '.' as input
                    // before: "0"  --> after: "0."
                    // before: "123 --> after: "123.
                    currentInputString() + newChar
                } else if (currentInputString().length == 1 && currentInputString() == "0") {
                    // handle case when input is "0" (beginning)
                    // before: "0" --> after: "2"
                    newChar.toString()
                } else {
                    // handle normal case
                    // before "123", after "1234"
                    currentInputString() + newChar
                }
            }
            .share()
    }

    private fun currentInputString(): String {
        return currentScreenState.inputNumberStringState
    }
}
