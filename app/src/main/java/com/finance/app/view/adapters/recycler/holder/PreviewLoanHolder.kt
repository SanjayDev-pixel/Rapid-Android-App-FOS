package com.finance.app.view.adapters.recycler.holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.databinding.PreviewLayoutLoanBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.LoanInfoModel
import com.finance.app.viewModel.AppDataViewModel

class PreviewLoanHolder(val binding: PreviewLayoutLoanBinding, val mContext: Context)
    : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(data: LoanInfoModel, viewModel: AppDataViewModel) {

        binding.tvLoanAmount.text = data.loanAmountRequest.toString()
        binding.tvTenure.text = data.tenure.toString()
        binding.tvAffordableEmi.text = data.affordableEMI.toString()
        binding.tvChannelPartnerName.text = data.channelPartnerName.toString()

        viewModel.getLoanProductNameFromId(data.productID, binding.tvLoanProduct)
        viewModel.getLoanPurposeNameFromId(data.productID, data.loanPurposeID, binding.tvLoanPurpose)

        viewModel.getMasterDropdownNameFromId(data.interestTypeTypeDetailID,
                AppEnums.DropdownMasterType.LoanInformationInterestType, binding.tvInterestType)

        viewModel.getMasterDropdownNameFromId(data.sourcingChannelPartnerTypeDetailID,
                AppEnums.DropdownMasterType.SourcingChannelPartner, binding.tvChannelPartner)

        //viewModel.getMasterDropdownNameFromId(data.channelPartnerDsaID,AppEnums.DropdownMasterType.ChannelPartnerName,binding.tvChannelPartnerName)

    }
}