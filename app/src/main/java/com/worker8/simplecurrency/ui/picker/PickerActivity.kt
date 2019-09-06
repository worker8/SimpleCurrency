package com.worker8.simplecurrency.ui.picker

import android.os.Bundle
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

        }
        pickerRecyclerView.adapter = adapter
        val viewModel =
            ViewModelProviders.of(this, PickerViewModel.PickerViewModelFactory(input, repo))
                .get(PickerViewModel::class.java)
        viewModel.screenState
            .observeOn(repo.schedulerSharedRepo.mainThread)
            .subscribe {
                adapter.submitList(it.currencyList)
            }
            .addTo(disposableBag)
        lifecycle.addObserver(viewModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableBag.dispose()
    }
}
