package com.finance.app.others

import android.widget.TextView
import motobeans.architecture.retrofit.response.Response.ResponseSample
import motobeans.architecture.util.exToDouble


fun List<ResponseSample>.totalAmountOfProducts(): Double {
    return getTotalAmount(this)
}

fun getTotalAmount(list: List<ResponseSample>): Double {
    var totalAmount = 0.0
    list.forEach { product -> totalAmount += product.message.exToDouble() }
    return totalAmount
}

fun TextView.setTextVertically(text: CharSequence?) {
    val textHolder: StringBuilder = StringBuilder("")
    text?.let {
        for (index in text.indices) {
            if (index != text.length - 1) {
                textHolder.append(text[index]).append("\n")
            } else {
                textHolder.append(text[index])
            }
        }
    }
    setText(textHolder)
}
