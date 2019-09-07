package com.worker8.simplecurrency.ui.picker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.worker8.simplecurrency.R
import com.worker8.simplecurrency.addTo
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_picker.*
import javax.inject.Inject

class PickerActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var repo: PickerRepo
    lateinit var input: PickerContract.Input
    val adapter = PickerAdapter()
    private val disposableBag = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        input = object : PickerContract.Input {
            override val isBase = intent.getBooleanExtra(BASE_OR_TARGET_KEY, true)
        }
        pickerRecyclerView.adapter = adapter
        val viewModel =
            ViewModelProviders.of(this, PickerViewModel.PickerViewModelFactory(input, repo))
                .get(PickerViewModel::class.java)

        viewModel.screenState
            .observeOn(repo.schedulerSharedRepo.mainThread)
            .subscribe {
                Log.d("ddw", "submitList, size = ${it.currencyList.size}")
                adapter.submitList(it.currencyList)
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
        val BASE_OR_TARGET_KEY = "BASE_OR_TARGET_KEY"
        val RESULT_KEY = "RESULT_KEY"
    }
}
