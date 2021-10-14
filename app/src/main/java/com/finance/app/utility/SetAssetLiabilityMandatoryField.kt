package com.finance.app.utility

import com.finance.app.databinding.FragmentAssetLiablityBinding

class SetAssetLiabilityMandatoryField(binding: FragmentAssetLiablityBinding) {

    init {
        ShowAsMandatory(binding.layoutObligations.inputLayoutBalanceTenure)
        ShowAsMandatory(binding.layoutObligations.inputLayoutFinancierName)
        ShowAsMandatory(binding.layoutObligations.inputLayoutLoanAmount)
        ShowAsMandatory(binding.layoutObligations.inputLayoutAccountNum)
        ShowAsMandatory(binding.layoutObligations.inputLayoutTenure)
        ShowAsMandatory(binding.layoutObligations.inputLayoutEmiAmount)
        ShowAsMandatory(binding.layoutObligations.inputLayoutBouncesInLastSixMonths)
        ShowAsMandatory(binding.layoutObligations.inputLayoutBouncesInLastNineMonths)
        ShowAsMandatory(binding.layoutCreditCard.inputLayoutCreditCardLimit)
        ShowAsMandatory(binding.layoutCreditCard.inputLayoutCurrentUtilization)
        ShowAsMandatory(binding.inputLayoutValue)
    }
}