package com.worker8.simplecurrency.ui.picker

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class PickerActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var repo: PickerRepo
    lateinit var input: PickerContract.Input

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        input = object : PickerContract.Input {

        }

        val viewModel =
            ViewModelProviders.of(this, PickerViewModel.PickerViewModelFactory(input, repo))
                .get(PickerViewModel::class.java)

        lifecycle.addObserver(viewModel)
    }
}
