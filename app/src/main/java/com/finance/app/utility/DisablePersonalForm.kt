package com.finance.app.utility

import android.text.InputType
import com.finance.app.databinding.LayoutCustomViewPersonalBinding
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.view.customViews.CustomSpinnerView
import kotlinx.android.synthetic.main.layout_zip_address.view.*

class DisablePersonalForm(binding: LayoutCustomViewPersonalBinding,
                          val profile: CustomSpinnerView<DropdownMaster>,
                          val livingStandard: CustomSpinnerView<DropdownMaster>,
                          val detailQualification: CustomSpinnerView<DropdownMaster>,
                          val qualification: CustomSpinnerView<DropdownMaster>,
                          val caste: CustomSpinnerView<DropdownMaster>,
                          val religion: CustomSpinnerView<DropdownMaster>,
                          val gender: CustomSpinnerView<DropdownMaster>,
                          val permanentAddressProof: CustomSpinnerView<DropdownMaster>,
                          val currentAddressProof: CustomSpinnerView<DropdownMaster>,
                          val nationality: CustomSpinnerView<DropdownMaster>,
                          val martialStatus : CustomSpinnerView<DropdownMaster>,
                          val currentResidenceType : CustomSpinnerView<DropdownMaster>,
                          val permanentResidenceType : CustomSpinnerView<DropdownMaster>) {

    init {
        /*
        Disable spinner, In case of Submitted Record */
        profile.disableSelf()
        livingStandard.disableSelf()
        detailQualification.disableSelf()
        qualification.disableSelf()
        caste.disableSelf()
        religion.disableSelf()
        gender.disableSelf()
        permanentAddressProof.disableSelf()
        currentAddressProof.disableSelf()
        nationality.disableSelf()
        martialStatus.disableSelf()
        currentResidenceType.disableSelf()
        permanentResidenceType.disableSelf()
        binding.basicInfoLayout.etFirstName.inputType = InputType.TYPE_NULL
        binding.basicInfoLayout.etMiddleName.inputType = InputType.TYPE_NULL
        binding.basicInfoLayout.etLastName.inputType = InputType.TYPE_NULL
        binding.basicInfoLayout.etDOB.setOnTouchListener { _, _ -> true }
        binding.basicInfoLayout.etAge.setOnTouchListener { _, _ -> true }
        binding.basicInfoLayout.etMobile.inputType = InputType.TYPE_NULL
        binding.basicInfoLayout.btnGetOTP.isClickable = false
        binding.basicInfoLayout.etFatherFirstName.inputType = InputType.TYPE_NULL
        binding.basicInfoLayout.etFatherMiddleName.inputType = InputType.TYPE_NULL
        binding.basicInfoLayout.etFatherLastName.inputType = InputType.TYPE_NULL
        binding.basicInfoLayout.etSpouseFirstName.inputType = InputType.TYPE_NULL
        binding.basicInfoLayout.etSpouseMiddleName.inputType = InputType.TYPE_NULL
        binding.basicInfoLayout.etSpouseLastName.inputType = InputType.TYPE_NULL
        binding.basicInfoLayout.etEmail.inputType = InputType.TYPE_NULL
        binding.basicInfoLayout.etAge.inputType = InputType.TYPE_NULL
        binding.basicInfoLayout.btnGetOTP.inputType = InputType.TYPE_NULL
        binding.basicInfoLayout.etNoOffamilymembers.inputType = InputType.TYPE_NULL
        binding.basicInfoLayout.etNumOfEarningMember.inputType =InputType.TYPE_NULL
        binding.basicInfoLayout.etNumOfDependent.inputType =InputType.TYPE_NULL
        binding.basicInfoLayout.etFatherLastName.inputType =InputType.TYPE_NULL
        binding.basicInfoLayout.etFatherMiddleName.inputType =InputType.TYPE_NULL
        binding.basicInfoLayout.etFatherLastName.inputType =InputType.TYPE_NULL
        binding.basicInfoLayout.etAlternateNum.inputType =InputType.TYPE_NULL

//        binding.basicInfoLayout.ivUploadDobProof.isClickable = false
        binding.basicInfoLayout.btnUploadDob.isClickable = false
        binding.basicInfoLayout.cbIncomeConsidered.isClickable = false
        binding.basicInfoLayout.btnUploadProfileImage.isClickable =false
        binding.btnAddKYC.isClickable = false
        binding.btnKyclist.isClickable= false

        //Disable Current Address
        binding.personalAddressLayout.cbSameAsCurrent.isClickable = false
        binding.personalAddressLayout.etCurrentAddress.inputType = InputType.TYPE_NULL
        binding.personalAddressLayout.etCurrentLandmark.inputType = InputType.TYPE_NULL
        binding.personalAddressLayout.etCurrentRentAmount.inputType = InputType.TYPE_NULL
        binding.personalAddressLayout.etCurrentStaying.inputType = InputType.TYPE_NULL
        binding.personalAddressLayout.customCurrentZipAddressView.inputLayoutCurrentPinCode.etCurrentPinCode.inputType = InputType.TYPE_NULL

        //binding.personalAddressLayout.customCurrentZipAddressView.spinnerCurrentDistrict.isEnabled = false
        //binding.personalAddressLayout.customCurrentZipAddressView.spinnerCurrentCity.isEnabled = false

        //Disable Permanent Address
        binding.personalAddressLayout.etPermanentAddress.inputType = InputType.TYPE_NULL
        binding.personalAddressLayout.etPermanentLandmark.inputType = InputType.TYPE_NULL
        binding.personalAddressLayout.etPermanentStaying.inputType = InputType.TYPE_NULL
        binding.personalAddressLayout.etPermanentRentAmount.inputType = InputType.TYPE_NULL
        binding.personalAddressLayout.customPermanentZipAddressView.inputLayoutCurrentPinCode.etCurrentPinCode.inputType = InputType.TYPE_NULL
        binding.personalAddressLayout.btnUploadAddress.isClickable = false
        binding.personalAddressLayout.btnUploadPermanentAddress.isClickable = false
        //binding.personalAddressLayout.customCurrentZipAddressView.disableSelf()

    }



}