package com.finance.app.view.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log

class EditTexNormal : androidx.appcompat.widget.AppCompatEditText {
    constructor(context: Context) : super(context) {
        init(null)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }
    /*constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }*/
    private fun init(context: Context?) {
        try {
            if (context != null) {
                val myTypeface = Typeface.createFromAsset(context.assets, "fonts/montserrat.otf")
                typeface = myTypeface

            }
        } catch (e: Exception) {

            Log.e(TAG, e.message, e)
        }

    }
    companion object {
        private val TAG = "TextViewBold"
    }
}