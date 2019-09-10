package com.worker8.simplecurrency

import com.worker8.simplecurrency.ui.main.MainContract
import com.worker8.simplecurrency.ui.main.MainRepoInterface
import com.worker8.simplecurrency.ui.main.MainViewModel
import io.mockk.*
import io.reactivex.observers.TestObserver
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MainViewModelTest {
    private lateinit var input: MainContract.Input
    private lateinit var repo: MainRepoInterface
    private lateinit var viewModel: MainViewModel
    private lateinit var viewAction: MainContract.ViewAction

    /* input */
    private lateinit var onNumpad0Click: PublishSubject<Char>
    private lateinit var onNumpad1Click: PublishSubject<Char>
    private lateinit var onNumpad2Click: PublishSubject<Char>
    private lateinit var onNumpad3Click: PublishSubject<Char>
    private lateinit var onNumpad4Click: PublishSubject<Char>
    private lateinit var onNumpad5Click: PublishSubject<Char>
    private lateinit var onNumpad6Click: PublishSubject<Char>
    private lateinit var onNumpad7Click: PublishSubject<Char>
    private lateinit var onNumpad8Click: PublishSubject<Char>
    private lateinit var onNumpad9Click: PublishSubject<Char>
    private lateinit var backSpaceClick: PublishSubject<Unit>
    private lateinit var backSpaceLongClick: PublishSubject<Unit>
    private lateinit var swapButtonClick: PublishSubject<Unit>
    private lateinit var dotClick: PublishSubject<Char>
    private lateinit var onBaseCurrencyChanged: PublishSubject<String>
    private lateinit var onTargetCurrencyChanged: PublishSubject<String>
    private lateinit var onTargetCurrencyClicked: PublishSubject<Unit>

    /* repo */
    private lateinit var getLatestSelectedRateFlowable: PublishProcessor<Double>
    private lateinit var populateDbIfFirstTime: PublishSubject<Boolean>

    @Before
    fun setup() {
        input = mockk()

        onNumpad0Click = PublishSubject.create()
        onNumpad1Click = PublishSubject.create()
        onNumpad2Click = PublishSubject.create()
        onNumpad3Click = PublishSubject.create()
        onNumpad4Click = PublishSubject.create()
        onNumpad5Click = PublishSubject.create()
        onNumpad6Click = PublishSubject.create()
        onNumpad7Click = PublishSubject.create()
        onNumpad8Click = PublishSubject.create()
        onNumpad9Click = PublishSubject.create()
        backSpaceClick = PublishSubject.create()
        backSpaceLongClick = PublishSubject.create()
        swapButtonClick = PublishSubject.create()
        dotClick = PublishSubject.create()
        onBaseCurrencyChanged = PublishSubject.create()
        onTargetCurrencyChanged = PublishSubject.create()
        onTargetCurrencyClicked = PublishSubject.create()

        getLatestSelectedRateFlowable = PublishProcessor.create()
        populateDbIfFirstTime = PublishSubject.create()

        every { input.onNumpad0Click } returns onNumpad0Click
        every { input.onNumpad1Click } returns onNumpad1Click
        every { input.onNumpad2Click } returns onNumpad2Click
        every { input.onNumpad3Click } returns onNumpad3Click
        every { input.onNumpad4Click } returns onNumpad4Click
        every { input.onNumpad5Click } returns onNumpad5Click
        every { input.onNumpad6Click } returns onNumpad6Click
        every { input.onNumpad7Click } returns onNumpad7Click
        every { input.onNumpad8Click } returns onNumpad8Click
        every { input.onNumpad9Click } returns onNumpad9Click

        every { input.backSpaceClick } returns backSpaceClick
        every { input.backSpaceLongClick } returns backSpaceLongClick
        every { input.swapButtonClick } returns swapButtonClick
        every { input.dotClick } returns dotClick
        every { input.onBaseCurrencyChanged } returns onBaseCurrencyChanged
        every { input.onTargetCurrencyChanged } returns onTargetCurrencyChanged
        every { input.onTargetCurrencyClicked } returns onTargetCurrencyClicked

        repo = mockk(relaxed = true)


        every { repo.mainThread } returns Schedulers.trampoline()
        every { repo.backgroundThread } returns Schedulers.trampoline()
        every { repo.populateDbIfFirstTime() } returns populateDbIfFirstTime
        every { repo.getSelectedBaseCurrencyCode() } returns "JPY"
        every { repo.getSelectedTargetCurrencyCode() } returns "USD"

        every { repo.setSelectedBaseCurrencyCode(any()) } returns true
        every { repo.setSelectedTargetCurrencyCode(any()) } returns true

        every { repo.getLatestSelectedRateFlowable() } returns getLatestSelectedRateFlowable
        every { repo.setupPeriodicUpdate() } just Runs

        viewAction = mockk(relaxed = true)

        viewModel = MainViewModel(repo)
            .apply {
                input = this@MainViewModelTest.input
                viewAction = this@MainViewModelTest.viewAction
            }
    }

    @Test
    fun testSimpleConversion() {
        // 1. arrange
        val fakeRate = 2.0
        viewModel.onCreate()
        val screenStateTestObserver = viewModel.screenState.test()

        // 2. act
        populateDbIfFirstTime.onNext(true)
        getLatestSelectedRateFlowable.offer(fakeRate)

        onNumpad1Click.onNext('1')
        onNumpad0Click.onNext('0')
        onNumpad0Click.onNext('0')

        // 3. assert
        verify(exactly = 1) { repo.setupPeriodicUpdate() }
        screenStateTestObserver.assertNoErrors()

        screenStateTestObserver.lastValue.apply {
            Assert.assertEquals(outputNumberString, "200")
        }
    }

    @Test
    fun testDecimalConversion() {
        // 1. arrange
        val fakeRate = 2.0
        viewModel.onCreate()
        val screenStateTestObserver = viewModel.screenState.test()

        // 2. act
        populateDbIfFirstTime.onNext(true)
        getLatestSelectedRateFlowable.offer(fakeRate)

        // key in: 99.2
        onNumpad1Click.onNext('9')
        onNumpad0Click.onNext('9')
        onNumpad0Click.onNext('.')
        onNumpad0Click.onNext('2')

        // 3. assert
        verify(exactly = 1) { repo.setupPeriodicUpdate() }
        screenStateTestObserver.assertNoErrors()

        screenStateTestObserver.lastValue.apply {
            Assert.assertEquals(outputNumberString, "198.4") // 99.2 * 2 = 198.4
        }
    }

    @Test
    fun testBigNumberWithCommaConversion() {
        // 1. arrange
        val fakeRate = 2.0
        viewModel.onCreate()
        val screenStateTestObserver = viewModel.screenState.test()

        // 2. act
        populateDbIfFirstTime.onNext(true)
        getLatestSelectedRateFlowable.offer(fakeRate)

        // key in: 1,000,123.2
        onNumpad1Click.onNext('1')
        onNumpad0Click.onNext('0')
        onNumpad0Click.onNext('0')
        onNumpad0Click.onNext('0')
        onNumpad0Click.onNext('1')
        onNumpad0Click.onNext('2')
        onNumpad0Click.onNext('3')
        onNumpad0Click.onNext('.')
        onNumpad0Click.onNext('2')

        // 3. assert
        verify(exactly = 1) { repo.setupPeriodicUpdate() }
        screenStateTestObserver.assertNoErrors()

        screenStateTestObserver.lastValue.apply {
            Assert.assertEquals(inputNumberString, "1,000,123.2")
            Assert.assertEquals(outputNumberString, "2,000,246.4") // 1,000,123.2 * 2
        }
    }

    @Test
    fun testSwapCurrency() {
        // 1. arrange
        val fakeRate = 2.0
        viewModel.onCreate()
        val screenStateTestObserver = viewModel.screenState.test()

        // 2. act
        populateDbIfFirstTime.onNext(true)
        getLatestSelectedRateFlowable.offer(fakeRate)
        onNumpad1Click.onNext('1')
        onNumpad0Click.onNext('0')
        onNumpad0Click.onNext('0')

        // 3. assert - check initial state
        screenStateTestObserver.lastValue.apply {
            Assert.assertEquals(baseCurrencyCode, "JPY")
            Assert.assertEquals(targetCurrencyCode, "USD") // 1,000,123.2 * 2
        }

        // 2. act - hit swap button
        verify(exactly = 4) {
            repo.getSelectedBaseCurrencyCode()
        }

        swapButtonClick.onNext(Unit)
        populateDbIfFirstTime.onNext(true)
        getLatestSelectedRateFlowable.offer(4.0) // not using real db here, update manually

        // 3. assert - check if output is updated once currency is updated
        screenStateTestObserver.lastValue.apply {
            Assert.assertEquals(inputNumberString, "100")
            Assert.assertEquals(outputNumberString, "400") // 1,000,123.2 * 2
        }
    }

    private val TestObserver<MainContract.ScreenState>.lastValue
        get() = this.values().last()
}
