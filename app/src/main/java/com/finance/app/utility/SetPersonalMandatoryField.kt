package com.finance.app.utility

import com.finance.app.databinding.LayoutCustomViewPersonalBinding

class SetPersonalMandatoryField(binding: LayoutCustomViewPersonalBinding) {

    init {
        ShowAsMandatory(binding.basicInfoLayout.inputLayoutFirstName)
        ShowAsMandatory(binding.basicInfoLayout.inputLayoutDob)
        ShowAsMandatory(binding.basicInfoLayout.inputLayoutFatherFirstName)
        ShowAsMandatory(binding.basicInfoLayout.inputLayoutSpouseFirstName)
        ShowAsMandatory(binding.personalAddressLayout.inputLayoutCurrentAddress)
        ShowAsMandatory(binding.personalAddressLayout.inputLayoutPermanentAddress)
        ShowAsMandatory(binding.personalAddressLayout.inputLayoutCurrentLandmark)
        ShowAsMandatory(binding.personalAddressLayout.inputLayoutPermanentLandmark)
        ShowAsMandatory(binding.personalAddressLayout.inputLayoutCurrentStaying)
        ShowAsMandatory(binding.personalAddressLayout.inputLayoutPermanentStaying)
        ShowAsMandatory(binding.personalAddressLayout.inputLayoutCurrentRentAmount)
        ShowAsMandatory(binding.basicInfoLayout.inputLayoutMobile)
        ShowAsMandatory(binding.basicInfoLayout.inputLayoutNoOffamilymembers)
    }
}