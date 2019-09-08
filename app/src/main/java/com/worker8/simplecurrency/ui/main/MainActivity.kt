package com.worker8.simplecurrency.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding3.view.clicks
import com.worker8.simplecurrency.R
import com.worker8.simplecurrency.addTo
import com.worker8.simplecurrency.ui.picker.PickerActivity
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.numpad.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    private val disposableBag = CompositeDisposable()

    private val onBaseCurrencyChangedSubject: PublishSubject<String> = PublishSubject.create()
    private val onTargetCurrencyChangedSubject: PublishSubject<String> = PublishSubject.create()

    @Inject
    lateinit var repo: MainRepo
    val PICKER_BASE_REQUEST_CODE = 3832
    val PICKER_TARGET_REQUEST_CODE = 3833

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainInput = object : MainContract.Input {
            override val onBaseCurrencyChanged = onBaseCurrencyChangedSubject.hide()
            override val onTargetCurrencyChanged = onTargetCurrencyChangedSubject.hide()
            override val onNumpad0Click by lazy { mainNum0.clicks().map { '0' } }
            override val onNumpad1Click by lazy { mainNum1.clicks().map { '1' } }
            override val onNumpad2Click by lazy { mainNum2.clicks().map { '2' } }
            override val onNumpad3Click by lazy { mainNum3.clicks().map { '3' } }
            override val onNumpad4Click by lazy { mainNum4.clicks().map { '4' } }
            override val onNumpad5Click by lazy { mainNum5.clicks().map { '5' } }
            override val onNumpad6Click by lazy { mainNum6.clicks().map { '6' } }
            override val onNumpad7Click by lazy { mainNum7.clicks().map { '7' } }
            override val onNumpad8Click by lazy { mainNum8.clicks().map { '8' } }
            override val onNumpad9Click by lazy { mainNum9.clicks().map { '9' } }
            override val backSpaceClick by lazy { mainNumBackspace.clicks() }
            override val dotClick by lazy { mainNumDot.clicks().map { '.' } }
            override val onTargetCurrencyClicked by lazy { mainTargetCurrencyPicker.clicks() }
        }
        val viewActionLocal = object : MainContract.ViewAction {
            override fun navigateToSelectTargetCurrency(inputAmount: Double) {
                val intent = Intent(this@MainActivity, PickerActivity::class.java)
                    .apply {
                        putExtra(PickerActivity.BASE_OR_TARGET_KEY, false)
                        putExtra(PickerActivity.INPUT_AMOUNT, inputAmount)
                    }
                startActivityForResult(intent, PICKER_TARGET_REQUEST_CODE)
            }
        }
        val viewModel =
            ViewModelProviders.of(this, MainViewModel.MainViewModelFactory(repo))
                .get(MainViewModel::class.java).apply {
                    input = mainInput
                    viewAction = viewActionLocal
                }
        lifecycle.addObserver(viewModel)
        mainBaseCurrencyPicker.setOnClickListener {
            val intent = Intent(this@MainActivity, PickerActivity::class.java)
                .apply { putExtra(PickerActivity.BASE_OR_TARGET_KEY, true) }
            startActivityForResult(intent, PICKER_BASE_REQUEST_CODE)
        }
        viewModel.screenState
            .subscribe {
                it.apply {
                    mainInputCurrency.text = baseCurrencyCode
                    mainOutputCurrency.text = targetCurrencyCode
                    mainInputNumber.text = inputNumberString
                    mainOutputNumber.text = outputNumberString
                    mainNumDot.isEnabled = isEnableDot
                }
            }
            .addTo(disposableBag)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICKER_BASE_REQUEST_CODE -> {
                    data?.getStringExtra(PickerActivity.RESULT_KEY)?.let {
                        onBaseCurrencyChangedSubject.onNext(it)
                    }
                }
                PICKER_TARGET_REQUEST_CODE -> {
                    data?.getStringExtra(PickerActivity.RESULT_KEY)?.let {
                        onTargetCurrencyChangedSubject.onNext(it)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableBag.dispose()
    }
}
