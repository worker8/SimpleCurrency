package com.worker8.simplecurrency.ui.picker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.worker8.simplecurrency.R
import kotlinx.android.synthetic.main.row_picker.view.*

class PickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(pickerRowType: PickerAdapter.PickerRowType, callback: (String) -> Unit) {
        itemView.apply {
            pickerCurrencyName.text = pickerRowType.currencyName
            pickerCurrencyRate.text = pickerRowType.currencyRate
            pickerCurrencyCode.text = pickerRowType.currencyCode
            setOnClickListener { callback.invoke(pickerRowType.currencyCode) }
        }
    }

    companion object {
        fun create(parent: ViewGroup) =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_picker, parent, false)
                .let { PickerViewHolder(it) }
    }
}
