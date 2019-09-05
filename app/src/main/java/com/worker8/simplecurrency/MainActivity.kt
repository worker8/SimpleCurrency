package com.worker8.simplecurrency

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.numpad.*

class MainActivity : AppCompatActivity() {
    private val disposableBag = CompositeDisposable()
    lateinit var input: MainContract.Input

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = MainRepo(mainThread = AndroidSchedulers.mainThread(), backgroundThread = Schedulers.io())
        input = object : MainContract.Input {
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
        }
        setContentView(R.layout.activity_main)
        val viewModel =
            ViewModelProviders.of(this, MainViewModel.MainViewModelFactory(input, repo)).get(MainViewModel::class.java)
        lifecycle.addObserver(viewModel)
        viewModel.screenState
            .subscribe {
                it.apply {
                    mainInputNumber.text = inputNumberString
                    mainOutputNumber.text = outputNumberString
                    mainNumDot.isEnabled = isEnableDot
                }
            }
            .addTo(disposableBag)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableBag.dispose()
    }
}
