package com.cnm.umbrellaalarm.adapter

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.databinding.BindingAdapter

@BindingAdapter("bind:bindOnEditorActionListener")
fun bindOnEditorActionListener(editText: EditText, click: (() -> Unit)) {
    editText.setOnEditorActionListener { _, i, _ ->
        when (i) {
            EditorInfo.IME_ACTION_SEARCH -> {
                click()
            }
        }
        true
    }
}

