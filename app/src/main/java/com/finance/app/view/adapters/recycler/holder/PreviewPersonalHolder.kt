package com.finance.app.view.adapters.recycler.holder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.databinding.PreviewLayoutPersonalBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AddressDetail
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.viewModel.AppDataViewModel
import kotlinx.android.synthetic.main.preview_layout_address_detail.view.*
import kotlinx.android.synthetic.main.preview_layout_basic_detail.view.*

class PreviewPersonalHolder(val binding: PreviewLayoutPersonalBinding, val mContext: Context)
    : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(list: ArrayList<PersonalApplicantsModel>?, pos: Int, viewModel: AppDataViewModel) {
        if (!list.isNullOrEmpty()) {
            setValueInCard(list[pos], viewModel)
            handleCollapse()
        }
    }

    private fun handleCollapse() {
        binding.collapseForm.setOnClickListener {
            binding.cardApplicant.visibility = View.GONE
            binding.collapseForm.visibility = View.GONE
            binding.expandForm.visibility = View.VISIBLE
        }

        binding.expandForm.setOnClickListener {
            binding.cardApplicant.visibility = View.VISIBLE
            binding.collapseForm.visibility = View.VISIBLE
            binding.expandForm.visibility = View.GONE
        }
    }

    private fun setValueInCard(data: PersonalApplicantsModel, viewModel: AppDataViewModel) {
        binding.tvCoApplicant.text = data.firstName
        binding.previewLayoutBasic.tvFirstName.text = data.firstName
        binding.previewLayoutBasic.tvMiddleName.text = data.middleName
        binding.previewLayoutBasic.tvLastName.text = data.lastName
        binding.previewLayoutBasic.tvDob.text = data.dateOfBirth
        binding.previewLayoutBasic.tvAge.text = data.age.toString()
        binding.previewLayoutBasic.tvMobile.text = data.contactDetail?.mobile
        binding.previewLayoutBasic.tvEmail.text = data.contactDetail?.email
        binding.previewLayoutBasic.tvFatherFirstName.text = data.fatherFirstName
        binding.previewLayoutBasic.tvFatherMiddleName.text = data.fatherMiddleName
        binding.previewLayoutBasic.tvFatherLastName.text = data.fatherLastName
        binding.previewLayoutBasic.tvAlternateContactNum.text = data.alternateContact
        binding.previewLayoutBasic.tvNumOfDependents.text = (data.numberOfDependents).toString()
        binding.previewLayoutBasic.tvNumOfEarningMembers.text = data.numberOfEarningMembers.toString()
        binding.previewLayoutBasic.tvNumOfFamilymembers.text =data.numberOfFamilyMembersOthers.toString()

        viewModel.getMasterDropdownNameFromId(data.dobProofTypeDetailID, AppEnums.DropdownMasterType.DOBProof,
                binding.previewLayoutBasic.tvDobProof)
        viewModel.getMasterDropdownNameFromId(data.genderTypeDetailID, AppEnums.DropdownMasterType.Gender,
                binding.previewLayoutBasic.tvGender)
        viewModel.getMasterDropdownNameFromId(data.nationalityTypeDetailID, AppEnums.DropdownMasterType.Nationality,
                binding.previewLayoutBasic.tvNationality)
        viewModel.getMasterDropdownNameFromId(data.religionTypeDetailID, AppEnums.DropdownMasterType.Religion,
                binding.previewLayoutBasic.tvReligion)
        viewModel.getMasterDropdownNameFromId(data.casteTypeDetailID, AppEnums.DropdownMasterType.Caste,
                binding.previewLayoutBasic.tvCaste)
        viewModel.getMasterDropdownNameFromId(data.qualificationTypeDetailID, AppEnums.DropdownMasterType.Qualification,
                binding.previewLayoutBasic.tvQualification)
        viewModel.getMasterDropdownNameFromId(data.detailQualificationTypeDetailID, AppEnums.DropdownMasterType.DetailQualification,
                binding.previewLayoutBasic.tvDetailQualification)
        viewModel.getMasterDropdownNameFromId(data.maritialStatusTypeDetailID, AppEnums.DropdownMasterType.MaritalStatus,
                binding.previewLayoutBasic.tvMaritalStatus)
        viewModel.getMasterDropdownNameFromId(data.relationshipTypeDetailId, AppEnums.DropdownMasterType.Relationship,
                binding.previewLayoutBasic.tvRelationship)
        viewModel.getMasterDropdownNameFromId(data.genderTypeDetailID, AppEnums.DropdownMasterType.Gender,
                binding.previewLayoutBasic.tvGender)
        viewModel.getMasterDropdownNameFromId(data.genderTypeDetailID, AppEnums.DropdownMasterType.Gender,
                binding.previewLayoutBasic.tvGender)
        viewModel.getMasterDropdownNameFromId(data.livingStandardTypeDetailId,AppEnums.DropdownMasterType.LivingStandardIndicators,binding.previewLayoutBasic.tvLivingStandard)

        fillAddressData(data.addressDetailList, viewModel)
    }

    private fun fillAddressData(addressDetailList: ArrayList<AddressDetail>?, viewModel: AppDataViewModel) {
        addressDetailList?.let {
            fillCurrentAddress(addressDetailList[0], viewModel)
            fillPermanentAddress(addressDetailList[1], viewModel)
        }
    }

    private fun fillCurrentAddress(data: AddressDetail, viewModel: AppDataViewModel) {
        binding.previewLayoutAddress.tvCurrentAddress.text = data.address1
        binding.previewLayoutAddress.tvCurrentLandmark.text = data.landmark
        binding.previewLayoutAddress.tvCurrentRentAmount.text = data.rentAmount
        binding.previewLayoutAddress.tvCurrentStaying.text = (data.stayingInYears).toString()
        binding.previewLayoutAddress.tvCurrentPinCode.text = data.zip
        binding.previewLayoutAddress.tvCurrentState.text = data.stateName
        binding.previewLayoutAddress.tvCurrentDistrict.text = data.districtName
        binding.previewLayoutAddress.tvCurrentCity.text = data.cityName

        viewModel.getMasterDropdownNameFromId(data.residenceTypeTypeDetailID, AppEnums.DropdownMasterType.ResidenceType,
                binding.previewLayoutAddress.tvCurrentResidenceType)
        viewModel.getMasterDropdownNameFromId(data.addressProof, AppEnums.DropdownMasterType.AddressProof,
                binding.previewLayoutAddress.tvCurrentAddressProof)
        viewModel.getStateNameFromId(data.stateID, binding.previewLayoutAddress.tvCurrentState)

    }

    private fun fillPermanentAddress(data: AddressDetail, viewModel: AppDataViewModel) {
        binding.previewLayoutAddress.tvPermanentAddress.text = data.address1
        binding.previewLayoutAddress.tvPermanentLandmark.text = data.landmark
        binding.previewLayoutAddress.tvPermanentRentAmount.text = data.rentAmount
        binding.previewLayoutAddress.tvPermanentStaying.text = (data.stayingInYears).toString()
        binding.previewLayoutAddress.tvPermanentPinCode.text = data.zip
        binding.previewLayoutAddress.tvPermanentDistrict.text = data.districtName
        binding.previewLayoutAddress.tvPermanentCity.text = data.cityName
        binding.previewLayoutAddress.tvPermanentState.text = data.stateName

        viewModel.getMasterDropdownNameFromId(data.residenceTypeTypeDetailID, AppEnums.DropdownMasterType.ResidenceType,
                binding.previewLayoutAddress.tvPermanentResidenceType)
        viewModel.getMasterDropdownNameFromId(data.addressProof, AppEnums.DropdownMasterType.AddressProof,
                binding.previewLayoutAddress.tvPermanentAddressProof)
        viewModel.getStateNameFromId(data.stateID, binding.previewLayoutAddress.tvPermanentState)

    }

}