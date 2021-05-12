package com.example.mviexercise.ui.login

import android.text.Editable
import android.text.TextWatcher

internal class AppTextWatcher (
    private val actionBlock: (() -> Unit),
    private val preBlock: (() -> Unit)? = null,
    private val postBlock: (() -> Unit)? = null
) : TextWatcher {

    override fun afterTextChanged(p0: Editable?) {
        preBlock?.let { it() }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        postBlock?.let { it() }
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        actionBlock()
    }
}
