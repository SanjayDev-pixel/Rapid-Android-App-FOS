package com.example.motobeans.educationapp.app.view.adapter.arrayadapter

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.finance.app.R


/**
 * Created by munishkumarthakur on 25/01/18.
 */

/***** Adapter class extends with ArrayAdapter  */
class CustomTempAdapter(val context1: Context, val resourceId: Int,
    val data: MutableList<String>) :
    ArrayAdapter<String>(context1, resourceId, data) {

    companion object {
        val TAG_DATA = "tagData"
    }

    private var inflater: LayoutInflater = context1.getSystemService(
        Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    init {
        data.add(0, "Select")
    }

    override
    fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override
    fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

/*
  override fun getViewTypeCount(): Int {
    return data.size
  }*/

    // This funtion called for each row ( Called data.size() times )
    fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {

        /********** Inflate spinner_rows.xml file for each row ( Defined below )  */
        val row = inflater.inflate(R.layout.item_custom_spinner, parent, false)

        /***** Get each Model object from Arraylist  */
        val tempValue = data.get(position)

        val tvTitle = row.findViewById(R.id.tvSpinnerValue) as TextView

        // Set values for spinner each row
        tvTitle.text = tempValue

        row.tag = tempValue

        when (position == 0) {
            true -> {
                tvTitle.setTextColor(ContextCompat.getColor(context1, R.color.divider))
                tvTitle.setTypeface(null, Typeface.NORMAL)
            }
        }

        return row
    }
}