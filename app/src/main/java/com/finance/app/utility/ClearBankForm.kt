package com.finance.app.utility

import android.content.Context
import android.view.View
import com.finance.app.databinding.DialogBankDetailFormBinding
import com.finance.app.databinding.FragmentBankDetailBinding
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.view.adapters.recycler.spinner.MasterSpinnerAdapter
import com.finance.app.view.customViews.CustomSpinnerView
import fr.ganfra.materialspinner.MaterialSpinner

class ClearBankForm {

    constructor(binding: FragmentBankDetailBinding, context: Context, masterDropdown: AllMasterDropDown, bankName: CustomSpinnerView<DropdownMaster>, accountType: CustomSpinnerView<DropdownMaster>) {
//        binding.etAccountNum.text?.clear()
//        binding.etSalaryCreditedInSixMonths.text?.clear()
//        binding.etAccountHolderName.text?.clear()
//        binding.spinnerSalaryCredit.adapter = MasterSpinnerAdapter(context, masterDropdown.SalaryCredit!!)
//        bankName.clearSpinner()
//        accountType.clearSpinner()
//        binding.inputLayoutSalaryCreditInSixMonth.visibility = View.GONE
    }

    constructor(binding: DialogBankDetailFormBinding, context: Context, masterDropdown: AllMasterDropDown, bankName: MaterialSpinner, accountType: MaterialSpinner) {
        binding.etAccountNum.text?.clear()
        binding.etSalaryCreditedInSixMonths.text?.clear()
        binding.etAccountHolderName.text?.clear()
        binding.spinnerSalaryCredit.adapter = MasterSpinnerAdapter(context, masterDropdown.SalaryCredit!!)
//        bankName.clearSpinner()
//        accountType.clearSpinner()
        binding.inputLayoutSalaryCreditInSixMonth.visibility = View.GONE
    }


}