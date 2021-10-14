package com.finance.app.view.adapters.recycler.holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.databinding.PreviewInnerLayoutBankBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.BankDetailBean
import com.finance.app.viewModel.AppDataViewModel

class PreviewInnerBankHolder(val binding: PreviewInnerLayoutBankBinding, val mContext: Context)
    : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(list: ArrayList<BankDetailBean>?, pos: Int, viewModel: AppDataViewModel) {

        if (!list.isNullOrEmpty()) {
                setValueInCard(list[pos], viewModel)
        }
    }

    private fun setValueInCard(data: BankDetailBean, viewModel: AppDataViewModel) {
        binding.tvBankName.text = data.bankName
        binding.tvAccountNum.text = data.accountNumber
        binding.tvAccountHolderName.text = data.accountHolderName

        viewModel.getMasterDropdownNameFromId(data.accountTypeDetailID, AppEnums.DropdownMasterType.AccountType,
                binding.tvAccountType)
        viewModel.getMasterDropdownNameFromId(data.salaryCreditTypeDetailID, AppEnums.DropdownMasterType.SalaryCredit,
                binding.tvSalaryCredit)
    }
}