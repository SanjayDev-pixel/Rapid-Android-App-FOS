package com.finance.app.view.adapters.recycler.holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.databinding.PreviewLayoutPropertyBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.PropertyModel
import com.finance.app.viewModel.AppDataViewModel

class PreviewPropertyHolder(val binding: PreviewLayoutPropertyBinding, val mContext: Context)
    : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(data: PropertyModel, pos: Int, viewModel: AppDataViewModel) {

        binding.tvPropertyAddress.text = data.propertyAddress
        binding.tvNumOfTenants.text = data.numberOfTenants.toString()
        binding.tvCashOCR.text = data.cashOCRValue.toString()
        binding.tvOCRValue.text = data.ocrValue.toString()
        binding.tvPropertyArea.text = data.propertyAreaSquareFt.toString()
        binding.tvMVProperty.text = data.mvOfProperty
        binding.tvAgreementValue.text = data.agreementValue.toString()
        binding.tvDistanceFromBranch.text = data.distanceFromBranch
        binding.tvDistanceFromResidence.text = data.distanceFromExistingResidence
        binding.tvLandmark.text = data.landmark
        binding.tvPinCode.text = data.pinCode
        binding.tvState.text= data.stateName
        binding.tvCity.text = data.cityName
        binding.tvDistrict.text =data.districtName
        binding.tvTransactionCategory.text = data.propertyNatureOfTransactionCategoryTypeDetailName


        viewModel.getMasterDropdownNameFromId(data.occupiedByTypeDetailID,
                AppEnums.DropdownMasterType.PropertyOccupiedBy, binding.tvOccupiedBy)
        viewModel.getMasterDropdownNameFromId(data.ownershipTypeDetailID,
                AppEnums.DropdownMasterType.PropertyOwnership, binding.tvOwnership)
        viewModel.getMasterDropdownNameFromId(data.unitTypeTypeDetailID,
                AppEnums.DropdownMasterType.PropertyUnitType, binding.tvUnitType)
        viewModel.getMasterDropdownNameFromId(data.natureOfPropertyTransactionTypeDetailID,
                AppEnums.DropdownMasterType.NatureOfPropertyTransaction, binding.tvPropertyNature)
        viewModel.getMasterDropdownNameFromId(data.alreadyOwnedPropertyTypeDetailID,
                AppEnums.DropdownMasterType.AlreadyOwnedProperty, binding.tvOwnedProperty)
        viewModel.getMasterDropdownNameFromId(data.occupiedByTypeDetailID,
                AppEnums.DropdownMasterType.PropertyOccupiedBy, binding.tvOccupiedBy)
        viewModel.getMasterDropdownNameFromId(data.tenantNocAvailableTypeDetailID,
                AppEnums.DropdownMasterType.TenantNocAvailable, binding.tvTenantNocAvailable)
       /* viewModel.getMasterDropdownNameFromId(data.propertyNatureOfTransactionCategoryTypeDetailID,
                AppEnums.DropdownMasterType.Pr, binding.tvTransactionCategory)*/

    }
}