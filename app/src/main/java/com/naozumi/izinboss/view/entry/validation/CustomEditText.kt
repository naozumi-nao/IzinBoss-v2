package com.naozumi.izinboss.view.entry.validation

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.naozumi.izinboss.R
import com.naozumi.izinboss.model.util.ViewUtils

class CustomEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        when (id) {
            R.id.ed_register_password, R.id.ed_login_password -> {
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        //Do Nothing
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (s.toString().length < 8) {
                            setError(context.getString(R.string.pass_less_than_8_char), null)
                        } else {
                            error = null
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                        // Do Nothing
                    }
                })
            }
            R.id.ed_register_email, R.id.ed_login_email -> {
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        //Do Nothing
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (!ViewUtils.isValidEmail(s.toString())) {
                            setError(context.getString(R.string.invalid_email), null)
                        } else {
                            error = null
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                        // Do Nothing
                    }
                })
            }
        }
    }
}