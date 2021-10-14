package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.DeviationListItemBinding
import com.finance.app.persistence.model.DeviationList


class LoanSubmitStatusAdapter(private val c: Context, private val deviationList:ArrayList<DeviationList>) : RecyclerView.Adapter<LoanSubmitStatusAdapter.LoanSubmitViewHolder>() {
    private lateinit var binding: DeviationListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanSubmitViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.deviation_list_item, parent, false)
        return LoanSubmitViewHolder(binding, c)
    }

    override fun getItemCount() = deviationList.size


    fun getItemList(): ArrayList<DeviationList> {
        return deviationList
    }

    override fun onBindViewHolder(holder: LoanSubmitStatusAdapter.LoanSubmitViewHolder, position: Int) {
        holder.bindItems(position,deviationList[position] )
    }

    inner class LoanSubmitViewHolder(val binding: DeviationListItemBinding, val c: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(position: Int, deviation: DeviationList) {
            binding.deviationheadingText.text=deviation.head
            binding.deviationtext.text=deviation.deviation
            binding.approvalAuthority.text =deviation.aprvlAuth
            binding.migrationfact.text=deviation.multigatingFact

        }
    }
}












