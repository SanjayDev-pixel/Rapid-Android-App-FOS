package com.finance.app.utility

import android.content.Context
import com.finance.app.databinding.FragmentReferenceBinding
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.StatesMaster
import com.finance.app.view.adapters.recycler.spinner.CitySpinnerAdapter
import com.finance.app.view.adapters.recycler.spinner.DistrictSpinnerAdapter
import com.finance.app.view.adapters.recycler.spinner.MasterSpinnerAdapter
import com.finance.app.view.adapters.recycler.spinner.StatesSpinnerAdapter

class ClearReferenceForm(private val binding: FragmentReferenceBinding, private val context: Context,
                         private val masterDropdown: AllMasterDropDown, private val states: List<StatesMaster>) {

    init {
//        binding.referenceAddressLayout.etContactNum.text?.clear()
//        binding.etName.text?.clear()
//        binding.etKnownSince.text?.clear()
//        binding.spinnerRelation.adapter = MasterSpinnerAdapter(context, masterDropdown.ReferenceRelationship!!)
//        binding.spinnerOccupation.adapter = MasterSpinnerAdapter(context, masterDropdown.OccupationType!!)
//        binding.referenceAddressLayout.spinnerState.adapter = StatesSpinnerAdapter(context, states)
//        binding.referenceAddressLayout.spinnerDistrict.adapter = DistrictSpinnerAdapter(context, ArrayList())
//        binding.referenceAddressLayout.spinnerCity.adapter = CitySpinnerAdapter(context, ArrayList())
//        binding.referenceAddressLayout.etPinCode.text?.clear()
//        binding.referenceAddressLayout.etLandmark.text?.clear()
//        binding.referenceAddressLayout.etAddress.text?.clear()
    }
}