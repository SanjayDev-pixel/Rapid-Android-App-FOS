package com.finance.app.utility

import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import com.finance.app.R
import com.finance.app.databinding.FragmentPropertyInfoBinding

class DisablePropertyFields(binding: FragmentPropertyInfoBinding,context1 :Context) {
    val myTypeface = Typeface.createFromAsset(context1.assets, "fonts/Raleway-Bold.ttf")
    init {
        binding.spinnerUnitType.isEnabled = false
        binding.spinnerUnitType.disabledColor = R.color.black
        binding.spinnerUnitType.typeface = myTypeface
        binding.spinnerOwnership.isEnabled = false
        binding.spinnerOwnership.disabledColor = R.color.black
        binding.spinnerOwnership.typeface = myTypeface
        binding.spinnerPropertyNature.isEnabled = false
        binding.spinnerPropertyNature.disabledColor = R.color.black
        binding.spinnerPropertyNature.typeface = myTypeface
        binding.spinnerTransactionCategory.isEnabled = false
        binding.spinnerTransactionCategory.disabledColor = R.color.black
        binding.spinnerTransactionCategory.typeface = myTypeface
        binding.spinnerOwnedProperty.isEnabled = false
        binding.spinnerOwnedProperty.disabledColor = R.color.black
        binding.spinnerOwnedProperty.typeface = myTypeface
        binding.spinnerOccupiedBy.isEnabled = false
        binding.spinnerOccupiedBy.disabledColor = R.color.black
        binding.spinnerOccupiedBy.typeface = myTypeface
        binding.spinnerState.isEnabled = false
        binding.spinnerState.isClickable = false
        binding.spinnerDistrict.isEnabled = false
        binding.spinnerCity.isEnabled = false
        binding.spinnerTenantNocAvailable.isEnabled =  false
        binding.spinnerTenantNocAvailable.disabledColor = R.color.black
        binding.spinnerTenantNocAvailable.typeface = myTypeface
        binding.cbIsFirstProperty.isEnabled = false
        binding.etNumOfTenants.inputType = InputType.TYPE_NULL
        binding.etCashOcr.inputType = InputType.TYPE_NULL
        binding.etOcr.inputType = InputType.TYPE_NULL
        binding.etPropertyArea.inputType = InputType.TYPE_NULL
        binding.etMvProperty.inputType = InputType.TYPE_NULL
        binding.etAgreementValue.inputType = InputType.TYPE_NULL
        binding.etDistanceFromBranch.inputType = InputType.TYPE_NULL
        binding.etDistanceFromResidence.inputType = InputType.TYPE_NULL
        binding.etPropertyAddress.inputType = InputType.TYPE_NULL
        binding.etLandmark.inputType = InputType.TYPE_NULL
        binding.etPinCode.inputType = InputType.TYPE_NULL
        binding.spinnerPropertytype.isEnabled = false
        binding.spinnerPropertytype.disabledColor = R.color.black
        binding.spinnerPropertytype.typeface = myTypeface
        binding.spinnerTrancactiontype.isEnabled = false
        binding.spinnerTrancactiontype.disabledColor = R.color.black
        binding.spinnerTrancactiontype.typeface = myTypeface

        //binding.btnPrevious.isEnabled = false
        //binding.btnNext.isEnabled = false

    }
}