package com.naozumi.izinboss.model.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView

object TextInputUtils {
    fun createTextWatcherWithButton( //Disables Button if Input Fields are Blank
        button: Button?,
        vararg textview: TextView?
    ): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val isFormFilled = textview.all { it?.text?.isNotBlank() ?: false }
                button?.isEnabled = isFormFilled
            }
        }
    }
}
