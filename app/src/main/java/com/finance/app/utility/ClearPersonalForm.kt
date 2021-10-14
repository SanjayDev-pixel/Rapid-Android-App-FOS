package com.finance.app.utility

import android.view.View
import com.finance.app.databinding.FragmentPersonalBinding
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.view.customViews.CustomSpinnerView

class ClearPersonalForm(private val binding: FragmentPersonalBinding,
                        private val spinnerList: ArrayList<CustomSpinnerView<DropdownMaster>>) {

    init {
        binding.etIdNum.text?.clear()
        binding.etIssueDate.text?.clear()
        binding.etExpiryDate.text?.clear()
        binding.basicInfoLayout.cbIncomeConsidered.isChecked = false
        clearBasicInfoForm()
        clearAddressInfoForm()
    }

    private fun clearAddressInfoForm() {
        binding.personalAddressLayout.customCurrentZipAddressView.clearZipCode()
        binding.personalAddressLayout.customPermanentZipAddressView.clearZipCode()
        binding.personalAddressLayout.etPermanentRentAmount.text?.clear()
        binding.personalAddressLayout.etPermanentLandmark.text?.clear()
        binding.personalAddressLayout.etPermanentStaying.text?.clear()
        binding.personalAddressLayout.etCurrentRentAmount.text?.clear()
        binding.personalAddressLayout.etPermanentAddress.text?.clear()
        binding.personalAddressLayout.etCurrentStaying.text?.clear()
        binding.personalAddressLayout.etCurrentLandmark.text?.clear()
        binding.personalAddressLayout.etCurrentAddress.text?.clear()
        binding.personalAddressLayout.cbSameAsCurrent.isChecked = false
        clearAddressInfoDropdown()
    }

    private fun clearAddressInfoDropdown() {
        binding.personalAddressLayout.llPermanentAddress.visibility = View.VISIBLE
        binding.personalAddressLayout.customCurrentZipAddressView.clearPinCodes()
        binding.personalAddressLayout.customPermanentZipAddressView.clearPinCodes()
    }

    private fun clearBasicInfoForm() {
        binding.basicInfoLayout.etNumOfDependent.text?.clear()
        binding.basicInfoLayout.etAlternateNum.text?.clear()
        binding.basicInfoLayout.etNumOfEarningMember.text?.clear()
        binding.basicInfoLayout.etAge.text?.clear()
        binding.basicInfoLayout.etLastName.text?.clear()
        binding.basicInfoLayout.etMiddleName.text?.clear()
        binding.basicInfoLayout.etFirstName.text?.clear()
        binding.basicInfoLayout.etSpouseLastName.text?.clear()
        binding.basicInfoLayout.etSpouseFirstName.text?.clear()
        binding.basicInfoLayout.etSpouseMiddleName.text?.clear()
        binding.basicInfoLayout.etFatherFirstName.text?.clear()
        binding.basicInfoLayout.etFatherMiddleName.text?.clear()
        binding.basicInfoLayout.etFatherLastName.text?.clear()
        binding.basicInfoLayout.etEmail.text?.clear()
        binding.basicInfoLayout.etDOB.text?.clear()
        binding.basicInfoLayout.etMobile.text?.clear()
        binding.basicInfoLayout.etMobile.isEnabled = true
        binding.basicInfoLayout.btnGetOTP.visibility = View.VISIBLE
        binding.basicInfoLayout.ivVerifiedStatus.visibility = View.GONE
        clearBasicInfoDropdown()
    }

    private fun clearBasicInfoDropdown() {
        for (spinner in spinnerList) {
            spinner.clearSelf()
            spinner.enableSelf()
        }
    }
}