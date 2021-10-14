package com.finance.app.utility

import android.content.Context
import com.finance.app.databinding.FragmentAssetLiablityBinding
import com.finance.app.databinding.LayoutCreditCardDetailsBinding
import com.finance.app.databinding.LayoutObligationBinding
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.view.adapters.recycler.spinner.MasterSpinnerAdapter

class ClearAssetLiabilityForm(private val binding: FragmentAssetLiablityBinding, val context: Context, private val masterDropdown: AllMasterDropDown) {

    fun clearFullForm() {
        clearAssetForm(binding)
        clearCardForm(binding.layoutCreditCard)
        clearObligationForm(binding.layoutObligations)
    }

    fun clearAssetForm(binding: FragmentAssetLiablityBinding) {
        binding.etValue.text?.clear()
        binding.spinnerAssetType.adapter = MasterSpinnerAdapter(context, masterDropdown.AssetDetail!!)
        binding.spinnerAssetSubType.adapter = MasterSpinnerAdapter(context, masterDropdown.AssetSubType!!)
        binding.spinnerOwnership.adapter = MasterSpinnerAdapter(context, masterDropdown.Ownership!!)
        binding.spinnerDocumentProof.adapter = MasterSpinnerAdapter(context, masterDropdown.DocumentProof!!)
    }

    fun clearObligationForm(binding: LayoutObligationBinding) {
        binding.etFinancierName.text?.clear()
        binding.etLoanAmount.text?.clear()
        binding.etAccountNum.text?.clear()
        binding.etTenure.text?.clear()
        binding.etBalanceTenure.text?.clear()
        binding.etEmiAmount.text?.clear()
        binding.etBouncesInLastNineMonths.text?.clear()
        binding.etBouncesInLastSixMonths.text?.clear()
        binding.etDisbursementDate.text?.clear()
        binding.spinnerLoanOwnership.adapter = MasterSpinnerAdapter(context, masterDropdown.LoanOwnership!!)
        binding.spinnerObligate.adapter = MasterSpinnerAdapter(context, masterDropdown.Obligate!!)
        binding.spinnerLoanType.adapter = MasterSpinnerAdapter(context, masterDropdown.LoanType!!)
        binding.spinnerRepaymentBank.adapter = MasterSpinnerAdapter(context, masterDropdown.RepaymentBank!!)
        binding.spinnerEmiPaidInSameMonth.adapter = MasterSpinnerAdapter(context, masterDropdown.BounceEmiPaidInSameMonth!!)
    }

    fun clearCardForm(binding: LayoutCreditCardDetailsBinding) {
        binding.etCreditCardLimit.text?.clear()
        binding.etCurrentUtilization.text?.clear()
        binding.etLastPaymentDate.text?.clear()
        binding.spinnerBankName.adapter = MasterSpinnerAdapter(context, masterDropdown.BankName!!)
        binding.spinnerObligate.adapter = MasterSpinnerAdapter(context, masterDropdown.Obligate!!)
    }
}