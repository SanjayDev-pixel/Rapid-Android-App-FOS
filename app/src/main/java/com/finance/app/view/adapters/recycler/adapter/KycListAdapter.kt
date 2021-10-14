package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemKycBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.KYCDetail
import com.finance.app.utility.ConvertDate
import com.finance.app.utility.LeadMetaData

class KycListAdapter(private val context: Context , private val kycList: ArrayList<KYCDetail>) : RecyclerView.Adapter<KycListAdapter.KycViewHolder>() {

    private var listSize: MutableLiveData<Int>? = MutableLiveData()
    private var itemClickListener: ItemClickListener? = null

    interface ItemClickListener {
        fun onKycDetailDeleteClicked(position: Int)
        fun onKycDetailEditClicked(position: Int , kycDetail: KYCDetail)
    }

    inner class KycViewHolder(val binding: ItemKycBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(position: Int , kyc: KYCDetail) {
            binding.tvIdType.text = kyc.identificationTypeDetail.toString()
            binding.tvIdNum.text = kyc.identificationNumber
            binding.tvVerifiedStatus.text = kyc.verifiedStatusTypeDetail.toString()
            binding.tvIssueDate.text = ConvertDate().convertToAppFormat(kyc.issueDate)
            binding.tvExpiryDate.text = ConvertDate().convertToAppFormat(kyc.expireDate)
            LeadMetaData.getLeadData()?.let {
                if (it.status.equals(AppEnums.LEAD_TYPE.SUBMITTED.type , true)) {
                    binding.ivDelete.visibility = View.GONE
                    binding.ivEdit.visibility = View.GONE
                } else {
                    binding.ivDelete.visibility = View.VISIBLE
                    binding.ivEdit.visibility = View.VISIBLE
                }
            }

            addClickListener(position , kyc)

        }

        private fun addClickListener(position: Int , kycDetail: KYCDetail) {
            binding.ivDelete.setOnClickListener {
                itemClickListener?.onKycDetailDeleteClicked(position)
            }

            binding.ivEdit.setOnClickListener {
                itemClickListener?.onKycDetailEditClicked(position , kycDetail)
            }
        }

    }

    fun setOnItemClickListener(listener: ItemClickListener) {
        itemClickListener = listener
    }

    fun getItem(position: Int): KYCDetail {
        return kycList[position]
    }

    fun addItem(kycDetail: KYCDetail) {
        kycList.add(0 , kycDetail)
        notifyDataSetChanged()
        listSize?.postValue(itemCount)
    }

    fun updateItem(position: Int , kycDetail: KYCDetail) {
        if (position >= 0 && position < kycList.size) {
            kycList[position] = kycDetail
            notifyDataSetChanged()
            listSize?.postValue(itemCount)
        }
    }

    fun deleteItem(position: Int) {
        if (position >= 0 && position <= kycList.size) {
            kycList.removeAt(position)
            notifyDataSetChanged()
            listSize?.postValue(itemCount)
        }
    }

    fun getItemList() = kycList

    fun getItemCountObserver() = listSize

    override fun getItemCount(): Int = kycList.size

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): KycViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return KycViewHolder(DataBindingUtil.inflate(layoutInflater , R.layout.item_kyc , parent , false))
    }

    override fun onBindViewHolder(holder: KycViewHolder , position: Int) {
        holder.bindItems(position , kycList[position])
    }

}
