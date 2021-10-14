package com.finance.app.utility

import com.finance.app.databinding.FragmentAssetLiablityBinding

class DisableAssetLiabilityForm(binding: FragmentAssetLiablityBinding) {

    init {

        binding.spinnerAssetType.isEnabled = false
        binding.spinnerAssetSubType.isEnabled = false
        binding.spinnerOwnership.isEnabled = false
        binding.spinnerDocumentProof.isEnabled = false
        binding.etValue.isEnabled = false
        binding.btnAddAsset.isEnabled = false
        binding.layoutCreditCard.spinnerBankName.isEnabled = false
        binding.layoutCreditCard.etCreditCardLimit.isEnabled = false
        binding.layoutCreditCard.etLastPaymentDate.isEnabled = false
        binding.layoutCreditCard.etCurrentUtilization.isEnabled = false
        binding.layoutCreditCard.spinnerObligate.isEnabled = false
        binding.layoutCreditCard.btnAddCreditCard.isEnabled = false
        binding.layoutObligations.spinnerLoanOwnership.isEnabled = false
        binding.layoutObligations.spinnerObligate.isEnabled = false
        binding.layoutObligations.spinnerLoanType.isEnabled = false
        binding.layoutObligations.etLoanAmount.isEnabled = false
        binding.layoutObligations.etFinancierName.isEnabled = false
        binding.layoutObligations.etTenure.isEnabled = false
        binding.layoutObligations.etBalanceTenure.isEnabled = false
        binding.layoutObligations.spinnerRepaymentBank.isEnabled = false
        binding.layoutObligations.etEmiAmount.isEnabled = false
        binding.layoutObligations.etBouncesInLastNineMonths.isEnabled = false
        binding.layoutObligations.etBouncesInLastSixMonths.isEnabled = false
        binding.layoutObligations.spinnerEmiPaidInSameMonth.isEnabled = false
        binding.layoutObligations.etDisbursementDate.isEnabled = false
        binding.layoutObligations.btnAddObligation.isEnabled = false
    }
}