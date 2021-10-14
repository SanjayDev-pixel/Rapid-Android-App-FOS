package com.finance.app.utility

import com.finance.app.databinding.ActivityLeadCreateBinding

class SetCreateLeadMandatoryField(binding: ActivityLeadCreateBinding) {

    init {
        ShowAsMandatory(binding.inputLayoutApplicantFirstName)
        ShowAsMandatory(binding.inputLayoutContactNum)
        ShowAsMandatory(binding.inputLayoutAddress)
        ShowAsMandatory(binding.inputLayoutLoanAmount)
    }
}