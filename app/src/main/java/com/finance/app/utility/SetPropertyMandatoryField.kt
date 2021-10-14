package com.finance.app.utility

import com.finance.app.databinding.FragmentPropertyInfoBinding

class SetPropertyMandatoryField(binding: FragmentPropertyInfoBinding) {

    init {
        ShowAsMandatory(binding.inputLayoutLandmark)
        ShowAsMandatory(binding.inputLayoutPinCode)
        ShowAsMandatory(binding.inputLayoutPropertyAddress)
        ShowAsMandatory(binding.inputLayoutNoOfTenants)
        ShowAsMandatory(binding.inputLayoutCashOcr)
        ShowAsMandatory(binding.inputLayoutOcr)
        ShowAsMandatory(binding.inputLayoutMvProperty)
        ShowAsMandatory(binding.inputLayoutAgreementValue)
        ShowAsMandatory(binding.inputLayoutPropertyArea)
    }
}