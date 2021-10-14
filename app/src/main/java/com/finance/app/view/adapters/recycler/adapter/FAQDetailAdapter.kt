package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.persistence.model.FAQ
import motobeans.architecture.retrofit.response.Response

class FAQDetailAdapter(val context: Context,private val arrayListFaq : ArrayList<FAQ>) : RecyclerView.Adapter<FAQDetailAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): FAQDetailAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.faq_item_design, parent, false)
            return ViewHolder(v)
        }


    override fun getItemCount(): Int {
        return arrayListFaq.size
    }

    override fun onBindViewHolder(holder: FAQDetailAdapter.ViewHolder , position: Int) {
        holder.bindItems(arrayListFaq[position])
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(faq: FAQ) {
            val question_textview = itemView.findViewById<TextView>(R.id.question_tv)
            val answers_textview = itemView.findViewById<TextView>(R.id.answers_tv)
            val plus_imageview = itemView.findViewById<ImageView>(R.id.plus_iv)
            val minus_imageview = itemView.findViewById<ImageView>(R.id.minus_iv)
            question_textview.text = faq.question
            answers_textview.text = faq.answer
            plus_imageview.setOnClickListener {
                answers_textview.visibility = View.VISIBLE
                minus_imageview.visibility = View.VISIBLE
                plus_imageview.visibility = View.GONE
            }
            minus_imageview.setOnClickListener {
                answers_textview.visibility = View.GONE
                minus_imageview.visibility = View.GONE
                plus_imageview.visibility = View.VISIBLE
            }
        }
    }
}