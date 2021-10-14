package com.finance.app.utility

import com.finance.app.databinding.LayoutCustomEmploymentViewBinding
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.view.customViews.CustomSpinnerView

class ClearEmploymentForm(private val binding: LayoutCustomEmploymentViewBinding) {

    fun clearAll() {
        clearSalaryForm()
        clearSenpForm()
    }

    fun clearSalaryForm(salarySpinnerList: ArrayList<CustomSpinnerView<DropdownMaster>>? = null) {
        binding.layoutSalary.cbIsPensioner.isChecked = false
        binding.layoutSalary.etCompanyName.text?.clear()
        binding.layoutSalary.etJoiningDate.text?.clear()
        binding.layoutSalary.layoutAddress.etLandmark.text?.clear()
        binding.layoutSalary.etDesignation.text?.clear()
        binding.layoutSalary.etEmployeeId.text?.clear()
        binding.layoutSalary.etTotalExperience.text?.clear()
        binding.layoutSalary.etOfficialMailId.text?.clear()
        binding.layoutSalary.etRetirementAge.text?.clear()
        binding.layoutSalary.layoutAddress.etAddress.text?.clear()
        binding.layoutSalary.layoutAddress.etContactNum.text?.clear()
        binding.layoutSalary.etNetIncome.text?.clear()
        binding.layoutSalary.etGrossIncome.text?.clear()
        binding.layoutSalary.etDeduction.text?.clear()
        binding.layoutSalary.layoutAddress.customZipAddressView.clearPinCodes()
        binding.layoutSenp.layoutAddress.customZipAddressView.clearPinCodes()
        clearSalaryDropdown(salarySpinnerList)
    }

    private fun clearSalaryDropdown(salarySpinnerList: ArrayList<CustomSpinnerView<DropdownMaster>>?) {
        salarySpinnerList?.let {
            for (spinner in salarySpinnerList) {
                spinner.clearSelf()
            }
        }
    }

    fun clearSenpForm(senpSpinnerList: ArrayList<CustomSpinnerView<DropdownMaster>>? = null) {
        binding.layoutSenp.cbAllEarningMember.isChecked = false
        binding.layoutSenp.etBusinessName.text?.clear()
        binding.layoutSenp.etIncorporationDate.text?.clear()
        binding.layoutSenp.layoutAddress.etLandmark.text?.clear()
        binding.layoutSenp.etBusinessVintage.text?.clear()
        binding.layoutSenp.etGstRegistration.text?.clear()
        binding.layoutSenp.layoutAddress.etContactNum.text?.clear()
        binding.layoutSenp.layoutAddress.etAddress.text?.clear()
        binding.layoutSenp.etAverageMonthlyIncome.text?.clear()
        binding.layoutSenp.etLastYearIncome.text?.clear()
        binding.layoutSenp.etCurrentYearIncome.text?.clear()
        binding.layoutSenp.etMonthlyIncome.text?.clear()
        binding.layoutSenp.layoutAddress.customZipAddressView.clearPinCodes()
        binding.layoutSalary.layoutAddress.customZipAddressView.clearPinCodes()
        clearSenpDropdown(senpSpinnerList)
    }

    private fun clearSenpDropdown(senpSpinnerList: ArrayList<CustomSpinnerView<DropdownMaster>>?) {
        senpSpinnerList?.let {
            for (spinner in senpSpinnerList) {
                spinner.clearSelf()
            }
        }
    }
}