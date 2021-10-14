package com.finance.app.utility

import com.finance.app.databinding.FragmentLoanInformationBinding

class SetLoanInfoMandatoryField(binding: FragmentLoanInformationBinding) {

    init {
        ShowAsMandatory(binding.inputLayoutAmountRequest)
        ShowAsMandatory(binding.inputLayoutTenure)
        ShowAsMandatory(binding.inputLayoutEmi)
    }
}