package com.worker8.simplecurrency.ui.picker

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.widget.textChanges
import com.worker8.simplecurrency.R
import com.worker8.simplecurrency.common.addTo
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_picker.*
import javax.inject.Inject

class PickerActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var repo: PickerRepo

    private val adapter by lazy { PickerAdapter(isBase) }
    private val disposableBag = CompositeDisposable()

    val isBase get() = intent.getBooleanExtra(BASE_OR_TARGET_KEY, true)
    val inputAmount get() = intent.getDoubleExtra(INPUT_AMOUNT, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        val inputLocal = object : PickerContract.Input {
            override val inputAmount = this@PickerActivity.inputAmount
            override val onFilterTextChanged =
                pickerInput.textChanges().map { it.toString() }
            override val isBase = this@PickerActivity.isBase
        }
        val viewActionLocal = object : PickerContract.ViewAction {
            override fun showTerminalError() {
                Snackbar.make(pickerContainer, R.string.error_message, Snackbar.LENGTH_INDEFINITE)
                    .show()
            }
        }
        pickerRecyclerView.adapter = adapter
        val viewModel =
            ViewModelProviders.of(this, PickerViewModel.PickerViewModelFactory(repo))
                .get(PickerViewModel::class.java)
                .apply {
                    input = inputLocal
                    viewAction = viewActionLocal
                }

        viewModel.screenState
            .distinctUntilChanged()
            .observeOn(repo.mainThread)
            .subscribe({ screenState ->
                screenState.apply {
                    adapter.submitList(currencyList.toList())
                    pickerLastUpdatedMessage.text =
                        "${getString(R.string.last_updated)} ${latestUpdatedString}"
                }
            }, {
                viewActionLocal.showTerminalError()
            })
            .addTo(disposableBag)

        adapter.selectedCurrencyCode
            .subscribe({ currencyCode ->
                setResult(RESULT_OK, Intent().apply { putExtra(RESULT_KEY, currencyCode) })
                finish()
            }
                , {
                    viewActionLocal.showTerminalError()
                })
            .addTo(disposableBag)
        lifecycle.addObserver(viewModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableBag.dispose()
    }

    companion object {
        const val INPUT_AMOUNT = "INPUT_AMOUNT"
        const val BASE_OR_TARGET_KEY = "BASE_OR_TARGET_KEY"
        const val RESULT_KEY = "RESULT_KEY"
    }
}
