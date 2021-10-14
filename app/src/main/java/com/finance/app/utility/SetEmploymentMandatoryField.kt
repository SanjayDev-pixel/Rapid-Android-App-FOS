package com.finance.app.utility

import com.finance.app.databinding.FragmentEmploymentFormBinding
import com.finance.app.databinding.LayoutSalaryBinding
import com.finance.app.databinding.LayoutSenpBinding

class SetEmploymentMandatoryField(binding: FragmentEmploymentFormBinding) {

    init {
        setSalaryMandatoryField(binding.lytSalaryDetail)
        setSenpMandatoryField(binding.lytBusinessDetail)
    }

    private fun setSenpMandatoryField(binding: LayoutSenpBinding) {
        ShowAsMandatory(binding.inputBusinessName)
        ShowAsMandatory(binding.inputLayoutIncorporationDate)
        //ShowAsMandatory(binding.inputGstRegistration)
        ShowAsMandatory(binding.inputLayoutBusinessVintage)
        ShowAsMandatory(binding.layoutAddress.inputLayoutAddress)
        ShowAsMandatory(binding.layoutAddress.inputLayoutLandmark)
        ShowAsMandatory(binding.layoutAddress.inputLayoutContact)
        ShowAsMandatory(binding.inputLayoutLastYearIncome)
        ShowAsMandatory(binding.inputLayoutCurrentYearIncome)
        ShowAsMandatory(binding.inputMonthlyIncome)
    }

    private fun setSalaryMandatoryField(binding: LayoutSalaryBinding) {
        ShowAsMandatory(binding.inputCompanyName)
        ShowAsMandatory(binding.inputDesignation)
       // ShowAsMandatory(binding.inputLayoutEmployeeId)
        ShowAsMandatory(binding.inputLayoutJoiningDate)
        ShowAsMandatory(binding.layoutAddress.inputLayoutAddress)
        ShowAsMandatory(binding.layoutAddress.inputLayoutLandmark)
        ShowAsMandatory(binding.layoutAddress.inputLayoutContact)
        ShowAsMandatory(binding.inputLayoutGrossIncome)
        ShowAsMandatory(binding.inputLayoutTotalExperience)
        ShowAsMandatory(binding.inputLayoutDeduction)
    }
}