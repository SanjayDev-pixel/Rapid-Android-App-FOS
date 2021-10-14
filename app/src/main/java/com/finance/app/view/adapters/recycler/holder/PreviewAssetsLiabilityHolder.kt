package com.finance.app.view.adapters.recycler.holder

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.databinding.PreviewLayoutAssetsLiabilityBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AssetLiability
import com.finance.app.persistence.model.AssetLiabilityModel
import com.finance.app.persistence.model.CardDetail
import com.finance.app.persistence.model.ObligationDetail
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.adapters.recycler.adapter.PreviewInnerAdapter
import com.finance.app.viewModel.AppDataViewModel

class PreviewAssetsLiabilityHolder(val binding: PreviewLayoutAssetsLiabilityBinding, val mContext: FragmentActivity)
    : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(list: ArrayList<AssetLiabilityModel>?, pos: Int, viewModel: AppDataViewModel) {

        if (!list.isNullOrEmpty()) {
            try {
                setUpAssetsRecyclerView(list[pos].applicantAssetDetailList, viewModel)
                setUpCardsRecyclerView(list[pos].applicantCreditCardDetailList, viewModel)
                setUpObligationsRecyclerView(list[pos].applicantExistingObligationList, viewModel)
            }catch(e:IndexOutOfBoundsException){e.printStackTrace()}
            handleCollapse()
            var applicantType :String = ""
            if(LeadMetaData.getLeadData()?.personalData!!.applicantDetails[pos].isMainApplicant==true){
                applicantType =  "Applicant"
            }else{ applicantType="Co-Applicant"}
            binding.tvCoApplicant.setText(applicantType.plus("   : ").plus(LeadMetaData.getLeadData()?.personalData!!.applicantDetails[pos].firstName))
        }
    }

    private fun setUpAssetsRecyclerView(data: ArrayList<AssetLiability>, viewModel: AppDataViewModel) {
        val previewInnerAdapter = PreviewInnerAdapter(mContext,
                dataList = data, viewModel = viewModel, previewTypeEnums = AppEnums.PreviewType.ASSETS)
        binding.rcAssetLiability.adapter = previewInnerAdapter

    }

    private fun setUpCardsRecyclerView(data: ArrayList<CardDetail>, viewModel: AppDataViewModel) {
        val previewInnerAdapter = PreviewInnerAdapter(mContext,
                dataList = data, viewModel = viewModel, previewTypeEnums = AppEnums.PreviewType.CARD)
        binding.rcCard.adapter = previewInnerAdapter

    }

    private fun setUpObligationsRecyclerView(data: ArrayList<ObligationDetail>, viewModel: AppDataViewModel) {
        val previewInnerAdapter = PreviewInnerAdapter(mContext,
                dataList = data, viewModel = viewModel, previewTypeEnums = AppEnums.PreviewType.OBLIGATION)
        binding.rcObligations.adapter = previewInnerAdapter

    }

    private fun handleCollapse() {
        binding.collapseForm.setOnClickListener {
            binding.cardApplicant.visibility = View.GONE
            binding.collapseForm.visibility = View.GONE
            binding.expandForm.visibility = View.VISIBLE
        }

        binding.expandForm.setOnClickListener {
            binding.cardApplicant.visibility = View.VISIBLE
            binding.collapseForm.visibility = View.VISIBLE
            binding.expandForm.visibility = View.GONE
        }
    }

}