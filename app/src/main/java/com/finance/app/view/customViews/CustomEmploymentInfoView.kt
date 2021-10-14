package com.finance.app.view.customViews

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.databinding.LayoutCustomEmploymentViewBinding
import com.finance.app.databinding.LayoutEmploymentAddressBinding
import com.finance.app.databinding.LayoutSalaryBinding
import com.finance.app.databinding.LayoutSenpBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.*
import com.finance.app.utility.*
import com.finance.app.view.customViews.interfaces.IspinnerMainView
import com.finance.app.view.utils.EditTexNormal
import com.google.android.material.textfield.TextInputEditText
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.Constants.APP.ASSESED_INCOME
import motobeans.architecture.constants.Constants.APP.BANK_SALARY
import motobeans.architecture.constants.Constants.APP.CASH_SALARY
import motobeans.architecture.constants.Constants.APP.ITR
import motobeans.architecture.constants.Constants.APP.SALARY
import motobeans.architecture.constants.Constants.APP.SENP
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.util.AppUtilExtensions
import motobeans.architecture.util.AppUtils.showToast
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import javax.inject.Inject


class CustomEmploymentInfoView @JvmOverloads constructor(val mContext: Context , attrs: AttributeSet? = null) : LinearLayout(mContext , attrs) {

    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var formValidation: FormValidation

    private lateinit var binding: LayoutCustomEmploymentViewBinding

    private lateinit var activity: FragmentActivity
    //    private var index: Int = 0
    private var formType: Int = -1
    private var lastYearIncome = 0.0f
    private var currentYearIncome = 0.0f
    private var grossIncome = 0.0f
    private var deduction = 0.0f
    private var netIncome = ""
    private var averageMonthlyIncome = ""
    private lateinit var selectedApplicant: PersonalApplicantsModel
    private var selectedEmploymentDetails: EmploymentApplicantsModel? = null
    private lateinit var profile: CustomSpinnerView<DropdownMaster>
    private lateinit var subProfile: CustomSpinnerView<DropdownMaster>
    private lateinit var sector: CustomSpinnerView<DropdownMaster>
    private lateinit var salaryIndustry: CustomSpinnerView<DropdownMaster>
    private lateinit var senpIndustry: CustomSpinnerView<DropdownMaster>
    private lateinit var employmentType: CustomSpinnerView<DropdownMaster>
    private lateinit var constitution: CustomSpinnerView<DropdownMaster>
    private lateinit var businessSetUpType: CustomSpinnerView<DropdownMaster>
    private var senpSpinnerList: ArrayList<CustomSpinnerView<DropdownMaster>> = ArrayList()
    private var salarySpinnerList: ArrayList<CustomSpinnerView<DropdownMaster>> = ArrayList()

    fun attachView(activity: FragmentActivity , selectedApplicant: PersonalApplicantsModel , employmentDetails: EmploymentApplicantsModel?) {
        ArchitectureApp.instance.component.inject(this)
        this.activity = activity
        this.selectedApplicant = selectedApplicant
        this.selectedEmploymentDetails = employmentDetails
        binding = AppUtilExtensions.initCustomViewBinding(context = context , layoutId = R.layout.layout_custom_employment_view , container = this)

//        if (selectedApplicant.incomeConsidered) {
//            binding.lytEmploymentForm.visibility = View.VISIBLE
//            binding.lytEmploymentForm.visibility = View.GONE
//        } else {
//            binding.lytEmploymentForm.visibility = View.GONE
//        }


        initializeViews(employmentDetails)
    }

    private fun initializeViews(applicant: EmploymentApplicantsModel?) {
        System.out.println("1>>>>>>>>>>>>")
        setDatePicker()
        setUpCustomViews()
        setClickListeners()
        proceedFurther(applicant)


    }

    private fun setDatePicker() {
        binding.layoutSalary.etJoiningDate.setOnClickListener {
            SelectDate(binding.layoutSalary.etJoiningDate , context)
        }

        binding.layoutSenp.etIncorporationDate.setOnClickListener {
            DateDifference(context , binding.layoutSenp.etIncorporationDate , binding.layoutSenp.etBusinessVintage)
        }
    }

    private fun setUpCustomViews() {
        binding.layoutSalary.layoutAddress.customZipAddressView.attachActivity(activity = activity)
        binding.layoutSenp.layoutAddress.customZipAddressView.attachActivity(activity = activity)
    }

    private fun setClickListeners() {
        salaryIncomeListener(binding.layoutSalary.etGrossIncome , AppEnums.INCOME_TYPE.GROSS_INCOME)
        salaryIncomeListener(binding.layoutSalary.etDeduction , AppEnums.INCOME_TYPE.DEDUCTION)
        senpIncomeListener(binding.layoutSenp.etLastYearIncome , AppEnums.INCOME_TYPE.LAST_YEAR_INCOME)
        senpIncomeListener(binding.layoutSenp.etCurrentYearIncome , AppEnums.INCOME_TYPE.CURRENT_YEAR_INCOME)

        CurrencyConversion().convertToCurrencyType(binding.layoutSalary.etGrossIncome)
        CurrencyConversion().convertToCurrencyType(binding.layoutSalary.etNetIncome)
        CurrencyConversion().convertToCurrencyType(binding.layoutSalary.etDeduction)
        CurrencyConversion().convertToCurrencyType(binding.layoutSenp.etLastYearIncome)
        CurrencyConversion().convertToCurrencyType(binding.layoutSenp.etCurrentYearIncome)
        CurrencyConversion().convertToCurrencyType(binding.layoutSenp.etAverageMonthlyIncome)
        CurrencyConversion().convertToCurrencyType(binding.layoutSenp.etMonthlyIncome)
    }

    private fun salaryIncomeListener(amountField: EditTexNormal? , type: AppEnums.INCOME_TYPE) {
        amountField?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence , start: Int , count: Int , after: Int) {}
            override fun onTextChanged(s: CharSequence , start: Int , before: Int , count: Int) {
                when (type) {
                    AppEnums.INCOME_TYPE.GROSS_INCOME -> grossIncome = getIncomeValue(amountField.text.toString())
                    AppEnums.INCOME_TYPE.DEDUCTION -> deduction = getIncomeValue(amountField.text.toString())
                    else -> return
                }

                if (grossIncome > deduction) {
                    val netIncome = (grossIncome - deduction).toString()
                    binding.layoutSalary.etNetIncome.setText(netIncome)
                }
            }
        })
    }

    private fun senpIncomeListener(amountField: EditTexNormal? , type: AppEnums.INCOME_TYPE) {
        amountField?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence , start: Int , count: Int , after: Int) {}
            override fun onTextChanged(s: CharSequence , start: Int , before: Int , count: Int) {
                when (type) {
                    AppEnums.INCOME_TYPE.LAST_YEAR_INCOME -> lastYearIncome = getIncomeValue(amountField.text.toString())
                    AppEnums.INCOME_TYPE.CURRENT_YEAR_INCOME -> currentYearIncome = getIncomeValue(amountField.text.toString())
                    else -> return
                }
                val averageMonthlyIncome = ((lastYearIncome + currentYearIncome) / 2).toString()
                binding.layoutSenp.etAverageMonthlyIncome.setText(averageMonthlyIncome)
            }
        })
    }

    private fun getIncomeValue(amount: String): Float {
        if (amount.exIsNotEmptyOrNullOrBlank()) {
            val stringAmount = CurrencyConversion().convertToNormalValue(amount)
            return stringAmount.toFloat()
        }
        return 0.0f
    }

    private fun proceedFurther(applicant: EmploymentApplicantsModel?) {
        getDropDownsFromDB(applicant)
    }

    private fun getDropDownsFromDB(applicant: EmploymentApplicantsModel?) {
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(activity ,
                Observer { MasterDropdown ->
                    MasterDropdown?.let {
                        setMasterDropDownValue(applicant , MasterDropdown)
                    }
                })
    }

    private fun setMasterDropDownValue(applicant: EmploymentApplicantsModel? , dropDowns: AllMasterDropDown) {
        setCustomSpinner(dropDowns , applicant)
        fillValueInCustomSpinner(applicant)
    }

    private fun setCustomSpinner(dropDowns: AllMasterDropDown , applicant: EmploymentApplicantsModel?) {

        subProfile = CustomSpinnerView(
                mContext = context , isMandatory = true ,
                dropDowns = dropDowns.SubProfileSegment , label = "Sub Profile *"
        )
        binding.layoutSubProfile.addView(subProfile)

        profile = CustomSpinnerView(mContext = context , isMandatory = true ,
                dropDowns = dropDowns.ProfileSegment!! , label = "Profile *" ,
                iSpinnerMainView = object : IspinnerMainView<DropdownMaster> {
                    override fun getSelectedValue(value: DropdownMaster) {
                        setSubProfileSegment(value , dropDowns , applicant)
                    }
                })
        binding.layoutProfile.addView(profile)

        setSalaryDropDown(binding.layoutSalary , dropDowns)
        setSenpDropDown(binding.layoutSenp , dropDowns)
    }

    private fun setSubProfileSegment(value: DropdownMaster , dropDowns: AllMasterDropDown , applicant: EmploymentApplicantsModel?) {

        val subProfileSegments: ArrayList<DropdownMaster> = ArrayList()
        for (sub in dropDowns.SubProfileSegment!!) {
            if (sub.refTypeDetailID == value.typeDetailID) {
                subProfileSegments.add(sub)
            }
        }
        binding.layoutSubProfile.removeAllViews()
        subProfile = CustomSpinnerView(mContext = context , isMandatory = true ,
                dropDowns = subProfileSegments , label = "Sub Profile *" ,
                iSpinnerMainView = object : IspinnerMainView<DropdownMaster> {
                    override fun getSelectedValue(value: DropdownMaster) {
                        showFormBasedOnUserSelection(value.typeDetailID)
                    }
                })

        binding.layoutSubProfile.addView(subProfile)
        applicant?.subProfileTypeDetailID?.let {
            subProfile.setSelection(applicant?.subProfileTypeDetailID.toString())
        }

    }

    private fun showFormBasedOnUserSelection(id: Int) {
        when (id) {
            CASH_SALARY , BANK_SALARY -> {
                formType = SALARY
                showSalaryForm(id)
            }
            ITR , ASSESED_INCOME -> {
                formType = SENP
                showSenpForm(id)
            }
        }
    }

    private fun fillValueInCustomSpinner(applicant: EmploymentApplicantsModel?) {
        applicant?.profileSegmentTypeDetailID?.let {
            profile.setSelection(applicant.profileSegmentTypeDetailID.toString())
        }

        applicant?.let { fillSenpForm(binding.layoutSenp , it) }
        applicant?.let { fillSalaryForm(binding.layoutSalary , it) }
        checkIncomeAndSubmission()
    }

    private fun showSalaryForm(id: Int) {
        if (id == BANK_SALARY) {
            binding.layoutSalary.cbIsPensioner.visibility = View.VISIBLE
        } else {
            binding.layoutSalary.cbIsPensioner.visibility = View.GONE
        }
        binding.layoutSalary.llSalary.visibility = View.VISIBLE
        binding.layoutSenp.llSenp.visibility = View.GONE
        ClearEmploymentForm(binding).clearSenpForm(senpSpinnerList)
    }

    private fun showSenpForm(id: Int) {
        if (id == ASSESED_INCOME) {
            binding.layoutSenp.cbAllEarningMember.visibility = View.VISIBLE
            binding.layoutSenp.lastCurrentIncome.visibility = View.GONE
            binding.layoutSenp.inputMonthlyIncome.visibility = View.VISIBLE
            binding.layoutSenp.inputAverageMonthlyIncome.visibility = View.GONE
        } else {
            binding.layoutSenp.cbAllEarningMember.visibility = View.GONE
            binding.layoutSenp.inputMonthlyIncome.visibility = View.GONE
            binding.layoutSenp.lastCurrentIncome.visibility = View.VISIBLE
            binding.layoutSenp.inputAverageMonthlyIncome.visibility = View.VISIBLE
        }
        binding.layoutSenp.llSenp.visibility = View.VISIBLE
        binding.layoutSalary.llSalary.visibility = View.GONE
        ClearEmploymentForm(binding).clearSalaryForm(salarySpinnerList)
    }

    private fun setSalaryDropDown(binding: LayoutSalaryBinding , dropDowns: AllMasterDropDown) {
//        sector = CustomSpinnerView(mContext = context, isMandatory = true,
//                dropDowns = dropDowns.Sector!!, label = "Sector *")
//        binding.layoutSector.addView(sector)
//        salaryIndustry = CustomSpinnerView(mContext = context, isMandatory = true,
//                dropDowns = dropDowns.Industry!!, label = "Industry *")
//        binding.layoutIndustry.addView(salaryIndustry)
//        employmentType = CustomSpinnerView(mContext = context, isMandatory = true,
//                dropDowns = dropDowns.EmploymentType!!, label = "Employment Type *")
//        binding.layoutEmploymentType.addView(employmentType)
//        salarySpinnerList.clear()
//        salarySpinnerList.add(sector)
//        salarySpinnerList.add(salaryIndustry)
//        salarySpinnerList.add(employmentType)
    }

    private fun setSenpDropDown(binding: LayoutSenpBinding , dropDowns: AllMasterDropDown) {
//        constitution = CustomSpinnerView(mContext = context, isMandatory = true,
//                dropDowns = dropDowns.Constitution!!, label = "Constitution *")
//        binding.layoutConstitution.addView(constitution)
//        senpIndustry = CustomSpinnerView(mContext = context, isMandatory = true,
//                dropDowns = dropDowns.Industry!!, label = "Industry *")
//        binding.layoutIndustry.addView(senpIndustry)
//        businessSetUpType = CustomSpinnerView(mContext = context, isMandatory = true,
//                dropDowns = dropDowns.BusinessSetupType!!, label = "Business Setup Type *")
//        binding.layoutBusinessSetupType.addView(businessSetUpType)
//
//        senpSpinnerList.clear()
//        senpSpinnerList.add(constitution)
//        senpSpinnerList.add(senpIndustry)
//        senpSpinnerList.add(businessSetUpType)

    }

    private fun checkIncomeAndSubmission() {
        if (selectedApplicant.incomeConsidered != null && selectedApplicant.incomeConsidered!!.not()) {
//            DisableEmploymentForm(binding, senpSpinnerList, salarySpinnerList, profile, subProfile)
            showToast(context , context.getString(R.string.error_incone_consideration))
        } else if (LeadMetaData.getLeadData()?.status == AppEnums.LEAD_TYPE.SUBMITTED.type) {
//            DisableEmploymentForm(binding, senpSpinnerList, salarySpinnerList, profile, subProfile)
            showToast(context , context.getString(R.string.status_lead_submission))
        }
    }

    private fun fillSenpForm(binding: LayoutSenpBinding , applicant: EmploymentApplicantsModel) {
        val vintageYear = applicant.businessVinatgeInYear ?: 0
        binding.etBusinessVintage.setText(if (vintageYear == 0) "" else vintageYear.toString())
        binding.etIncorporationDate.setText(applicant.dateOfIncorporation ?: "")
        binding.etBusinessName.setText(applicant.companyName ?: "")
        binding.etGstRegistration.setText(applicant.gstRegistration ?: "")

        applicant.incomeDetail?.let {
            binding.etLastYearIncome.setText(applicant.incomeDetail?.itrLastYearIncome.toString())
            binding.etCurrentYearIncome.setText(applicant.incomeDetail?.itrCurrentYearIncome.toString())
            binding.etAverageMonthlyIncome.setText(applicant.incomeDetail?.itrAverageMonthlyIncome.toString())
            binding.etMonthlyIncome.setText(applicant.incomeDetail?.assessedMonthlyIncome.toString())
        }

        applicant.constitutionTypeDetailID?.let {
            constitution.setSelection(applicant.constitutionTypeDetailID.toString())
        }

        applicant.industryTypeDetailID?.let {
            senpIndustry.setSelection(applicant.industryTypeDetailID.toString())
        }

        applicant.businessSetupTypeDetailID?.let {
            businessSetUpType.setSelection(applicant.businessSetupTypeDetailID.toString())
        }

        applicant.allEarningMembers?.let { binding.cbAllEarningMember.isChecked = it }
        fillAddress(binding.layoutAddress , applicant.addressBean)
    }

    private fun fillSalaryForm(binding: LayoutSalaryBinding , applicant: EmploymentApplicantsModel) {
        applicant.dateOfJoining?.let {
            binding.etJoiningDate.setText(applicant.dateOfJoining)
        }
        applicant.totalExperience?.let {
            binding.etTotalExperience.setText(applicant.totalExperience)
        }
        applicant.retirementAge?.let {
            binding.etRetirementAge.setText(applicant.retirementAge.toString())
        }
        applicant.officialMailID?.let {
            binding.etOfficialMailId.setText(applicant.officialMailID)
        }
        applicant.designation?.let {
            binding.etDesignation.setText(applicant.designation)
        }
        applicant.employeeID?.let {
            binding.etEmployeeId.setText(applicant.employeeID)
        }
        applicant.companyName?.let {
            binding.etCompanyName.setText(applicant.companyName)
        }
        applicant.sectorTypeDetailID?.let {
            sector.setSelection(applicant.sectorTypeDetailID.toString())
        }
        applicant.industryTypeDetailID?.let {
            salaryIndustry.setSelection(applicant.industryTypeDetailID.toString())
        }
        applicant.employmentTypeDetailID?.let {
            employmentType.setSelection(applicant.employmentTypeDetailID.toString())
        }

        applicant.incomeDetail?.let {
            binding.etGrossIncome.setText(applicant.incomeDetail?.salariedGrossIncome.toString())
            binding.etDeduction.setText(applicant.incomeDetail?.salariedDeduction.toString())
            binding.etNetIncome.setText(applicant.incomeDetail?.salariedNetIncome.toString())
        }

        applicant.isPensioner?.let { binding.cbIsPensioner.isChecked = it }
        fillAddress(binding.layoutAddress , applicant.addressBean)
    }

    private fun fillAddress(binding: LayoutEmploymentAddressBinding , address: AddressDetail?) {
        address?.let {
            binding.etAddress.setText(address.address1)
            binding.etLandmark.setText(address.landmark)
            binding.etContactNum.setText(address.contactNum)
//            binding.customZipAddressView.updateAddressData(addressDetail = address)
        }
    }

    private fun getCurrentApplicant(): EmploymentApplicantsModel? {
        var applicant: EmploymentApplicantsModel? = null
        if (selectedApplicant.incomeConsidered != null && selectedApplicant.incomeConsidered!!) {
            //assign new values to employee model...
            applicant = EmploymentApplicantsModel()

            applicant.leadApplicantNumber = selectedApplicant.leadApplicantNumber
            applicant.incomeConsidered = selectedApplicant.incomeConsidered

            val profileDD = profile.getSelectedValue()
            val subProfileDD = subProfile.getSelectedValue()
            applicant.profileSegmentTypeDetailID = profileDD?.typeDetailID
            applicant.subProfileTypeDetailID = subProfileDD?.typeDetailID
            applicant.isMainApplicant = selectedApplicant.isMainApplicant
            when (formType) {
                SALARY -> return getSalaryForm(binding.layoutSalary , applicant)
                SENP -> return getSenpForm(binding.layoutSenp , applicant)
            }
        }

        return applicant
    }

    private fun getSalaryForm(binding: LayoutSalaryBinding , applicant: EmploymentApplicantsModel): EmploymentApplicantsModel {

        val sectorDD = sector.getSelectedValue()
        val industryDD = salaryIndustry.getSelectedValue()
        val employmentDD = employmentType.getSelectedValue()
        applicant.companyName = binding.etCompanyName.text.toString()
        applicant.sectorTypeDetailID = sectorDD?.typeDetailID
        applicant.industryTypeDetailID = industryDD?.typeDetailID
        applicant.employmentTypeDetailID = employmentDD?.typeDetailID
        applicant.designation = binding.etDesignation.text.toString()
        applicant.isPensioner = binding.cbIsPensioner.isChecked
        applicant.dateOfJoining = binding.etJoiningDate.text.toString()
        applicant.totalExperience = binding.etTotalExperience.text.toString()
        applicant.retirementAge = if (binding.etRetirementAge.text.toString() == "") 0
        else binding.etRetirementAge.text.toString().toInt()

        applicant.officialMailID = binding.etOfficialMailId.text.toString()
        applicant.addressBean = getAddress(binding.layoutAddress)
        applicant.incomeDetail = getSalaryIncomeDetail(binding)
        applicant.employeeID = binding.etEmployeeId.text.toString()
        return applicant
    }

    private fun getSenpForm(binding: LayoutSenpBinding , applicant: EmploymentApplicantsModel): EmploymentApplicantsModel {
        val constitutionDD = constitution.getSelectedValue()
        val industryDD = senpIndustry.getSelectedValue()
        val businessSetupTypeDD = businessSetUpType.getSelectedValue()

        applicant.companyName = binding.etBusinessName.text.toString()
        applicant.constitutionTypeDetailID = constitutionDD?.typeDetailID
        applicant.businessSetupTypeDetailID = businessSetupTypeDD?.typeDetailID
        applicant.industryTypeDetailID = industryDD?.typeDetailID
        applicant.dateOfIncorporation = binding.etIncorporationDate.text.toString()
        applicant.gstRegistration = binding.etGstRegistration.text.toString()
        applicant.businessVinatgeInYear = binding.etBusinessVintage.text.toString().toInt()
        applicant.addressBean = getAddress(binding.layoutAddress)
        applicant.allEarningMembers = binding.cbAllEarningMember.isChecked
        applicant.incomeDetail = getSenpIncomeDetail(binding)
        return applicant
    }

    private fun getSalaryIncomeDetail(binding: LayoutSalaryBinding): IncomeDetail {
        val incomeDetail = IncomeDetail()
        incomeDetail.salariedGrossIncome = CurrencyConversion().convertToNormalValue(binding.etGrossIncome.text.toString()).toFloat()
        incomeDetail.salariedDeduction = CurrencyConversion().convertToNormalValue(binding.etDeduction.text.toString()).toFloat()
        incomeDetail.salariedNetIncome = CurrencyConversion().convertToNormalValue(binding.etNetIncome.text.toString()).toFloat()
        return incomeDetail
    }

    private fun getSenpIncomeDetail(binding: LayoutSenpBinding): IncomeDetail {
        val incomeDetail = IncomeDetail()
        incomeDetail.assessedMonthlyIncome = CurrencyConversion().convertToNormalValue(binding.etMonthlyIncome.text.toString()).toFloatOrNull()
        incomeDetail.itrAverageMonthlyIncome = CurrencyConversion().convertToNormalValue(binding.etAverageMonthlyIncome.text.toString()).toFloatOrNull()
        incomeDetail.itrLastYearIncome = CurrencyConversion().convertToNormalValue(binding.etLastYearIncome.text.toString()).toFloatOrNull()
        incomeDetail.itrCurrentYearIncome = CurrencyConversion().convertToNormalValue(binding.etCurrentYearIncome.text.toString()).toFloatOrNull()
        return incomeDetail
    }

    private fun getAddress(binding: LayoutEmploymentAddressBinding): AddressDetail {
        val address = AddressDetail()
        address.zip = binding.customZipAddressView.pinCode
        address.addressTypeDetail = Constants.CURRENT_ADDRESS
        address.stateID = binding.customZipAddressView.getStateId()
        address.districtID = binding.customZipAddressView.getDistrictId()
        address.cityID = binding.customZipAddressView.getCityId()
        address.address1 = binding.etAddress.text.toString()
        address.landmark = binding.etLandmark.text.toString()
        address.contactNum = binding.etContactNum.text.toString()
        return address
    }

    private fun validateSalary(): Boolean {
        if (formValidation.validateSalaryEmployment(binding.layoutSalary , salarySpinnerList)) {

            ClearEmploymentForm(binding).clearSenpForm(senpSpinnerList)
            return true

        } else showToast(mContext , mContext.getString(R.string.validation_error))
        return false
    }

    private fun validateSenp(): Boolean {
        if (formValidation.validateSenpEmployment(binding.layoutSenp , senpSpinnerList)) {

            ClearEmploymentForm(binding).clearSalaryForm(salarySpinnerList)

            return true

        } else showToast(mContext , mContext.getString(R.string.validation_error))
        return false
    }

//    fun isIncomeConsidered() = selectedApplicant.incomeConsidered

    fun isValidEmploymentDetails(): Boolean {
        return when (formType) {
            SENP -> validateSenp()
            SALARY -> validateSalary()
            else -> false
        }
    }

    fun getEmploymentDetails() = getCurrentApplicant()


}
