package com.finance.app.view.adapters.recycler.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemReferenceBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.ReferenceModel
import com.finance.app.utility.LeadMetaData

class ReferenceAdapter(private val c: Context, private val referencesList: ArrayList<ReferenceModel>?) : RecyclerView.Adapter<ReferenceAdapter.ReferenceViewHolder>() {
    private lateinit var binding: ItemReferenceBinding
    private var mOnItemClickListener: ItemClickListener? = null

    interface ItemClickListener {
        fun onReferenceDetailEditClicked(position: Int, reference: ReferenceModel)
        fun onReferenceDetailDeleteClicked(position: Int)
    }

    inner class ReferenceViewHolder(val binding: ItemReferenceBinding, val c: Context) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindItems(position: Int, reference: ReferenceModel) {
            binding.tvName.text = reference.name
            binding.tvRelation.text = reference.relationTypeName
            binding.tvOccupation.text = reference.occupationTypeName
            binding.tvAddress.text = reference.addressBean?.address1
            binding.tvPincode.text = reference.addressBean?.zip
            binding.tvCityAndDistrict.text = "${reference.addressBean?.cityName}, ${reference.addressBean?.districtName}, ${reference.addressBean?.stateName}"
            binding.tvContact.text = reference.contactNumber
            binding.tvKnownSince.text = reference.knowSince



            addClickListener(position, reference)
            LeadMetaData.getLeadData()?.let { if(it.status.equals(AppEnums.LEAD_TYPE.SUBMITTED.type,true)) {
                binding.ivDelete.visibility = View.GONE
                binding.ivEdit.visibility = View.GONE
            }else
            {
                binding.ivDelete.visibility = View.VISIBLE
                binding.ivEdit.visibility = View.VISIBLE
            }
            }

        }

        private fun addClickListener(position: Int, reference: ReferenceModel) {
            binding.ivDelete.setOnClickListener {
                mOnItemClickListener?.onReferenceDetailDeleteClicked(position)
            }

            binding.ivEdit.setOnClickListener {
                mOnItemClickListener?.onReferenceDetailEditClicked(position, reference)
            }
        }
    }

    override fun getItemCount() = referencesList?.size ?: 0

    fun setOnItemClickListener(listener: ItemClickListener) {
        mOnItemClickListener = listener
    }

    fun addItem(position: Int = 0, referenceDetail: ReferenceModel) {
        referencesList?.let {
            it.add(position, referenceDetail)
            notifyDataSetChanged()
        }
    }

    fun updateItem(position: Int, referenceDetail: ReferenceModel) {
        referencesList?.let {
            if (position >= 0 && position < it.size) {
                it[position] = referenceDetail
                notifyDataSetChanged()
            }
        }
    }

    fun deleteItem(position: Int) {
        referencesList?.let {
            referencesList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReferenceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_reference, parent, false)
        return ReferenceViewHolder(binding, c)
    }

    override fun onBindViewHolder(holder: ReferenceViewHolder, position: Int) {
        if (!referencesList.isNullOrEmpty()) {
            holder.bindItems(position, referencesList[position])
        }
    }


}
