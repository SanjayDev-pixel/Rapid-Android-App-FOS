package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.DeviationListItemBinding
import com.finance.app.databinding.RejectionListItemBinding
import com.finance.app.persistence.model.RejectionList

class LoanRejectionAdapter  (private val c: Context, private val rejectionList:ArrayList<RejectionList>) : RecyclerView.Adapter<LoanRejectionAdapter.LoanSubmitViewHolder>() {
    private lateinit var binding: RejectionListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanSubmitViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.rejection_list_item, parent, false)
        return LoanSubmitViewHolder(binding, c)
    }

    override fun getItemCount() = rejectionList.size

    fun getItemList(): ArrayList<RejectionList> {
        return rejectionList
    }

    override fun onBindViewHolder(holder: LoanRejectionAdapter.LoanSubmitViewHolder, position: Int) {
        holder.bindItems(position,rejectionList[position] )
    }
    inner class LoanSubmitViewHolder(val binding: RejectionListItemBinding, val c: Context) : RecyclerView.ViewHolder(binding.root) {


        fun bindItems(position: Int, rejection: RejectionList) {
           // binding.deviationheadingText.setText("Rejection")
            binding.name.text = rejection.bName
            binding.rejectionReason.text=rejection.rejectionReason

        }
    }
}

