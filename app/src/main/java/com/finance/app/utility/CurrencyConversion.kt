package com.finance.app.utility

import android.text.Editable
import android.text.TextWatcher
import com.finance.app.view.utils.EditTexNormal
import com.google.android.material.textfield.TextInputEditText
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class CurrencyConversion {

    fun convertToCurrencyType(editText: TextInputEditText) {
        editText.addTextChangedListener(onTextChangedListener(editText))
    }
    fun convertToCurrencyType(editText: EditTexNormal) {
        editText.addTextChangedListener(onTextChangedListener(editText))
    }
    fun convertToNormalValue(value: String): String {
        return if (value.contains(",")) {
            value.replace(",".toRegex(), "").trim()
        } else {
            value.trim()
        }
    }

    private fun onTextChangedListener(editText: TextInputEditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                editText.removeTextChangedListener(this)

                try {
                    var originalString = s.toString()

                    val longval: Long?
                    if (originalString.contains(",")) {
                        originalString = originalString.replace(",".toRegex(), "")
                    }
                    longval = java.lang.Long.parseLong(originalString)

                    val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
                    formatter.applyPattern("#,###,###,###")
                    val formattedString = formatter.format(longval)

                    //setting text after format to EditText
                    editText.setText(formattedString)
                    editText.setSelection(editText.text!!.length)
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }

                editText.addTextChangedListener(this)
            }
        }
    }
    private fun onTextChangedListener(editText: EditTexNormal): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                editText.removeTextChangedListener(this)

                try {
                    var originalString = s.toString()

                    val longval: Long?
                    if (originalString.contains(",")) {
                        originalString = originalString.replace(",".toRegex(), "")
                    }
                    longval = java.lang.Long.parseLong(originalString)

                    val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
                    formatter.applyPattern("#,###,###,###")
                    val formattedString = formatter.format(longval)

                    //setting text after format to EditText
                    editText.setText(formattedString)
                    editText.setSelection(editText.text!!.length)
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }

                editText.addTextChangedListener(this)
            }
        }
    }
}