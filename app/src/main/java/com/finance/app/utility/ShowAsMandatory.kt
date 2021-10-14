package com.finance.app.utility

import android.graphics.Color
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.google.android.material.textfield.TextInputLayout

class ShowAsMandatory(field: TextInputLayout) {

    init {
        field.hint = buildSpannedString {
            append(field.hint)
            color(Color.RED) { append(" *") } // Mind the space prefix.
        }
    }
}