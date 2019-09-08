package com.worker8.simplecurrency.ui.main.event

import com.worker8.simplecurrency.ui.main.MainRepo
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class CalculateConversionRateEvent(
    private val newNumberInputSharedObservable: Observable<String>,
    private val backSpaceInputEventSharedObservable: Observable<String>,
    private val backSpaceLongClickSharedObservable: Observable<String>,
    private val triggerCalculateSubject: PublishSubject<String> = PublishSubject.create(),
    private val seedDatabaseSharedObservable: Observable<Boolean>,
    private val repo: MainRepo
) {
    fun process(): Observable<Result<Pair<Double, Double>>> {
        return Observable.combineLatest(
            Observable.merge(
                newNumberInputSharedObservable,
                backSpaceInputEventSharedObservable,
                triggerCalculateSubject,
                backSpaceLongClickSharedObservable
            ),
            seedDatabaseSharedObservable.subscribeOn(repo.schedulerSharedRepo.backgroundThread)
                .flatMap { repo.getLatestSelectedRateFlowable().toObservable() },
            BiFunction<String, Double, Result<Pair<Double, Double>>> { numberString, rate ->
                val dotRemoved = if (numberString.isNotEmpty() && numberString.last() == '.') {
                    numberString.removeRange(
                        numberString.length - 1,
                        numberString.length
                    )
                } else {
                    numberString
                }
                val input = dotRemoved.toDoubleOrNull()
                return@BiFunction if (input != null) {
                    Result.success(input to rate)
                } else {
                    Result.failure(Exception())
                }
            }).share()
    }
}
