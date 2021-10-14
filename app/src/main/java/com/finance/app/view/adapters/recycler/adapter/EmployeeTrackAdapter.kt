package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.persistence.model.EmployeeByManager
import java.util.*


class EmployeeTrackAdapter (val context: Context, private val arrayListEmployeeTrack : ArrayList<EmployeeByManager>,private val onItemClickListeners: OnItemClickListeners) : RecyclerView.Adapter<EmployeeTrackAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeTrackAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.employee_item_design, parent, false)
        return ViewHolder(v,onItemClickListeners)
    }


    override fun getItemCount(): Int {
        return  arrayListEmployeeTrack.size
    }

    override fun onBindViewHolder(holder: EmployeeTrackAdapter.ViewHolder , position: Int) {
        holder.bindItems( arrayListEmployeeTrack[position])
    }
    inner class ViewHolder(itemView: View,onItemClickListeners: OnItemClickListeners) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(employeeByManager: EmployeeByManager) {
            val employeeNameFisrtLetter = itemView.findViewById<TextView>(R.id.txtNameFirstLetter)
            val employeeNmae_tv = itemView.findViewById<TextView>(R.id.employee_name)
            val employee_ll = itemView.findViewById<LinearLayout>(R.id.ll_employee_track)
            employeeNmae_tv.text=employeeByManager.employeeName
            employeeNameFisrtLetter.text = employeeByManager.employeeName?.dropLast(employeeByManager.employeeName!!.length-1)
            val r = Random()
            val red = r.nextInt(255 - 0 + 1) + 0
            val green = r.nextInt(255 - 0 + 1) + 0
            val blue = r.nextInt(255 - 0 + 1) + 0

            val draw = GradientDrawable()
            draw.shape = GradientDrawable.OVAL
            draw.setSize(140,140)
            draw.setColor(Color.rgb(red, green, blue))
            employeeNameFisrtLetter.setBackground(draw)
            itemView.setOnClickListener {
                onItemClickListeners.onItemClick(adapterPosition)
               // itemView.setBackgroundColor(Color.parseColor("#9013334C"))
                //itemView.background=R.drawable.circle.toDrawable()

            }
           // itemView.setBackgroundColor(Color.parseColor("#ffffff"))

        }

    }
    interface OnItemClickListeners {
        fun onItemClick(position: Int)
    }
}