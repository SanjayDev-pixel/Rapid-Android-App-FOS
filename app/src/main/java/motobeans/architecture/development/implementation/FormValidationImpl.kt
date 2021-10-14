package motobeans.architecture.development.implementation

import android.util.Log
import android.view.View
import android.widget.Toast
import com.finance.app.databinding.*
import com.finance.app.persistence.model.*
import com.finance.app.utility.CurrencyConversion
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.activity.UpdateCallActivity
import com.finance.app.view.customViews.ChannelPartnerViewCreateLead
import com.finance.app.view.customViews.CustomChannelPartnerView
import com.finance.app.view.customViews.CustomSpinnerView
import com.finance.app.view.utils.EditTexNormal
import com.google.android.material.textfield.TextInputEditText
import fr.ganfra.materialspinner.MaterialSpinner
import kotlinx.android.synthetic.main.layout_zip_address.view.*
import motobeans.architecture.constants.Constants
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import java.util.regex.Matcher
import java.util.regex.Pattern

class FormValidationImpl : FormValidation {

    override fun validateLogin(binding: ActivityLoginBinding): Boolean {
        val userName = binding.etUserName.text.toString()
        val pwd = binding.etPassword.text.toString()
        val error = when {
            !userName.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etUserName)
            !pwd.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etPassword)
            else -> 0
        }
        return isValidForm(error)
    }

    override fun validatePersonalInfo(binding: LayoutCustomViewPersonalBinding, spinnerDMList: ArrayList<CustomSpinnerView<DropdownMaster>>,religion:CustomSpinnerView<DropdownMaster>): Boolean {
        var errorCount = 0

        val firstName = binding.basicInfoLayout.etFirstName.text.toString()
        val dob = binding.basicInfoLayout.etDOB.text.toString()
        val currentAddress = binding.personalAddressLayout.etCurrentAddress.text.toString()
        val currentLandmark = binding.personalAddressLayout.etCurrentLandmark.text.toString()
        val currentStaying = binding.personalAddressLayout.etCurrentStaying.text.toString()
        val email = binding.basicInfoLayout.etEmail.text.toString()
        val age = binding.basicInfoLayout.etAge.text.toString()
        val mobile = binding.basicInfoLayout.etMobile.text.toString()
        val father_Name = binding.basicInfoLayout.etFatherFirstName.text.toString()
        val numberOfFamilyMenmbers=binding.basicInfoLayout.etNoOffamilymembers.text.toString()
        if(religion.getSelectedValue()==null){
            errorCount++
            religion.showError(true)
        }
        //religion.getSelectedValue()

        if (age.exIsNotEmptyOrNullOrBlank()) {
            if (age.toInt() !in 99 downTo 14) {
                errorCount++
                binding.basicInfoLayout.etAge.error = "Invalid Age"
            }
        }

        if (!currentStaying.exIsNotEmptyOrNullOrBlank() || currentStaying == "" || currentStaying.toFloat() > 99) {
            errorCount++
            binding.personalAddressLayout.etCurrentStaying.error = "Required field missing or Invalid Entry"
        }

        if (!binding.personalAddressLayout.customCurrentZipAddressView.validateAndHandleError()) {
            errorCount++
        }

        if (!binding.personalAddressLayout.cbSameAsCurrent.isChecked) {
            errorCount.plus(checkPermanentAddressFields(binding))
        }

        if (!father_Name.exIsNotEmptyOrNullOrBlank()) {
            binding.basicInfoLayout.etFatherFirstName.error = "Enter your Father's Name"
            errorCount++
        }

        if(!numberOfFamilyMenmbers.exIsNotEmptyOrNullOrBlank()){
            binding.basicInfoLayout.etNoOffamilymembers.error = "Enter your Family member number"
            errorCount++
        }

        val fieldError = when {
            !currentLandmark.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.personalAddressLayout.etCurrentLandmark)
            !dob.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.basicInfoLayout.etDOB)
            !firstName.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.basicInfoLayout.etFirstName)
            !currentAddress.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.personalAddressLayout.etCurrentAddress)
            !isValidEmail(email) -> setFieldError(binding.basicInfoLayout.etEmail)
            !isValidMobile(mobile) -> setFieldError(binding.basicInfoLayout.etMobile)
            !numberOfFamilyMenmbers.exIsNotEmptyOrNullOrBlank()-> setFieldError(binding.basicInfoLayout.etNoOffamilymembers)
            else -> 0
        }

        var spinnerError = 0
        spinnerDMList.forEach { item ->
            if (!item.isValid()) ++spinnerError
        }

        val totalErrors = errorCount + fieldError + spinnerError
        return isValidForm(totalErrors)
    }

    private fun checkPermanentAddressFields(binding: LayoutCustomViewPersonalBinding): Int {
        var errorCount = 0
        val permanentAddress = binding.personalAddressLayout.etPermanentAddress.text.toString()
        val permanentStaying = binding.personalAddressLayout.etPermanentStaying.text.toString()
        val rentAmount = binding.personalAddressLayout.etPermanentRentAmount.text.toString()

        if (!binding.personalAddressLayout.customPermanentZipAddressView.validateAndHandleError()) {
            errorCount++
        }

        return errorCount.plus(
                when {
                    !permanentAddress.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.personalAddressLayout.etPermanentAddress)
                    !permanentStaying.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.personalAddressLayout.etPermanentStaying)
                    !rentAmount.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.personalAddressLayout.etPermanentRentAmount)
                    else -> 0
                }
        )
    }

    override fun validateLoanInformation(
            binding: FragmentLoanInformationBinding,
            loanProduct: CustomSpinnerView<LoanProductMaster>,
            loanPurpose: CustomSpinnerView<LoanPurpose>,
            spinnerDMList: ArrayList<CustomSpinnerView<DropdownMaster>>,
            customChannelPartnerView: CustomChannelPartnerView
    ): Boolean {

        var errorCount = 0
        val loanAmount = CurrencyConversion().convertToNormalValue(binding.etAmountRequest.text.toString())
        val emi = binding.etEmi.text.toString()
        val tenure = binding.etTenure.text.toString()
        val applicationNumber = binding.etApplicationNumber.text.toString()

        val loan = loanProduct.getSelectedValue()
        if (loan != null && tenure != "" && loanAmount != "") {
            if (tenure.toInt() > loan.maxTenure || tenure.toInt() < loan.minTenure) {
                errorCount++
                binding.etTenure.error = "Range:${loan.minTenure} - ${loan.maxTenure}"
            }

            if (loanAmount.toInt() > loan.maxAmount || loanAmount.toInt() < loan.minAmount) {
                errorCount++
                binding.etAmountRequest.error = "Range:${loan.minAmount} - ${loan.maxAmount}"
            }
        }
        val loanPurposeData = loanPurpose.getSelectedValue()
        if (loanPurposeData == null) {
            errorCount++
            loanPurpose.showError(true)
        }

        val fieldError = when {
            !tenure.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etTenure)
            !loanAmount.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etAmountRequest)
            !emi.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etEmi)
            !applicationNumber.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etApplicationNumber)
            else -> 0
        }

        if (emi.equals("0")) {
            setFieldError(binding.etEmi)
            errorCount++
        }

        var spinnerError = 0
        spinnerDMList.forEach { item ->
            if (!item.isValid()) ++spinnerError
        }

        val sourcingPartnerName: String = customChannelPartnerView.getSourcingPartner().toString()
        if (sourcingPartnerName == "null") {
            errorCount++
        }

        val totalErrors = errorCount + fieldError + spinnerError
        return isValidForm(totalErrors)

    }

    override fun disableAssetLiabilityFields(binding: FragmentAssetLiablityBinding) {
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

    override fun validateSalaryEmployment(salaryBinding: LayoutSalaryBinding, salarySpinnerList: ArrayList<CustomSpinnerView<DropdownMaster>>): Boolean {
        val companyName = salaryBinding.etCompanyName.text.toString()
        val designation = salaryBinding.etDesignation.text.toString()
        val totalExp = salaryBinding.etTotalExperience.text.toString()
        val retirementAge = salaryBinding.etRetirementAge.text.toString()
        val grossIncome = salaryBinding.etGrossIncome.text.toString()
        val deduction = salaryBinding.etDeduction.text.toString()
       // val employeeId = salaryBinding.etEmployeeId.text.toString()

        val fieldError = when {
            !companyName.exIsNotEmptyOrNullOrBlank() -> setFieldError(salaryBinding.etCompanyName)
            !designation.exIsNotEmptyOrNullOrBlank() -> setFieldError(salaryBinding.etDesignation)
            !totalExp.exIsNotEmptyOrNullOrBlank() -> setFieldError(salaryBinding.etTotalExperience)
            !retirementAge.exIsNotEmptyOrNullOrBlank() -> setFieldError(salaryBinding.etRetirementAge)
            !grossIncome.exIsNotEmptyOrNullOrBlank() -> setFieldError(salaryBinding.etGrossIncome)
            !deduction.exIsNotEmptyOrNullOrBlank() -> setFieldError(salaryBinding.etDeduction)
           // !employeeId.exIsNotEmptyOrNullOrBlank() -> setFieldError(salaryBinding.etEmployeeId)
            else -> 0
        }

        var spinnerError = 0
        salarySpinnerList.forEach { item ->
            if (!item.isValid()) ++spinnerError
        }

        val addressError = validateAddress(salaryBinding.layoutAddress)
        val totalError = spinnerError + fieldError + addressError
        return isValidForm(totalError)
    }

    override fun validateSenpEmployment(senpBinding: LayoutSenpBinding, senpSpinnerList: ArrayList<CustomSpinnerView<DropdownMaster>>): Boolean {
        val businessName = senpBinding.etBusinessName.text.toString()
        val gstVatRegistration = senpBinding.etGstRegistration.text.toString()
        val incorporationDate = senpBinding.etIncorporationDate.text.toString()
        val businessVintage = senpBinding.etBusinessVintage.text.toString()

        val fieldError = when {
            !businessName.exIsNotEmptyOrNullOrBlank() -> setFieldError(senpBinding.etBusinessName)
            !gstVatRegistration.exIsNotEmptyOrNullOrBlank() -> setFieldError(senpBinding.etGstRegistration)
            !incorporationDate.exIsNotEmptyOrNullOrBlank() -> setFieldError(senpBinding.etIncorporationDate)
            !businessVintage.exIsNotEmptyOrNullOrBlank() -> setFieldError(senpBinding.etBusinessVintage)
            else -> 0
        }

        var spinnerError = 0
        senpSpinnerList.forEach { item ->
            if (!item.isValid()) ++spinnerError
        }

        val addressError = validateAddress(senpBinding.layoutAddress)
        val totalError = spinnerError + fieldError + addressError
        return isValidForm(totalError)
    }

    override fun validateEmploymentBusiness(businessBinding: LayoutSenpBinding): Boolean {
        val businessName = businessBinding.etBusinessName.text.toString()
        val gstVatRegistration = businessBinding.etGstRegistration.text.toString()
        val incorporationDate = businessBinding.etIncorporationDate.text.toString()
        val businessVintage = businessBinding.etBusinessVintage.text.toString()
        val montlyIncome = businessBinding.etMonthlyIncome.text.toString()
        val lastYearIncome = businessBinding.etLastYearIncome.text.toString()
        val currentYearIncome = businessBinding.etCurrentYearIncome.text.toString()

        var fieldError = 0

        if (businessName.exIsNotEmptyOrNullOrBlank().not()) {
            setFieldError(businessBinding.etBusinessName)
            fieldError++
        }
        /*if (gstVatRegistration.exIsNotEmptyOrNullOrBlank().not()) {
            setFieldError(businessBinding.etGstRegistration)
            fieldError++
        }*/
        if (incorporationDate.exIsNotEmptyOrNullOrBlank().not()) {
            setFieldError(businessBinding.etIncorporationDate)
            fieldError++
        }
        if (businessVintage.exIsNotEmptyOrNullOrBlank().not()) {
            setFieldError(businessBinding.etBusinessVintage)
            fieldError++
        }

        if (businessBinding.inputMonthlyIncome.visibility == View.VISIBLE && montlyIncome.exIsNotEmptyOrNullOrBlank().not()) {
            setFieldError(businessBinding.etMonthlyIncome)
            fieldError++
        }

        if (businessBinding.lytYearlyIncome.visibility == View.VISIBLE && (lastYearIncome.exIsNotEmptyOrNullOrBlank().not() || currentYearIncome.exIsNotEmptyOrNullOrBlank().not())) {
            if (lastYearIncome.exIsNotEmptyOrNullOrBlank().not())
                setFieldError(businessBinding.etLastYearIncome)
            if (currentYearIncome.exIsNotEmptyOrNullOrBlank().not())
                setFieldError(businessBinding.etCurrentYearIncome)

            fieldError++
        }

        var spinnerError = 0

        val constitution = businessBinding.spinnerConstitution.selectedItem as DropdownMaster?
        if (constitution == null) {
            spinnerError++
            businessBinding.spinnerConstitution.error = "Required Field"
        }

        val industry = businessBinding.spinnerIndustry.selectedItem as DropdownMaster?
        if (industry == null) {
            spinnerError++
            businessBinding.spinnerIndustry.error = "Required Field"
        }

        val businessSetupType = businessBinding.spinnerBusinessSetupType.selectedItem as DropdownMaster?
        if (businessSetupType == null) {
            spinnerError++
            businessBinding.spinnerBusinessSetupType.error = "Required Field"
        }

        val addressError = validateAddress(businessBinding.layoutAddress)
        val totalError = spinnerError + fieldError + addressError
        return isValidForm(totalError)
    }

    override fun validateEmploymentSalary(salaryBinding: LayoutSalaryBinding): Boolean {
        val companyName = salaryBinding.etCompanyName.text.toString()
        val designation = salaryBinding.etDesignation.text.toString()
        val totalExp = salaryBinding.etTotalExperience.text.toString()
        val retirementAge = salaryBinding.etRetirementAge.text.toString()
        val grossIncome = salaryBinding.etGrossIncome.text.toString()
        val deduction = salaryBinding.etDeduction.text.toString()
        //val employeeId = salaryBinding.etEmployeeId.text.toString()

        var fieldError = 0
        if (companyName.exIsNotEmptyOrNullOrBlank().not()) {
            setFieldError(salaryBinding.etCompanyName);fieldError++
        }
        if (designation.exIsNotEmptyOrNullOrBlank().not()) {
            setFieldError(salaryBinding.etDesignation);fieldError++
        }
        if (totalExp.exIsNotEmptyOrNullOrBlank().not()) {
            setFieldError(salaryBinding.etTotalExperience);fieldError++
        }
        if (retirementAge.exIsNotEmptyOrNullOrBlank().not()) {
            setFieldError(salaryBinding.etRetirementAge);fieldError++
        }
        if (grossIncome.exIsNotEmptyOrNullOrBlank().not()) {
            setFieldError(salaryBinding.etGrossIncome);fieldError++
        }
        if (deduction.exIsNotEmptyOrNullOrBlank().not()) {
            setFieldError(salaryBinding.etDeduction);fieldError++
        }
        /*if (employeeId.exIsNotEmptyOrNullOrBlank().not()) {
            setFieldError(salaryBinding.etEmployeeId);fieldError++
        }*/


        var spinnerError = 0
        val sector = salaryBinding.spinnerSector.selectedItem as DropdownMaster?
        if (sector == null) {
            spinnerError++
            salaryBinding.spinnerSector.error = "Required Field"
        }

        val employmentType = salaryBinding.spinnerEmploymentType.selectedItem as DropdownMaster?
        if (employmentType == null) {
            spinnerError++
            salaryBinding.spinnerEmploymentType.error = "Required Field"
        }
        if(sector?.typeDetailID == Constants.SECTOR_GOVERMENT)
        {

        }
        else
        {
            val industry = salaryBinding.spinnerIndustry.selectedItem as DropdownMaster?
            if (industry == null) {
                spinnerError++
                salaryBinding.spinnerIndustry.error = "Required Field"
            }
        }


        val addressError = validateAddress(salaryBinding.layoutAddress)
        val totalError = spinnerError + fieldError + addressError
        return isValidForm(totalError)
    }

    private fun validateAddress(binding: LayoutEmploymentAddressBinding): Int {
        var errorCount = 0
        val landmark = binding.etLandmark.text.toString()
        val contact = binding.etContactNum.text.toString()
        val officeAddress = binding.etAddress.text.toString()

        val error = when {
            !officeAddress.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etAddress)
            !landmark.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etLandmark)
            !isValidMobile(contact) -> setFieldError(binding.etContactNum)
            else -> 0
        }

        if (!binding.customZipAddressView.validateAndHandleError()) {
            errorCount++
        }

        return error + errorCount
    }

    override fun validateBankDetail(binding: DialogBankDetailFormBinding): Boolean {
        var errorCount = 0

        val bankName = binding.actBankName.tag as DropdownMaster?
        if (bankName == null) {
            errorCount++
            binding.actBankName.error = "Required Field"
        }

        val accountType = binding.spinnerAccountType.selectedItem as DropdownMaster?
        if (accountType == null) {
            errorCount++
            binding.spinnerAccountType.error = "Required Field"
        }

        val salaryCredit = binding.spinnerSalaryCredit.selectedItem as DropdownMaster?
        if (salaryCredit == null) {
            errorCount++
            binding.spinnerSalaryCredit.error = "Required Field"
        }

        val accountNum = binding.etAccountNum.text.toString()
        if (!accountNum.exIsNotEmptyOrNullOrBlank() || accountNum.length < 6) {
            errorCount++
            binding.etAccountNum.error = "Account Num not valid"
        }
        val accountHolderName = binding.etAccountHolderName.text.toString()
        if (!accountHolderName.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            binding.etAccountHolderName.error = "Account holder name can not be blank"
        }


        var numberOfSalary: String=binding.etSalaryCreditedInSixMonths.text.toString()
        if(!numberOfSalary.equals("")) {
            var salaryNumber: Int = 0
            numberOfSalary?.let {
                salaryNumber = it.toInt()
            }
            if (salaryNumber != 0 && salaryNumber > 6) {
                errorCount++
                binding.etSalaryCreditedInSixMonths.error = " Credited salary not more than six."
            }
        }

        return isValidForm(errorCount)
    }

    override fun validateReference(binding: DialogReferenceDetailsBinding): Boolean {
        var errorCount = 0
        val name = binding.etName.text.toString()
        val relation = binding.spinnerRelation.selectedItem as DropdownMaster?
        val occupation = binding.spinnerOccupation.selectedItem as DropdownMaster?
        val state = binding.referenceAddressLayout.customPermanentZipAddressView.spinnerCurrentState.selectedItem as StatesMaster?
        val district = binding.referenceAddressLayout.customPermanentZipAddressView.spinnerCurrentDistrict.selectedItem as Response.DistrictObj?
        val city = binding.referenceAddressLayout.customPermanentZipAddressView.spinnerCurrentCity.selectedItem as Response.CityObj?

        if (relation == null) {
            errorCount++
            binding.spinnerRelation.error = "Required Field"
        }

        if (occupation == null) {
            errorCount++
            binding.spinnerOccupation.error = "Required Field"
        }

        if (district == null) {
            errorCount++
            binding.referenceAddressLayout.customPermanentZipAddressView.spinnerCurrentDistrict.error = "Required Field"
        }
        if (city == null) {
            errorCount++
            binding.referenceAddressLayout.customPermanentZipAddressView.spinnerCurrentCity.error = "Required Field"
        }
        if (state == null) {
            errorCount++
            binding.referenceAddressLayout.customPermanentZipAddressView.spinnerCurrentState.error = "Required Field"
        }

        if (!name.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            binding.etName.error = "Name can not be blank"
        }

        val contact = binding.referenceAddressLayout.etContactNum.text.toString()
        if (!contact.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            binding.referenceAddressLayout.etContactNum.error = "Contact can not be blank"
        }

        val address = binding.referenceAddressLayout.etAddress.toString()
        if (!address.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            binding.referenceAddressLayout.etAddress.error = "Address can not be blank"
        }

        val landmark = binding.referenceAddressLayout.etLandmark.text.toString()
        if (!landmark.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            binding.referenceAddressLayout.etLandmark.error = "Landmark can not be blank"
        }

        val pinCode = binding.referenceAddressLayout.customPermanentZipAddressView.etCurrentPinCode.text.toString()
        if (!pinCode.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            binding.referenceAddressLayout.customPermanentZipAddressView.etCurrentPinCode.error = "Pin code can not be blank"
        }

        return isValidForm(errorCount)
    }

    override fun validateProperty(binding: FragmentPropertyInfoBinding): Boolean {
        var errorCount = 0
        val unitType = binding.spinnerUnitType.selectedItem as DropdownMaster?
        val ownership = binding.spinnerOwnership.selectedItem as DropdownMaster?
        val ownedProperty = binding.spinnerOwnedProperty.selectedItem as DropdownMaster?
        val propertyTransaction = binding.spinnerPropertyNature.selectedItem as DropdownMaster?
        val tenantNoc = binding.spinnerTenantNocAvailable.selectedItem as DropdownMaster?
        val occupiedBy = binding.spinnerOccupiedBy.selectedItem as DropdownMaster?
        val state = binding.spinnerState.selectedItem as StatesMaster?
        val district = binding.spinnerDistrict.selectedItem as Response.DistrictObj?
        val city = binding.spinnerCity.selectedItem as Response.CityObj?
        val propertyType = binding.spinnerPropertytype.selectedItem as DropdownMaster?
        val transactionType=binding.spinnerTrancactiontype.selectedItem as DropdownMaster?
        val loanPurposeId = LeadMetaData.getLeadData()?.loanData?.loanPurposeID

        /*if (unitType == null) {
            errorCount++
            binding.spinnerUnitType.error = "Required Field"
        }*/

        /*if (propertyTransaction == null) {
            errorCount++
            binding.spinnerPropertyNature.error = "Required Field"
        }*/
        if (occupiedBy == null) {
            errorCount++
            binding.spinnerOccupiedBy.error = "Required Field"
        }

        if (ownership == null) {
            errorCount++
            binding.spinnerOwnership.error = "Required Field"
        }

        if (ownedProperty == null) {
            errorCount++
            binding.spinnerOwnedProperty.error = "Required Field"
        }
        if (propertyType == null) {
            errorCount++
            binding.spinnerPropertytype.error = "Required Field"
        }
        if (transactionType == null) {
            errorCount++
            binding.spinnerTrancactiontype.error = "Required Field"
        }

        if (district == null) {
            errorCount++
            binding.spinnerDistrict.error = "Required Field"
        }
        if (city == null) {
            errorCount++
            binding.spinnerCity.error = "Required Field"
        }
        if (state == null) {
            errorCount++
            binding.spinnerState.error = "Required Field"
        }




        val propertyMv = binding.etMvProperty.text.toString()

        if (!propertyMv.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            binding.etMvProperty.error = "Required Field"
        }
        val propertyArea = binding.etPropertyArea.text.toString()
        if (!propertyArea.exIsNotEmptyOrNullOrBlank() || propertyArea.toInt() <= 0) {
            errorCount++
            binding.etPropertyArea.error = "Invalid Value"
        }


        if(occupiedBy?.typeDetailID !=182){

        }else{
            val tenants = binding.etNumOfTenants.text.toString()
            if (!tenants.exIsNotEmptyOrNullOrBlank()) {
                errorCount++
                binding.etNumOfTenants.error = "Required Field"
            }


            if (tenantNoc == null) {
                errorCount++
                binding.spinnerTenantNocAvailable.error = "Required Field"
            }

        }

        var ocr= ""
        var cashOcr= ""
        var agreementValue= ""
        if((loanPurposeId !=1) && (loanPurposeId !=6) && (loanPurposeId != 9) && (loanPurposeId !=10) && (loanPurposeId != 11) && (loanPurposeId != 12) && (loanPurposeId != 13)) {
            cashOcr = binding.etCashOcr.text.toString()
            if (!cashOcr.exIsNotEmptyOrNullOrBlank()) {
                errorCount++
                binding.etCashOcr.error = "Required Field"
            }

            ocr = binding.etOcr.text.toString()
            if (!ocr.exIsNotEmptyOrNullOrBlank()) {
                errorCount++
                binding.etOcr.error = "Required Field"
            }

            agreementValue = binding.etAgreementValue.text.toString()
            if (!agreementValue.exIsNotEmptyOrNullOrBlank()) {
                errorCount++
                binding.etAgreementValue.error = "Required Field"
            }

            if (ocr.exIsNotEmptyOrNullOrBlank() && cashOcr.exIsNotEmptyOrNullOrBlank()) {
                if (CurrencyConversion().convertToNormalValue(cashOcr).toDouble() > CurrencyConversion().convertToNormalValue(ocr).toDouble()) {
                    errorCount++

                    binding.etCashOcr.error = "Cannot be greater than  OCR"
                }
            } else {
                errorCount++
                binding.etCashOcr.error = "Cannot be greater than  OCR"
            }

            if (propertyMv.exIsNotEmptyOrNullOrBlank() && agreementValue.exIsNotEmptyOrNullOrBlank()) {
                if (CurrencyConversion().convertToNormalValue(propertyMv).toDouble() < CurrencyConversion().convertToNormalValue(agreementValue).toDouble()) {
                    errorCount++
                    binding.etAgreementValue.error = "Cannot be greater than MV of property"
                }

            } else {
                binding.etAgreementValue.error = "Cannot be greater than MV of property"
            }

            if (propertyMv.exIsNotEmptyOrNullOrBlank() && agreementValue.exIsNotEmptyOrNullOrBlank()) {
                if (CurrencyConversion().convertToNormalValue(ocr).toDouble() > CurrencyConversion().convertToNormalValue(propertyMv).toDouble() ||
                        CurrencyConversion().convertToNormalValue(ocr).toDouble() > CurrencyConversion().convertToNormalValue(agreementValue).toDouble()) {
                    errorCount++
                    binding.etOcr.error = "Cannot be greater than MV of Property or agreement value"
                }
            } else {
                binding.etOcr.error = "Cannot be greater than MV of Property or agreement value"
            }
        } else {
            Log.e("Tag","Sandeep :")
        }

        val pin = binding.etPinCode.text.toString()
        if (!pin.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            binding.etPinCode.error = "Required Field"
        }

        val propertyAddress = binding.etPropertyAddress.text.toString()
        if (!propertyAddress.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            binding.etPropertyAddress.error = "Required Field"
        }

        val landmark = binding.etLandmark.text.toString()
        if (!landmark.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            binding.etLandmark.error = "Required Field"
        }

        return isValidForm(errorCount)
    }

    override fun validateAssetLiabilityForm(binding: FragmentAssetLiablityBinding): Boolean {
        var errorCount = 0
        if (!validateAssets(binding) || !validateCards(binding.layoutCreditCard) || !validateObligations(binding.layoutObligations))
            errorCount++
        return isValidForm(errorCount)
    }

    override fun validateAssets(binding: FragmentAssetLiablityBinding): Boolean {
        val assetType = binding.spinnerAssetType.selectedItem as DropdownMaster?
        val assetSubType = binding.spinnerAssetSubType.selectedItem as DropdownMaster?
        val ownership = binding.spinnerOwnership.selectedItem as DropdownMaster?
        val documentProof = binding.spinnerDocumentProof.selectedItem as DropdownMaster?
        val value = binding.etValue.text.toString()

        val errorCount = when {
            !value.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etValue)
            assetSubType == null -> setSpinnerError(binding.spinnerAssetSubType)
            assetType == null -> setSpinnerError(binding.spinnerAssetType)
            ownership == null -> setSpinnerError(binding.spinnerOwnership)
            documentProof == null -> setSpinnerError(binding.spinnerDocumentProof)
            else -> 0
        }
        return isValidForm(errorCount)
    }

    override fun validateCards(binding: LayoutCreditCardDetailsBinding): Boolean {
        val bankName = binding.spinnerBankName.selectedItem as DropdownMaster?
        val obligate = binding.spinnerObligate.selectedItem as DropdownMaster?
        val cardLimit = binding.etCreditCardLimit.text.toString()
        val utilization = binding.etCurrentUtilization.text.toString()

        val spinnerError = when {
            bankName == null -> setSpinnerError(binding.spinnerBankName)
            obligate == null -> setSpinnerError(binding.spinnerObligate)
            else -> 0
        }

        val fieldError = when {
            !utilization.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etCurrentUtilization)
            !cardLimit.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etCreditCardLimit)
            CurrencyConversion().convertToNormalValue(cardLimit).toDouble() < CurrencyConversion().convertToNormalValue(utilization).toDouble() ->
                setFieldError(binding.etCurrentUtilization)
            else -> 0
        }
        val errorCount = spinnerError + fieldError
        return isValidForm(errorCount)
    }

    override fun validateObligations(binding: LayoutObligationBinding): Boolean {
        val obligate = binding.spinnerObligate.selectedItem as DropdownMaster?
        val loanType = binding.spinnerLoanType.selectedItem as DropdownMaster?
        val repaymentBank = binding.spinnerRepaymentBank.selectedItem as DropdownMaster?
        val ownership = binding.spinnerLoanOwnership.selectedItem as DropdownMaster?
        val financierName = binding.etFinancierName.text.toString()
        val accountNum = binding.etAccountNum.text.toString()
        val balanceTenure = binding.etBalanceTenure.text.toString()
        val tenure = binding.etTenure.text.toString()
        val emiAmount = binding.etEmiAmount.text.toString()
        val bouncesIn6 = binding.etBouncesInLastSixMonths.text.toString()
        val bouncesIn9 = binding.etBouncesInLastNineMonths.text.toString()

        val spinnerError = when {
            loanType == null -> setSpinnerError(binding.spinnerLoanType)
            obligate == null -> setSpinnerError(binding.spinnerObligate)
            repaymentBank == null -> setSpinnerError(binding.spinnerRepaymentBank)
            ownership == null -> setSpinnerError(binding.spinnerLoanOwnership)
            else -> 0
        }

        val fieldError = when {
            !financierName.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etFinancierName)
//            !accountNum.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etAccountNum)
            !tenure.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etTenure)
            !balanceTenure.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etBalanceTenure)
            !emiAmount.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etEmiAmount)
            !bouncesIn6.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etBouncesInLastSixMonths)
            !bouncesIn9.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etBouncesInLastNineMonths)
            else -> 0
        }
        val errorCount = spinnerError + fieldError
        return isValidForm(errorCount)
    }

    override fun validateObligationDialog(binding: AddObligationDialogBinding): Boolean {
        val obligate = binding.spinnerObligate.selectedItem as DropdownMaster?
        val loanType = binding.spinnerLoanType.selectedItem as DropdownMaster?
        var repaymentBank = binding.spinnerRepaymentBank.tag as DropdownMaster?
        val ownership = binding.spinnerLoanOwnership.selectedItem as DropdownMaster?
        val financierName = binding.etFinancierName.text.toString()
        val accountNum = binding.etAccountNum.text.toString()
        val balanceTenure = binding.etBalanceTenure.text.toString()
        val tenure = binding.etTenure.text.toString()
        val emiAmount = binding.etEmiAmount.text.toString()
        val bouncesIn6 = binding.etBouncesInLastSixMonths.text.toString()
        val bouncesIn9 = binding.etBouncesInLastNineMonths.text.toString()
        val loanAmount=binding.etLoanAmount.text.toString()

        var errorBank:Int=0
        if(loanType?.typeDetailID == Constants.KCC_LOAN || loanType?.typeDetailID == Constants.GOLD_LOAN || loanType?.typeDetailID == Constants.EMPLOYER_LOAN)
        {
            repaymentBank = null
        }
        else
        {
            if (repaymentBank==null){
                errorBank++
                binding.spinnerRepaymentBank.error = "Required Field"

            }
        }



        val spinnerError = when {
            loanType == null -> setSpinnerError(binding.spinnerLoanType)
            obligate == null -> setSpinnerError(binding.spinnerObligate)
            // repaymentBank == null -> setSpinnerError(binding.spinnerRepaymentBank)
            ownership == null -> setSpinnerError(binding.spinnerLoanOwnership)
            else -> 0
        }

        val fieldError = when {
            !financierName.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etFinancierName)
           // !accountNum.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etAccountNum)
            !tenure.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etTenure)
            !balanceTenure.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etBalanceTenure)
            !emiAmount.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etEmiAmount)
            !bouncesIn6.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etBouncesInLastSixMonths)
            !bouncesIn9.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etBouncesInLastNineMonths)
            !loanAmount.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etLoanAmount)

            else -> 0
        }
        var errorCount = spinnerError + fieldError+errorBank
        if(bouncesIn6.exIsNotEmptyOrNullOrBlank()){
            if (bouncesIn6.toInt() >6 ){
                binding.etBouncesInLastSixMonths.error = "Number of check bounce not more than six."
            }
        }

        if (bouncesIn6.exIsNotEmptyOrNullOrBlank() && bouncesIn9.exIsNotEmptyOrNullOrBlank()) {
            if (binding.etBouncesInLastSixMonths?.text.toString().toInt() > binding.etBouncesInLastNineMonths?.text.toString().toInt()) {
                errorCount++
                binding.etBouncesInLastNineMonths.error = "Number of check bounce not less than six"
            }
        }

        if( !tenure.equals("") && !balanceTenure.equals("") ){
            if(tenure.toInt() < balanceTenure.toInt()) {
                errorCount++
                binding.etBalanceTenure.error = "Balance Tenure  value is not more than Tenure Value"
            }
        }


        return isValidForm(errorCount)
    }

    override fun validateAddLead(
            binding: ActivityLeadCreateBinding, loanProduct: CustomSpinnerView<LoanProductMaster>,
            branches: CustomSpinnerView<UserBranches>,customChannelPartnerViewCreateLead: ChannelPartnerViewCreateLead
    ): Boolean {
        val area = binding.etArea.text.toString()
        val name = binding.etApplicantFirstName.text.toString()
        val email = binding.etEmail.text.toString()
        val contact = binding.etContactNum.text.toString()
        val loanAmount: String = binding.etLoanAmount.text.toString()


        val fieldError = (when {
            !name.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etApplicantFirstName)
            !loanAmount.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etLoanAmount)
            !isValidMobile(contact) -> setFieldError(binding.etContactNum)
            !area.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etArea)
            !isValidEmail(email) -> setFieldError(binding.etEmail)

            else -> 0
        })

        val loan = loanProduct.getSelectedValue()
        if (loan != null && loanAmount != "") {

            if (loanAmount.toInt() > loan.maxAmount || loanAmount.toInt() < loan.minAmount) {
                binding.etLoanAmount.error = "Range:${loan.minAmount} - ${loan.maxAmount}"
            }
        }

        var spinnerError = 0
        if (!loanProduct.isValid()) {
            ++spinnerError
        }
        if (!branches.isValid()) ++spinnerError

        val sourcingPartnerName: String = customChannelPartnerViewCreateLead.getSourcingPartner().toString()
        if (sourcingPartnerName == "null") {
            spinnerError++

        }

        return isValidForm(fieldError + spinnerError)
    }

    override fun validateUpdateCallForm(binding: ActivityUpdateCallBinding, formType: UpdateCallActivity.RequestLayout): Boolean {
        var errorCount = 0

        if (formType == UpdateCallActivity.RequestLayout.FOLLOW_UP) {
            val leadType = binding.layoutFollowUp.spinnerLeadType.selectedItem as DropdownMaster?
            if (leadType == null) {
                binding.layoutFollowUp.spinnerLeadType.error = "Please select lead type"
                errorCount++
            }

            val followUpTiming = binding.layoutFollowUp.etFollowUpTiming.text.toString()
            if (followUpTiming.isEmpty()) {
                binding.layoutFollowUp.etFollowUpTiming.error = "Required Field"
                errorCount++
            }
        }

        if (formType == UpdateCallActivity.RequestLayout.FIX_MEETING) {
            val notificationType = binding.layoutFixedMeeting.spinnerNotificationType.selectedItem as DropdownMaster?
            if (notificationType == null) {
                binding.layoutFixedMeeting.spinnerNotificationType.error = "Please select notification type"
                errorCount++
            }

            val fixedMeetingTiming = binding.layoutFixedMeeting.etMeetingDate.text.toString()
            if (fixedMeetingTiming.isEmpty()) {
                binding.layoutFixedMeeting.etMeetingDate.error = "Required Field"
                errorCount++
            }
        }

        if (formType == UpdateCallActivity.RequestLayout.NOT_INTERESTED) {
            val leadCLoseReasonType = binding.layoutNotInterested.spinnerLeadCloseReason.selectedItem as DropdownMaster?
            if (leadCLoseReasonType == null) {
                binding.layoutNotInterested.spinnerLeadCloseReason.error = "Please select reason"
                errorCount++
            }
        }

        return isValidForm(errorCount)
    }

    override fun validateCardsDialog(binding: AssetCreditcardDialogBinding): Boolean {
        val bankName = binding.spinnerBankName.tag as DropdownMaster?
        val obligate = binding.spinnerObligate.selectedItem as DropdownMaster?
        val cardLimit = binding.etCreditCardLimit.text.toString()
        val utilization = binding.etCurrentUtilization.text.toString()
        var bankerror:Int=0
        if (bankName == null) {
            bankerror++
            binding.spinnerBankName.error = "Required Field"
        }

        val spinnerError = when {
            // bankName == null -> setSpinnerError(binding.spinnerBankName)
            obligate == null -> setSpinnerError(binding.spinnerObligate)
            else -> 0
        }

        val fieldError = when {
            !utilization.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etCurrentUtilization)
            !cardLimit.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etCreditCardLimit)
            CurrencyConversion().convertToNormalValue(cardLimit).toDouble() < CurrencyConversion().convertToNormalValue(utilization).toDouble() ->
                setFieldError(binding.etCurrentUtilization)
            else -> 0
        }
        val errorCount = spinnerError + fieldError+bankerror
        return isValidForm(errorCount)
    }

    override fun validateAssetsDialog(binding: AddAssestsDialogBinding): Boolean {
        val assetType = binding.spinnerAssetType.selectedItem as DropdownMaster?
        val assetSubType = binding.spinnerAssetSubType.selectedItem as DropdownMaster?
        val ownership = binding.spinnerOwnership.selectedItem as DropdownMaster?
        val documentProof = binding.spinnerDocumentProof.selectedItem as DropdownMaster?
        val value = binding.etValue.text.toString()

        val errorCount = when {
            !value.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etValue)
            assetSubType == null -> setSpinnerError(binding.spinnerAssetSubType)
            assetType == null -> setSpinnerError(binding.spinnerAssetType)
            ownership == null -> setSpinnerError(binding.spinnerOwnership)
            documentProof == null -> setSpinnerError(binding.spinnerDocumentProof)
            else -> 0
        }
        return isValidForm(errorCount)
    }

    override fun validateAssetLiabilityInfo(binding: LayoutCustomviewAssetliabilityBinding): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validateKycDetail(binding: LayoutKycFormBinding): Boolean {
        var errorCount = 0
        var checkIskycdata = 0

        val idNum = binding.etIdNum.text.toString()
        if (!idNum.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            binding.etIdNum.error = "Required Field"
        }

        val identificationType = binding.spinnerIdentificationType.selectedItem as DropdownMaster?
        if (identificationType == null) {
            errorCount++
            binding.spinnerIdentificationType.error = "Required Field"
        }

        val verifiedStatus = binding.spinnerVerifiedStatus.selectedItem as DropdownMaster?
        if (verifiedStatus == null) {
            errorCount++
            binding.spinnerVerifiedStatus.error = "Required Field"
        }


        if(identificationType != null  && identificationType.typeDetailCode.equals("PAN")){
            val patternPan: Pattern = Pattern.compile("([a-zA-Z]){5}([0-9]){4}([a-zA-Z]){1}")
                    val matcher: Matcher = patternPan.matcher(idNum)
            if(matcher.matches()){ }else{
                errorCount++
                binding.etIdNum.error = "Please enter valid Pan number."
            }
        } else if(identificationType != null  && identificationType.typeDetailCode.equals("UID")){
            val patternUid: Pattern = Pattern.compile("([0-9]){12}")
            val matcher: Matcher = patternUid.matcher(idNum)
            if(matcher.matches()){ }else{
                errorCount++
                binding.etIdNum.error = "Please enter valid Adhar number."
            }
        }else if(identificationType != null  && identificationType.typeDetailCode.equals("Passport")){
            val patternUid: Pattern = Pattern.compile("(?!^0+\$)[a-zA-Z0-9]{3,20}$")
            val matcher: Matcher = patternUid.matcher(idNum)
            if(matcher.matches()){ }else{
                errorCount++
                binding.etIdNum.error = "Please enter valid Passport number."
            }
        }else if(identificationType != null  && identificationType.typeDetailCode.equals("Driving License")){
            val patternUid: Pattern = Pattern.compile("[a-zA-Z0-9-_ ]*\$")
            val matcher: Matcher = patternUid.matcher(idNum)
            if(matcher.matches()){ }else{
                errorCount++
                binding.etIdNum.error = "Please enter valid Driving License number."
            }

        }else if(identificationType != null  && identificationType.typeDetailCode.equals("VoterID")) {
            val patternUid: Pattern = Pattern.compile("[a-zA-Z0-9-_ ]*\$")
            val matcher: Matcher = patternUid.matcher(idNum)
            if (matcher.matches()) {
            } else {
                errorCount++
                binding.etIdNum.error = "Please enter valid VoterId number."
            }
        }
        //Check Duplicate Value



        return isValidForm(errorCount)
    }

    override fun validateKycDocumentDetail(binding: ActivityDocumentUploadingBinding): Boolean {
        var errorCount = 0

        val idNum = binding.etDocumentName.text.toString()
        if (!idNum.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            binding.etDocumentName.error = "Required Field"
        }

        val documentType = binding.spinnerDocumentType.selectedItem as DocumentTypeModel?
        if (documentType == null) {
            errorCount++
            binding.spinnerDocumentType.error = "Required Field"
        }

        return isValidForm(errorCount)
    }

    override fun validateKycDocumentDetailSelf(binding: SelfDeclarationUploadDocumentActivityBinding): Boolean {
        var errorCount = 0

        val idNum = binding.etDocumentName.text.toString()
        if (!idNum.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            binding.etDocumentName.error = "Required Field"
        }

        val documentType = binding.spinnerDocumentType.selectedItem as DocumentTypeModel?
        if (documentType == null) {
            errorCount++
            binding.spinnerDocumentType.error = "Required Field"
        }

        return isValidForm(errorCount)
    }

    override fun validateUploadKycDocumentDetail(binding: PerformKycDocumentUploadActivityBinding): Boolean {
        var errorCount = 0

        val idNum = binding.etDocumentName.text.toString()
        if (!idNum.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            binding.etDocumentName.error = "Required Field"
        }

        val documentType = binding.spinnerDocumentType.selectedItem as DocumentTypeModel?
        if (documentType == null) {
            errorCount++
            binding.spinnerDocumentType.error = "Required Field"
        }

        return isValidForm(errorCount)
    }




    override  fun validateResetPassword(binding:ActivityResetPasswordBinding):Boolean{
        var errorCount = 0
      val oldPassword =binding.etOldPassword.text.toString()
        val newPassword =binding.etNewPassword.text.toString()
        val confirmPassword =binding.etConfirmNewPassword.text.toString()

        val fieldError = (when {
            !oldPassword.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etOldPassword)
            !newPassword.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etNewPassword)
            !confirmPassword.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etConfirmNewPassword)
           /* !isValidPassword(newPassword) -> setFieldError(binding.etNewPassword)
            !isValidPassword(confirmPassword) -> setFieldError(binding.etConfirmNewPassword*/

            else -> 0
        })

        if(newPassword.length<8 || confirmPassword.length < 8 ){
            binding.etNewPassword.error= "Password length must have 8"
            binding.etConfirmNewPassword.error= "Password length must have 8"
        } else if(!isValidPassword(newPassword)){  binding.etNewPassword.error= "Password must have alphanumeric, 1 caps at least one special character"
        } else if(!isValidPassword(confirmPassword)) {
            binding.etConfirmNewPassword.error = "Password must have alphanumeric, 1 caps at least one special character"
        }else if(newPassword.exIsNotEmptyOrNullOrBlank() &&  confirmPassword.exIsNotEmptyOrNullOrBlank()) {
            if(!newPassword.equals(confirmPassword)) {
                errorCount++
                binding.etNewPassword.error = " Password not match"
                binding.etConfirmNewPassword.error = " Password not match"
            }
        }

      return isValidForm(errorCount +fieldError)
    }

    private fun setSpinnerError(spinner: MaterialSpinner): Int {
        spinner.error = "Required Field"
        return 2
    }

    private fun setFieldError(field: TextInputEditText): Int {
        field.error = "Invalid Input"
        return 2
    }
    private fun setFieldError(field: EditTexNormal): Int {
        field.error = "Invalid Input"
        return 2
    }

    override fun validateTemp(binding: TempActivityBinding): Boolean {
        val errorCount = 0
        return isValidForm(errorCount)
    }

    override fun validateForgetPassword(binding: ActivityForgetPasswordBinding) : Boolean {
        val strUserName = binding.etMobile.text.toString()
        var errorCount = 0
        if(!strUserName.exIsNotEmptyOrNullOrBlank()){
            binding.etMobile.error = "Field can not be left blank"
            errorCount++
        }
        else{
            errorCount =0
        }
        return isValidForm(errorCount)
    }

    private fun isValidMobile(phone: String): Boolean {
        return if (!phone.exIsNotEmptyOrNullOrBlank()) {
            return android.util.Patterns.PHONE.matcher(phone).matches()
        } else true
    }

    private fun isValidEmail(email: String): Boolean {
        return if (email.exIsNotEmptyOrNullOrBlank()) {
            (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        } else true
    }

    private fun isValidForm(errorCount: Int): Boolean = errorCount <= 0

    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }
 override fun validatePassword(binding: ActivitySetPasswordBinding) : Boolean{
     val strPassword = binding.etNewPassword.text.toString()
     val strConfirmPassword = binding.etConfirmNewPassword.text.toString()
     var errorCount = 0
     val fieldError = (when {
         !strPassword.exIsNotEmptyOrNullOrBlank() -> setFieldError(binding.etNewPassword)
         else -> 0
     })
     if(strPassword.length < 8){
         binding.etNewPassword.error= "Password length must have 8"
         errorCount++
     } else if (!(strPassword.equals(strConfirmPassword)))
     {
         errorCount++
         binding.etConfirmNewPassword.error = "New password and confirm password not matched"
     }
     else if(!isValidPassword(strPassword)){
         binding.etNewPassword.error= "Password must have alphanumeric, 1 caps and at least one special character"
         errorCount++
     }
     return isValidForm(errorCount +fieldError)
 }

}
