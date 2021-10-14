package com.finance.app.utility

import android.text.InputType
import com.finance.app.databinding.ChannelpartnernewBinding
import com.finance.app.databinding.FragmentLoanInformationBinding
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.persistence.model.LoanProductMaster
import com.finance.app.persistence.model.LoanPurpose
import com.finance.app.view.customViews.CustomChannelPartnerView
import com.finance.app.view.customViews.CustomSpinnerView
import kotlinx.android.synthetic.main.custom_spinner.view.*
import kotlinx.android.synthetic.main.layout_channel_partner.view.*

class DisableLoanInfoForm(binding: FragmentLoanInformationBinding,
                          loanProduct: CustomSpinnerView<LoanProductMaster>,
                          loanScheme: CustomSpinnerView<DropdownMaster>,
                          interestType: CustomSpinnerView<DropdownMaster>,chanelPartnerView : CustomChannelPartnerView
                        ) {

    init {
        loanProduct.disableSelf()

        //loanPurpose.disableSelf()
        loanScheme.disableSelf()
        interestType.disableSelf()
        //sourcingPartnerName.disableSelf()

        binding.etApplicationNumber.inputType = InputType.TYPE_NULL
        binding.etAmountRequest.inputType = InputType.TYPE_NULL
        binding.etTenure.inputType = InputType.TYPE_NULL
        binding.etEmi.inputType = InputType.TYPE_NULL
        binding.cbPropertySelected.isClickable = false
        //binding.btnNext.isEnabled = false
        binding.viewChannelPartner.disableSelf()


    }
}