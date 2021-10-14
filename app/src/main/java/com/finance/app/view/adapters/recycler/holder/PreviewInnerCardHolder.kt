package com.finance.app.view.adapters.recycler.holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.databinding.PreviewInnerLayoutCreditCardBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.CardDetail
import com.finance.app.viewModel.AppDataViewModel

class PreviewInnerCardHolder(val binding: PreviewInnerLayoutCreditCardBinding , val mContext: Context) : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(list: ArrayList<CardDetail>? , pos: Int , viewModel: AppDataViewModel) {

        if (!list.isNullOrEmpty()) {
            setValueInCard(list[pos] , viewModel)
        }
    }

    private fun setValueInCard(data: CardDetail , viewModel: AppDataViewModel) {
        data.cardLimit?.let { binding.tvCreditCardLimit.text = it.toString() }
        data.currentUtilization?.let { binding.tvCurrentUtilization.text = it.toString() }
        binding.tvLastPaymentDate.text = data.lastPaymentDate.toString()

        viewModel.getMasterDropdownNameFromId(
                data.bankNameTypeDetailID , AppEnums.DropdownMasterType.BankName ,
                binding.tvBankName
        )
        viewModel.getMasterDropdownNameFromId(
                data.obligateTypeDetail , AppEnums.DropdownMasterType.CreditCardObligation ,
                binding.tvObligate
        )
    }
}