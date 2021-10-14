package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemAssetBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.AssetLiability
import com.finance.app.utility.LeadMetaData
import motobeans.architecture.development.interfaces.DataBaseUtil
import javax.inject.Inject

class AssetDetailAdapter(private val c: Context , private val assets: ArrayList<AssetLiability> , allMasterDropDown: AllMasterDropDown?) : RecyclerView.Adapter<AssetDetailAdapter.AssetDetailViewHolder>() {
    private lateinit var binding: ItemAssetBinding
    private var mOnAssetClickListener: AssetClickListener? = null
    @Inject
    lateinit var dataBase: DataBaseUtil
    private var allMasterDropDown1: AllMasterDropDown? = allMasterDropDown

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): AssetDetailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater , R.layout.item_asset , parent , false)
        return AssetDetailViewHolder(binding , c , allMasterDropDown1)
    }

    override fun getItemCount() = assets.size

    fun setOnAssetClickListener(listner: AssetClickListener) {
        mOnAssetClickListener = listner
    }

    interface AssetClickListener {
        fun onAssetDeleteClicked(position: Int)
        fun onAssetEditClicked(position: Int , asset: AssetLiability)
    }


    fun getItemList(): ArrayList<AssetLiability> {
        return assets
    }

    fun addItem(position: Int = 0 , assetsDetail: AssetLiability) {
        assets.let {
            it.add(position , assetsDetail)
            notifyDataSetChanged()
        }
    }

    fun updateItem(position: Int , assetDetail: AssetLiability) {
        assets.let {
            if (position >= 0 && position <= it.size) {
                it[position] = assetDetail
                notifyDataSetChanged()
            }
        }

    }

    fun deleteItem(position: Int) {
        assets.let {
            assets.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: AssetDetailViewHolder , position: Int) {
        holder.bindItems(position , assets[position])
    }

    inner class AssetDetailViewHolder(val binding: ItemAssetBinding , val c: Context , allMasterDropDownNew: AllMasterDropDown?) : RecyclerView.ViewHolder(binding.root) {


        fun bindItems(position: Int , asset: AssetLiability) {
            asset.assetValue?.let { binding.tvValue.text = it.toString() }
            //binding.tvOwnership.text=asset.ownershipTypeDetailID
            //allMasterDropDown1?.AssetOwnership

            for (i in 0 until allMasterDropDown1?.AssetOwnership!!.size) {

                if (asset.ownershipTypeDetailID == allMasterDropDown1!!.AssetOwnership?.get(i)?.typeDetailID) {

                    binding.tvOwnership.text = allMasterDropDown1!!.AssetOwnership?.get(i)?.typeDetailCode

                }

            }
            for (i in 0 until allMasterDropDown1?.AssetDetail!!.size) {

                if (asset.assetDetailsID == allMasterDropDown1!!.AssetDetail?.get(i)?.typeDetailID) {

                    binding.tvAssetType.text = allMasterDropDown1!!.AssetDetail?.get(i)?.typeDetailCode

                }

            }

            for (i in 0 until allMasterDropDown1?.AssetSubType!!.size) {

                if (asset.subTypeOfAssetTypeDetailID == allMasterDropDown1!!.AssetSubType?.get(i)?.typeDetailID) {

                    binding.tvSubType.text = allMasterDropDown1!!.AssetSubType?.get(i)?.typeDetailCode

                }

            }

            LeadMetaData.getLeadData()?.let {
                if (it.status.equals(AppEnums.LEAD_TYPE.SUBMITTED.type , true)) {
                    binding.btnDelete.visibility = View.GONE
                    binding.btnEdit.visibility = View.GONE
                } else {
                    binding.btnDelete.visibility = View.VISIBLE
                    binding.btnEdit.visibility = View.VISIBLE
                }
            }


            addClickListener(position , asset)
        }

        private fun addClickListener(position: Int , asset: AssetLiability) {
            binding.btnDelete.setOnClickListener {
                mOnAssetClickListener!!.onAssetDeleteClicked(position)

            }

            binding.btnEdit.setOnClickListener {
                mOnAssetClickListener!!.onAssetEditClicked(position , asset)


            }
        }
    }
}
