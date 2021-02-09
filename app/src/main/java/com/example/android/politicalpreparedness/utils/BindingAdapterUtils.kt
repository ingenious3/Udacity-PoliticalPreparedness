package com.example.android.politicalpreparedness.utils

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.representative.model.Representative

@BindingAdapter("isVisible")
fun setEmptyTextViewVisibility(view: TextView, listData: List<Any>?) {
    if (listData != null && listData.isNotEmpty()) {
        view.visibility = View.GONE
    } else {
        view.visibility = View.VISIBLE
    }
}
