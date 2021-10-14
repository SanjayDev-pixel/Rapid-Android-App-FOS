package com.finance.app.view.adapters.recycler.holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.databinding.PreviewInnerLayoutObligationBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.ObligationDetail
import com.finance.app.viewModel.AppDataViewModel

class PreviewInnerObligationHolder(val binding: PreviewInnerLayoutObligationBinding, val mContext: Context)
    : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(list: ArrayList<ObligationDetail>?, pos: Int, viewModel: AppDataViewModel) {

        if (!list.isNullOrEmpty()) {
            setValueInCard(list[pos], viewModel)
        }
    }

    private fun setValueInCard(data: ObligationDetail, viewModel: AppDataViewModel) {
        binding.tvBalanceTenure.text = data.balanceTenure.toString()
        binding.tvDisbursementDate.text = data.loanDisbursementDate.toString()
        binding.tvEmi.text = data.emiAmount.toString()
        binding.tvEmiPaid.text = data.bounseEmiPaidInSameMonth.toString()
        binding.tvFinancerName.text = data.financerName.toString()
        binding.tvLoanAccNum.text = data.loanAccountNumber.toString()
        binding.tvLoanAmount.text = data.loanAmount.toString()
        binding.tvNumOfBouncesInNineMonths.text = data.numberOfBouncesInLastNineMonth.toString()
        binding.tvNumOfBouncesInSixMonths.text = data.numberOfBouncesInLastSixMonth.toString()
        binding.tvTenure.text = data.tenure.toString()

        viewModel.getMasterDropdownNameFromId(data.loanOwnershipTypeDetailID, AppEnums.DropdownMasterType.LoanOwnership,
                binding.tvLoanOwnership)
        viewModel.getMasterDropdownNameFromId(data.loanTypeTypeDetailID, AppEnums.DropdownMasterType.LoanType,
                binding.tvLoanType)
        viewModel.getMasterDropdownNameFromId(data.repaymentBankTypeDetailID, AppEnums.DropdownMasterType.RepaymentBank,
                binding.tvRepaymentBank)
    }
}