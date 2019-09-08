package com.worker8.simplecurrency.ui.picker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding3.widget.textChanges
import com.worker8.simplecurrency.R
import com.worker8.simplecurrency.addTo
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_picker.*
import javax.inject.Inject

class PickerActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var repo: PickerRepo
    lateinit var input: PickerContract.Input
    val adapter by lazy { PickerAdapter(isBase) }
    private val disposableBag = CompositeDisposable()

    val isBase get() = intent.getBooleanExtra(BASE_OR_TARGET_KEY, true)
    val inputAmount get() = intent.getDoubleExtra(INPUT_AMOUNT, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        input = object : PickerContract.Input {
            override val inputAmount = this@PickerActivity.inputAmount
            override val onFilterTextChanged =
                pickerInput.textChanges().map { it.toString() }
            override val isBase = this@PickerActivity.isBase
        }
        pickerRecyclerView.adapter = adapter
        val viewModel =
            ViewModelProviders.of(this, PickerViewModel.PickerViewModelFactory(input, repo))
                .get(PickerViewModel::class.java)

        viewModel.screenState
            .distinctUntilChanged()
            .observeOn(repo.schedulerSharedRepo.mainThread)
            .subscribe { screenState ->
                screenState.apply {
                    adapter.submitList(currencyList.toList())
                }
            }
            .addTo(disposableBag)

        adapter.selectedCurrencyCode
            .subscribe { currencyCode ->
                setResult(RESULT_OK, Intent().apply { putExtra(RESULT_KEY, currencyCode) })
                finish()
            }
            .addTo(disposableBag)
        lifecycle.addObserver(viewModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableBag.dispose()
    }

    companion object {
        val INPUT_AMOUNT = "INPUT_AMOUNT"
        val BASE_OR_TARGET_KEY = "BASE_OR_TARGET_KEY"
        val RESULT_KEY = "RESULT_KEY"
    }
}
