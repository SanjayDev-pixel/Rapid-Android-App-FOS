package com.finance.app.view.adapters.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemLeadsBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.view.adapters.recycler.holder.LeadsListingHolder

class LeadListingAdapter(private val mContext: FragmentActivity, private val leadList: ArrayList<AllLeadMaster>, private val leadStatusEnums: AppEnums.LEAD_TYPE)
    : RecyclerView.Adapter<LeadsListingHolder>() {

    fun updateAdapterList(list: ArrayList<AllLeadMaster>) {
        leadList.clear()
        leadList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount() = leadList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeadsListingHolder {
        val layoutInflater = LayoutInflater.from(mContext)
        val binding = DataBindingUtil.inflate<ItemLeadsBinding>(layoutInflater, R.layout.item_leads, parent, false)
        return LeadsListingHolder(binding, mContext)
    }

    override fun onBindViewHolder(holder: LeadsListingHolder, position: Int) {
        holder.bindItems(leadList[position], leadStatusEnums)
    }
}
