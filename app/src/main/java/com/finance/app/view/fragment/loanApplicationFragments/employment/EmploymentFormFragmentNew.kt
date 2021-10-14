package com.finance.app.view.fragment.loanApplicationFragments.employment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.databinding.FragmentEmploymentFormBinding
import com.finance.app.databinding.LayoutEmploymentAddressBinding
import com.finance.app.databinding.LayoutSalaryBinding
import com.finance.app.databinding.LayoutSenpBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.*
import com.finance.app.utility.*
import com.finance.app.view.activity.DocumentUploadingActivity
import com.finance.app.view.adapters.recycler.spinner.MasterSpinnerAdapter
import com.finance.app.view.utils.EditTexNormal
import com.finance.app.view.utils.setSelectionFromList
import kotlinx.android.synthetic.main.fragment_employment_form.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.Constants.APP.KEY_APPLICANT
import motobeans.architecture.customAppComponents.activity.BaseFragment
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.FormValidation
import javax.inject.Inject

class EmploymentFormFragmentNew : BaseFragment() {
    enum class FORM_TYPE {
        SALARY_DETAIL , BUSINESS_DETAIL , NONE
    }

    private lateinit var mContext: Context

    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var formValidation: FormValidation

    private var allMasterDropDown: AllMasterDropDown? = null
    private var selectedApplicant: PersonalApplicantsModel? = null
    private var selectedFormType: FORM_TYPE? = null

    private lateinit var binding: FragmentEmploymentFormBinding

    companion object {

        fun newInstance(selectedApplicant: PersonalApplicantsModel): EmploymentFormFragmentNew {
            val fragment = EmploymentFormFragmentNew()
            val args = Bundle()
            args.putSerializable(KEY_APPLICANT , selectedApplicant)
            fragment.arguments = args
            return fragment
        }
    }

    override fun init() {
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!

        ArchitectureApp.instance.component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater , container , R.layout.fragment_employment_form)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        initViews()
        setOnClickListener()
        setOnTextChangeListener()
        setOnTextConversionListener()

        //Now fetch arguments...
        fetchSelectedApplicant()
        fetchSpinnersDataFromDB()

        //Show empty view if this applicant details not required...
        shouldShowEmptyView()
        //Call Disable Functionality EmploymentFormFragment
        LeadMetaData.getLeadData()?.let {
            if (it.status.equals(AppEnums.LEAD_TYPE.SUBMITTED.type , true))
                DisableEmploymentForm(binding , mContext)
        }
    }

    private fun initViews() {
//        binding.vwIncomeConsider.visibility = View.VISIBLE

        //Set Mandatory fields...
        SetEmploymentMandatoryField(binding)
        bindDatePickerToViews()

    }

    private fun setOnClickListener() {
        binding.btnUploadEmployment.setOnClickListener {
            allMasterDropDown?.let { dropDown ->
                val docCodeID = dropDown.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.EMPLOYMENT_PROOF , true) }
                val bundle = Bundle()
                bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)//Hardcoded for Address proof...
                bundle.putString(Constants.KEY_TITLE , getString(R.string.employment))
                bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicant?.leadApplicantNumber)
                context?.let { c -> DocumentUploadingActivity.startActivity(c , bundle) }
            }
        }
    }

    private fun setOnTextChangeListener() {
        setSalaryIncomeTextChangeListener(binding.lytSalaryDetail.etGrossIncome)
        setSalaryIncomeTextChangeListener(binding.lytSalaryDetail.etDeduction)
        setBusinessIncomeTextChangeListener(binding.lytBusinessDetail.etLastYearIncome)
        setBusinessIncomeTextChangeListener(binding.lytBusinessDetail.etCurrentYearIncome)
    }

    private fun setOnTextConversionListener() {
        CurrencyConversion().convertToCurrencyType(binding.lytSalaryDetail.etGrossIncome)
        CurrencyConversion().convertToCurrencyType(binding.lytSalaryDetail.etNetIncome)
        CurrencyConversion().convertToCurrencyType(binding.lytSalaryDetail.etDeduction)
        CurrencyConversion().convertToCurrencyType(binding.lytBusinessDetail.etLastYearIncome)
        CurrencyConversion().convertToCurrencyType(binding.lytBusinessDetail.etCurrentYearIncome)
        CurrencyConversion().convertToCurrencyType(binding.lytBusinessDetail.etAverageMonthlyIncome)
        CurrencyConversion().convertToCurrencyType(binding.lytBusinessDetail.etMonthlyIncome)
    }

    private fun setSalaryIncomeTextChangeListener(amountField: EditTexNormal?) {
        amountField?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (binding.lytSalaryDetail.etGrossIncome.text.toString().isBlank() || binding.lytSalaryDetail.etDeduction.text.toString().isBlank())
                    return //Return if string is empty

                val grossIncome = CurrencyConversion().convertToNormalValue(binding.lytSalaryDetail.etGrossIncome.text.toString()).toFloat()
                val deduction = CurrencyConversion().convertToNormalValue(binding.lytSalaryDetail.etDeduction.text.toString()).toFloat()

                if (grossIncome > deduction) {
                    val netIncome = (grossIncome - deduction).toInt().toString()
                    binding.lytSalaryDetail.etNetIncome.setText(netIncome)
                } else {
                    binding.lytSalaryDetail.etDeduction.text?.clear()
                    binding.lytSalaryDetail.etNetIncome.text?.clear()
                }
            }

            override fun beforeTextChanged(s: CharSequence , start: Int , count: Int , after: Int) {}
            override fun onTextChanged(s: CharSequence , start: Int , before: Int , count: Int) {
            }
        })
    }

    private fun setBusinessIncomeTextChangeListener(amountField: EditTexNormal?) {
        amountField?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (binding.lytBusinessDetail.etLastYearIncome.text.toString().isBlank() || binding.lytBusinessDetail.etCurrentYearIncome.text.toString().isBlank()) return //Return if string is empty

                val lastYearIncome = CurrencyConversion().convertToNormalValue(binding.lytBusinessDetail.etLastYearIncome.text.toString()).toFloat()
                val currentYearIncome = CurrencyConversion().convertToNormalValue(binding.lytBusinessDetail.etCurrentYearIncome.text.toString()).toFloat()
                val averageMonthlyIncome = ((lastYearIncome + currentYearIncome) / 2).toInt().toString()

                binding.lytBusinessDetail.etAverageMonthlyIncome.setText(averageMonthlyIncome)
            }

            override fun beforeTextChanged(s: CharSequence , start: Int , count: Int , after: Int) {}
            override fun onTextChanged(s: CharSequence , start: Int , before: Int , count: Int) {
            }
        })
    }

    private fun shouldShowEmptyView() {
        selectedApplicant?.let {
            if (it.incomeConsidered != null && it.incomeConsidered!!) {
                binding.vwIncomeConsider.visibility = View.VISIBLE
                binding.vwIncomeNotConsider.visibility = View.GONE
            } else {
                binding.vwIncomeConsider.visibility = View.GONE
                binding.vwIncomeNotConsider.visibility = View.VISIBLE
            }
        } ?: run {
            binding.vwIncomeConsider.visibility = View.GONE
            binding.vwIncomeNotConsider.visibility = View.VISIBLE
        }
    }

    private fun bindDatePickerToViews() {
        binding.lytSalaryDetail.etJoiningDate.setOnClickListener { SelectDate(binding.lytSalaryDetail.etJoiningDate , mContext) }
        binding.lytBusinessDetail.etIncorporationDate.setOnClickListener { DateDifference(mContext , binding.lytBusinessDetail.etIncorporationDate , binding.lytBusinessDetail.etBusinessVintage) }
    }

    private fun fetchSelectedApplicant() {
        arguments?.getSerializable(KEY_APPLICANT)?.let { applicantDetails ->
            selectedApplicant = applicantDetails as PersonalApplicantsModel
        }
    }

    private fun fetchSpinnersDataFromDB() {
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(viewLifecycleOwner , Observer { masterDrownDownValues ->
            masterDrownDownValues?.let {
                allMasterDropDown = it
                //Assigning all spinners from here.. for sake of simplicity and symmetric....
                bindProfileSelectionSpinnerData()
                bindBusinessSpinnersData()
                bindSalarySpinnersData()
                bindCustomAddressSpinnersData()
                //Now fetch the applicant employment data from db, if available...
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    selectedApplicant?.let { applicant -> fetchApplicantEmploymentDetails(applicant) }
                } , 1000)

            }
        })
    }

    private fun fetchApplicantEmploymentDetails(applicant: PersonalApplicantsModel) {
        LeadMetaData.getLeadObservable().observe(this , Observer {
            it?.let { leadDetails ->
                val employmentList = leadDetails.employmentData.applicantDetails.filter { employmentDetail -> employmentDetail.leadApplicantNumber.equals(applicant.leadApplicantNumber , true) }
                if (employmentList.isNotEmpty()) fillEmploymentDetails(employmentList[0])
            }
        })
    }

    private fun bindProfileSelectionSpinnerData() {

        binding.spinnerProfile.adapter = MasterSpinnerAdapter(mContext , allMasterDropDown?.ProfileSegment)
        binding.spinnerSubProfile.adapter = MasterSpinnerAdapter(mContext , ArrayList())

        binding.spinnerProfile.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>? , view: View? , position: Int , id: Long) {
                val profileSelected = parent?.selectedItem as DropdownMaster?
                profileSelected?.let { profile ->
                    val subProfileSegment: ArrayList<DropdownMaster> = ArrayList()

                    allMasterDropDown?.SubProfileSegment?.forEach { subProfile -> if (subProfile.refTypeDetailID == profile.typeDetailID) subProfileSegment.add(subProfile) }
                    binding.spinnerSubProfile.adapter = MasterSpinnerAdapter(mContext , subProfileSegment)
                    //If previously sub profile value selected....
                    binding.spinnerProfile.tag?.let {
                        binding.spinnerSubProfile.setSelectionFromList(subProfileSegment , binding.spinnerProfile.tag as Int)
                    }
                }
                //Set Empty is profile is not yet selected..
                if (profileSelected == null) binding.spinnerSubProfile.adapter = MasterSpinnerAdapter(mContext , ArrayList())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.spinnerSubProfile.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>? , view: View? , position: Int , id: Long) {
                val subProfileSelected = parent?.selectedItem as DropdownMaster?
                subProfileSelected?.let { subProfile ->

                    if (subProfile.typeDetailID == Constants.CASH_SALARY || subProfile.typeDetailID == Constants.BANK_SALARY) {
                        selectedFormType = FORM_TYPE.SALARY_DETAIL
                        switchEmploymentTypeView(FORM_TYPE.SALARY_DETAIL , subProfile.typeDetailID)

                    } else if (subProfile.typeDetailID == Constants.ITR || subProfile.typeDetailID == Constants.ASSESED_INCOME) {
                        selectedFormType = FORM_TYPE.BUSINESS_DETAIL
                        switchEmploymentTypeView(FORM_TYPE.BUSINESS_DETAIL , subProfile.typeDetailID)

                    } else {
                        selectedFormType = FORM_TYPE.NONE
                        switchEmploymentTypeView(FORM_TYPE.NONE , subProfile.typeDetailID)
                    }

                } ?: run {
                    selectedFormType = FORM_TYPE.NONE
                    switchEmploymentTypeView(FORM_TYPE.NONE , -1)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun bindBusinessSpinnersData() {
        binding.lytBusinessDetail.spinnerConstitution.adapter = MasterSpinnerAdapter(mContext , allMasterDropDown?.Constitution)
        binding.lytBusinessDetail.spinnerIndustry.adapter = MasterSpinnerAdapter(mContext , allMasterDropDown?.Industry)
        binding.lytBusinessDetail.spinnerBusinessSetupType.adapter = MasterSpinnerAdapter(mContext , allMasterDropDown?.BusinessSetupType)
    }

    private fun bindSalarySpinnersData() {
        binding.lytSalaryDetail.spinnerSector.adapter = MasterSpinnerAdapter(mContext , allMasterDropDown?.Sector)
        binding.lytSalaryDetail.spinnerSector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>? , view: View? , position: Int , id: Long) {
                //Check here

               val sectorSelected = parent?.selectedItem as DropdownMaster?
                sectorSelected.let { sector ->
                    if(sector?.typeDetailID == Constants.SECTOR_GOVERMENT)
                    {
                        binding.lytSalaryDetail.spinnerIndustry.visibility = View.GONE
                        binding.lytSalaryDetail.spinnerIndustry.adapter = MasterSpinnerAdapter(mContext , allMasterDropDown?.Industry)
                    }
                    else
                    {
                        binding.lytSalaryDetail.spinnerIndustry.visibility = View.VISIBLE

                    }
                }
            }

        }
        binding.lytSalaryDetail.spinnerIndustry.adapter = MasterSpinnerAdapter(mContext , allMasterDropDown?.Industry)
        binding.lytSalaryDetail.spinnerEmploymentType.adapter = MasterSpinnerAdapter(mContext , allMasterDropDown?.EmploymentType)
    }

    private fun bindCustomAddressSpinnersData() {
        activity?.let {
            binding.lytSalaryDetail.layoutAddress.customZipAddressView.attachActivity(it)
            binding.lytBusinessDetail.layoutAddress.customZipAddressView.attachActivity(it)
        }
    }

    private fun switchEmploymentTypeView(formType: FORM_TYPE , incomeType: Int) {
        when (formType) {
            FORM_TYPE.SALARY_DETAIL -> {
                lytSalaryDetail.visibility = View.VISIBLE
                lytBusinessDetail.visibility = View.GONE
                when (incomeType) {
                    Constants.BANK_SALARY -> binding.lytSalaryDetail.cbIsPensioner.visibility = View.VISIBLE
                    else -> binding.lytSalaryDetail.cbIsPensioner.visibility = View.GONE
                }
            }

            FORM_TYPE.BUSINESS_DETAIL -> {
                lytSalaryDetail.visibility = View.GONE
                lytBusinessDetail.visibility = View.VISIBLE
                when (incomeType) {
                    Constants.ASSESED_INCOME -> {
                        binding.lytBusinessDetail.inputMonthlyIncome.visibility = View.VISIBLE
                        binding.lytBusinessDetail.lytYearlyIncome.visibility = View.GONE
                    }
                    Constants.ITR -> {
                        binding.lytBusinessDetail.lytYearlyIncome.visibility = View.VISIBLE
                        binding.lytBusinessDetail.inputMonthlyIncome.visibility = View.GONE
                    }
                }
            }

            FORM_TYPE.NONE -> {
                lytSalaryDetail.visibility = View.GONE
                lytBusinessDetail.visibility = View.GONE
            }
        }
    }

    private fun fillEmploymentDetails(employmentDetails: EmploymentApplicantsModel) {
        employmentDetails.profileSegmentTypeDetailID?.let { profileId ->
            allMasterDropDown?.ProfileSegment?.let { list ->
                binding.spinnerProfile.setSelectionFromList(list , profileId)
                employmentDetails.subProfileTypeDetailID?.let { subProfileId -> binding.spinnerProfile.tag = subProfileId }
            }
        }

        if (employmentDetails.subProfileTypeDetailID == Constants.CASH_SALARY || employmentDetails.subProfileTypeDetailID == Constants.BANK_SALARY) {
            fillSalaryDetails(employmentDetails)
        } else if (employmentDetails.subProfileTypeDetailID == Constants.ITR || employmentDetails.subProfileTypeDetailID == Constants.ASSESED_INCOME) {
            fillBusinessDetails(employmentDetails)
        }

    }

    private fun fillSalaryDetails(employmentDetails: EmploymentApplicantsModel) {
        employmentDetails.isPensioner?.let { binding.lytSalaryDetail.cbIsPensioner.isChecked = it }


        employmentDetails.dateOfJoining?.let { binding.lytSalaryDetail.etJoiningDate.setText(ConvertDate().convertToAppFormat(it.toString())) }
        employmentDetails.totalExperience?.let { binding.lytSalaryDetail.etTotalExperience.setText(it.toString()) }
        employmentDetails.retirementAge?.let { binding.lytSalaryDetail.etRetirementAge.setText(it.toString()) }
        employmentDetails.officialMailID?.let { binding.lytSalaryDetail.etOfficialMailId.setText(it.toString()) }
        employmentDetails.designation?.let { binding.lytSalaryDetail.etDesignation.setText(it.toString()) }
        employmentDetails.employeeID?.let { binding.lytSalaryDetail.etEmployeeId.setText(it.toString()) }
        employmentDetails.companyName?.let { binding.lytSalaryDetail.etCompanyName.setText(it.toString()) }

        employmentDetails.sectorTypeDetailID?.let { id -> allMasterDropDown?.Sector?.let { list -> binding.lytSalaryDetail.spinnerSector.setSelectionFromList(list , id) } }
        employmentDetails.industryTypeDetailID?.let { id -> allMasterDropDown?.Industry?.let { list -> binding.lytSalaryDetail.spinnerIndustry.setSelectionFromList(list , id) } }
        employmentDetails.employmentTypeDetailID?.let { id -> allMasterDropDown?.EmploymentType?.let { list -> binding.lytSalaryDetail.spinnerEmploymentType.setSelectionFromList(list , id) } }
        employmentDetails.employerContactNumber?.let { binding.lytSalaryDetail.layoutAddress.etContactNum.setText(it.toString()) }

        employmentDetails.addressBean?.let { fillAddressDetails(binding.lytSalaryDetail.layoutAddress , it) }

        employmentDetails.incomeDetail?.let { incomeDetail ->
            incomeDetail.salariedGrossIncome?.let { binding.lytSalaryDetail.etGrossIncome.setText(it.toInt().toString()) }
            incomeDetail.salariedDeduction?.let { binding.lytSalaryDetail.etDeduction.setText(it.toInt().toString()) }
            incomeDetail.salariedNetIncome?.let { binding.lytSalaryDetail.etNetIncome.setText(it.toInt().toString()) }
        }
    }

    private fun fillBusinessDetails(employmentDetails: EmploymentApplicantsModel) {
        employmentDetails.allEarningMembers?.let { binding.lytBusinessDetail.cbAllEarningMember.isChecked = it }
        employmentDetails.businessVinatgeInYear?.let { binding.lytBusinessDetail.etBusinessVintage.setText(it.toString()) }
        employmentDetails.dateOfIncorporation?.let { binding.lytBusinessDetail.etIncorporationDate.setText(ConvertDate().convertToAppFormat(it.toString())) }
        employmentDetails.companyName?.let { binding.lytBusinessDetail.etBusinessName.setText(it.toString()) }
        employmentDetails.gstRegistration?.let { binding.lytBusinessDetail.etGstRegistration.setText(it.toString()) }

        employmentDetails.constitutionTypeDetailID?.let { id -> allMasterDropDown?.Constitution?.let { list -> binding.lytBusinessDetail.spinnerConstitution.setSelectionFromList(list , id) } }
        employmentDetails.industryTypeDetailID?.let { id -> allMasterDropDown?.Industry?.let { list -> binding.lytBusinessDetail.spinnerIndustry.setSelectionFromList(list , id) } }
        employmentDetails.businessSetupTypeDetailID?.let { id -> allMasterDropDown?.BusinessSetupType?.let { list -> binding.lytBusinessDetail.spinnerBusinessSetupType.setSelectionFromList(list , id) } }
        employmentDetails.employerContactNumber?.let { binding.lytBusinessDetail.layoutAddress.etContactNum.setText(it.toString()) }

        employmentDetails.addressBean?.let { fillAddressDetails(binding.lytBusinessDetail.layoutAddress , it) }

        employmentDetails.incomeDetail?.let { incomeDetail ->
            incomeDetail.itrLastYearIncome?.let { binding.lytBusinessDetail.etLastYearIncome.setText(it.toInt().toString()) }
            incomeDetail.itrCurrentYearIncome?.let { binding.lytBusinessDetail.etCurrentYearIncome.setText(it.toInt().toString()) }
            incomeDetail.itrAverageMonthlyIncome?.let { binding.lytBusinessDetail.etAverageMonthlyIncome.setText(it.toInt().toString()) }
            incomeDetail.assessedMonthlyIncome?.let { binding.lytBusinessDetail.etMonthlyIncome.setText(it.toInt().toString()) }
        }
    }

    private fun fillAddressDetails(binding: LayoutEmploymentAddressBinding , address: AddressDetail) {
        binding.etAddress.setText(address.address1)
        binding.etLandmark.setText(address.landmark)
//        binding.customZipAddressView.etCurrentPinCode.setText(address.zip.toString()) //will fetch details automatically once zip is available
        binding.customZipAddressView.updateAddressData(addressDetail = address)
    }

    fun isEmploymentDetailsValid(): Boolean {
        // need to correct validation....
        selectedApplicant?.let {
            it.incomeConsidered?.let { incomeConsider ->
                if (incomeConsider.not())
                    return true //No need to validate in case of while income is not considerd..
            }
        }

        selectedFormType?.let {
            return when (it) {
                FORM_TYPE.SALARY_DETAIL -> formValidation.validateEmploymentSalary(binding.lytSalaryDetail)
                FORM_TYPE.BUSINESS_DETAIL -> formValidation.validateEmploymentBusiness(binding.lytBusinessDetail)
                FORM_TYPE.NONE -> false
            }
        }
        return false
    }

    fun getApplicantEmploymentDetails(): EmploymentApplicantsModel {
        val employmentDetails = EmploymentApplicantsModel()
        selectedApplicant?.let { applicant ->
            //Basic Details...
            employmentDetails.leadApplicantNumber = applicant.leadApplicantNumber
            employmentDetails.isMainApplicant = applicant.isMainApplicant
            employmentDetails.incomeConsidered = applicant.incomeConsidered

            //Employment Details....
            employmentDetails.profileSegmentTypeDetailID = (binding.spinnerProfile.selectedItem as DropdownMaster?)?.typeDetailID
            employmentDetails.subProfileTypeDetailID = (binding.spinnerSubProfile.selectedItem as DropdownMaster?)?.typeDetailID

            //Selected Form Details..
            selectedFormType?.let {
                when (it) {
                    FORM_TYPE.SALARY_DETAIL -> applySalaryDetails(employmentDetails)
                    FORM_TYPE.BUSINESS_DETAIL -> applyBusinessDetails(employmentDetails)
                    FORM_TYPE.NONE -> {
                    }
                }
            }
        }

        return employmentDetails
    }

    private fun applySalaryDetails(employmentDetails: EmploymentApplicantsModel): EmploymentApplicantsModel {
        employmentDetails.companyName = binding.lytSalaryDetail.etCompanyName.text.toString()
        employmentDetails.sectorTypeDetailID = (binding.lytSalaryDetail.spinnerSector.selectedItem as DropdownMaster?)?.typeDetailID
        employmentDetails.industryTypeDetailID = (binding.lytSalaryDetail.spinnerIndustry.selectedItem as DropdownMaster?)?.typeDetailID
        employmentDetails.employmentTypeDetailID = (binding.lytSalaryDetail.spinnerEmploymentType.selectedItem as DropdownMaster?)?.typeDetailID

        employmentDetails.designation = binding.lytSalaryDetail.etDesignation.text.toString()
        employmentDetails.isPensioner = binding.lytSalaryDetail.cbIsPensioner.isChecked
        employmentDetails.dateOfJoining = ConvertDate().convertToApiFormat(binding.lytSalaryDetail.etJoiningDate.text.toString())
        employmentDetails.totalExperience = binding.lytSalaryDetail.etTotalExperience.text.toString()
        employmentDetails.retirementAge = if (binding.lytSalaryDetail.etRetirementAge.text.toString().isEmpty()) 0 else binding.lytSalaryDetail.etRetirementAge.text.toString().toInt()

        employmentDetails.officialMailID = binding.lytSalaryDetail.etOfficialMailId.text.toString()
        employmentDetails.addressBean = getAddressDetails(binding.lytSalaryDetail.layoutAddress)
        employmentDetails.incomeDetail = getSalaryIncomeDetail(binding.lytSalaryDetail)
        employmentDetails.employerContactNumber = binding.lytSalaryDetail.layoutAddress.etContactNum.text.toString()
        employmentDetails.employeeID = binding.lytSalaryDetail.etEmployeeId.text.toString()
        return employmentDetails
    }

    private fun applyBusinessDetails(employmentDetails: EmploymentApplicantsModel): EmploymentApplicantsModel {
        employmentDetails.companyName = binding.lytBusinessDetail.etBusinessName.text.toString()
        employmentDetails.constitutionTypeDetailID = (binding.lytBusinessDetail.spinnerConstitution.selectedItem as DropdownMaster?)?.typeDetailID
        employmentDetails.businessSetupTypeDetailID = (binding.lytBusinessDetail.spinnerBusinessSetupType.selectedItem as DropdownMaster?)?.typeDetailID
        employmentDetails.industryTypeDetailID = (binding.lytBusinessDetail.spinnerIndustry.selectedItem as DropdownMaster?)?.typeDetailID
        employmentDetails.dateOfIncorporation = ConvertDate().convertToApiFormat(binding.lytBusinessDetail.etIncorporationDate.text.toString())
        employmentDetails.gstRegistration = binding.lytBusinessDetail.etGstRegistration.text.toString()
        employmentDetails.businessVinatgeInYear = binding.lytBusinessDetail.etBusinessVintage.text.toString().toInt()
        employmentDetails.addressBean = getAddressDetails(binding.lytBusinessDetail.layoutAddress)
        employmentDetails.employerContactNumber = binding.lytBusinessDetail.layoutAddress.etContactNum.text.toString()
        employmentDetails.allEarningMembers = binding.lytBusinessDetail.cbAllEarningMember.isChecked
        employmentDetails.incomeDetail = getBusinessIncomeDetail(binding.lytBusinessDetail)
        return employmentDetails
    }

    private fun getAddressDetails(binding: LayoutEmploymentAddressBinding): AddressDetail {
        val address = AddressDetail()
        address.zip = binding.customZipAddressView.pinCode
        address.addressTypeDetailID = 120
        address.addressTypeDetail = "EmploymentAddress"
        address.stateID = binding.customZipAddressView.getStateId()
        address.stateName = binding.customZipAddressView.getStateName()
        address.districtID = binding.customZipAddressView.getDistrictId()
        address.districtName = binding.customZipAddressView.getDistrictName()
        address.cityID = binding.customZipAddressView.getCityId()
        address.cityName = binding.customZipAddressView.getCityName()
        address.address1 = binding.etAddress.text.toString()
        address.landmark = binding.etLandmark.text.toString()
        return address
    }

    private fun getSalaryIncomeDetail(binding: LayoutSalaryBinding): IncomeDetail {
        val incomeDetail = IncomeDetail()
        incomeDetail.salariedGrossIncome = CurrencyConversion().convertToNormalValue(binding.etGrossIncome.text.toString()).toFloatOrNull()
        incomeDetail.salariedDeduction = CurrencyConversion().convertToNormalValue(binding.etDeduction.text.toString()).toFloatOrNull()
        incomeDetail.salariedNetIncome = CurrencyConversion().convertToNormalValue(binding.etNetIncome.text.toString()).toFloatOrNull()
        return incomeDetail
    }

    private fun getBusinessIncomeDetail(binding: LayoutSenpBinding): IncomeDetail {
        val incomeDetail = IncomeDetail()
        incomeDetail.assessedMonthlyIncome = CurrencyConversion().convertToNormalValue(binding.etMonthlyIncome.text.toString()).toFloatOrNull()
        incomeDetail.itrAverageMonthlyIncome = CurrencyConversion().convertToNormalValue(binding.etAverageMonthlyIncome.text.toString()).toFloatOrNull()
        incomeDetail.itrLastYearIncome = CurrencyConversion().convertToNormalValue(binding.etLastYearIncome.text.toString()).toFloatOrNull()
        incomeDetail.itrCurrentYearIncome = CurrencyConversion().convertToNormalValue(binding.etCurrentYearIncome.text.toString()).toFloatOrNull()
        return incomeDetail
    }
}
