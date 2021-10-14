package com.finance.app.view.adapters.recycler.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.finance.app.R
import motobeans.architecture.retrofit.response.Response

class LoanPurposeSpinnerAdapter(context1: Context, val value: ArrayList<Response.LoanPurpose>?,
                                val isMandatory: Boolean = false) : BaseAdapter() {

    private var inflater: LayoutInflater = context1.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private lateinit var spinnerValue: Response.LoanPurpose

    override fun getItem(position: Int): Any? {
        return value?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return value?.size ?: 0
    }

    override
    fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        spinnerValue = value?.get(position)?: value?.get(0)!!

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_custom_spinner, parent, false)
            val textView = view.findViewById<View>(R.id.tvSpinnerValue) as TextView
            textView.text = spinnerValue.loanPurposeName
        } else {
            view = convertView
        }
        return view
    }

    override
    fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }
}