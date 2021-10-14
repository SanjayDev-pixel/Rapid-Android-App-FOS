package com.finance.app.view.adapters.recycler.holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.databinding.PreviewInnerLayoutAssetBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AssetLiability
import com.finance.app.viewModel.AppDataViewModel

class PreviewInnerAssetHolder(val binding: PreviewInnerLayoutAssetBinding , val mContext: Context) : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(list: ArrayList<AssetLiability>? , pos: Int , viewModel: AppDataViewModel) {

        if (!list.isNullOrEmpty()) {
            setValueInCard(list[pos] , viewModel)
        }
    }

    private fun setValueInCard(data: AssetLiability , viewModel: AppDataViewModel) {

        viewModel.getMasterDropdownNameFromId(
                data.assetDetailsTypeDetailID , AppEnums.DropdownMasterType.AssetDetail ,
                binding.tvAssetType
        )

        viewModel.getMasterDropdownNameFromId(
                data.subTypeOfAssetTypeDetailID , AppEnums.DropdownMasterType.AssetSubType ,
                binding.tvAssetSubType
        )

        viewModel.getMasterDropdownNameFromId(
                data.ownershipTypeDetailID , AppEnums.DropdownMasterType.AssetOwnership ,
                binding.tvOwnership
        )

        viewModel.getMasterDropdownNameFromId(
                data.documentedProofTypeDetailID , AppEnums.DropdownMasterType.DocumentProof ,
                binding.tvDocumentProof
        )

        data.assetValue?.let { binding.tvValue.text = it.toString() }
    }
}