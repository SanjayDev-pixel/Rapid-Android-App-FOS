package com.finance.app.view.adapters.arrayadapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.finance.app.R

class CustomSpinnerAdapter<T>(mContext: Context, private val resourceId: Int,
                              val data: ArrayList<T>) : ArrayAdapter<T>(mContext, resourceId, data) {

    private var inflater: LayoutInflater = mContext.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override
    fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup) = getCustomView(position, parent)

    override
    fun getView(position: Int, convertView: View?, parent: ViewGroup) = getCustomView(position, parent)

    fun getCustomView(position: Int, parent: ViewGroup): View {

        val row = inflater.inflate(resourceId, parent, false)
        val tempValue = data[position]
        val tvTitle = row.findViewById(R.id.tvSpinnerValue) as TextView?
        row.tag = tempValue
        val myTypeface = Typeface.createFromAsset(context.assets, "fonts/Raleway-Bold.ttf")
        tvTitle?.typeface = myTypeface
        tvTitle?.text = tempValue.toString()

        return row
    }
}