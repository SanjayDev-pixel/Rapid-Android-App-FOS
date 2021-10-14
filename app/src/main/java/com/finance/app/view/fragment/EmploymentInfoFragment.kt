package com.finance.app.view.fragment

class EmploymentInfoFragment {}
/*
class EmploymentInfoFragment : BaseFragment(),
        PinCodeDetailConnector.PinCode,
        ApplicantsAdapter.ItemClickListener, DistrictCityConnector.District,
        DistrictCityConnector.City {

    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    @Inject
    lateinit var formValidation: FormValidation
    @Inject
    lateinit var dataBase: DataBaseUtil
    private lateinit var binding: FragmentEmploymentBinding
    private lateinit var mContext: Context
    private var mLead: AllLeadMaster? = null
    private lateinit var allMasterDropDown: AllMasterDropDown
    private val pinCodePresenter = PinCodeDetailPresenter(this)
    private val districtPresenter = DistrictPresenter(this)
    private val presenter = Presenter()
    private val cityPresenter = CityPresenter(this)
    private var applicantAdapter: ApplicantsAdapter? = null
    private var applicantTab: ArrayList<CoApplicantsList>? = ArrayList()
    private var employmentMaster: EmploymentMaster = EmploymentMaster()
    private var eDraftData = EmploymentApplicantList()
    private var eApplicantList: ArrayList<EmploymentApplicantsModel>? = ArrayList()
    private var currentApplicant: EmploymentApplicantsModel = EmploymentApplicantsModel()
    private var currentTab = CoApplicantsList()
    private var eAddressDetail: AddressDetail = AddressDetail()
    private lateinit var leadIdForApplicant: String
    private var pinCodeObj: Response.PinCodeObj? = null
    private var mPinCode: String = ""
    private var grossIncome: Float = 0.0f
    private var deduction: Float = 0.0f
    private var netIncome: String = ""
    private var lastYearIncome: Float = 0.0f
    private var currentYearIncome: Float = 0.0f
    private var averageMonthlyIncome: String = ""
    private var currentPosition = 0
    private var mStateId: String = ""
    private var mDistrictId: String = ""
    private var formType: Int = -1

    companion object {
        private val leadAndLoanDetail = LeadAndLoanDetail()
        private lateinit var states: List<StatesMaster>
       }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater, container, R.layout.layout_custom_employment_view)
        init()
        return binding.root
    }

    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        mLead = sharedPreferences.getLeadDetail()
        mContext = context!!
        leadIdForApplicant = mLead!!.leadID.toString()
        SetEmploymentMandatoryField(binding)
        getEmploymentInfo()
        setDatePicker()
        setClickListeners()
    }

    private fun getEmploymentInfo() {
        presenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP, CallGetLoan())
    }

    private fun setCoApplicants() {
        dataBase.provideDataBaseSource().coApplicantsDao().getCoApplicants(mLead!!.leadID!!).observe(viewLifecycleOwner, Observer { coApplicantsMaster ->
            coApplicantsMaster?.let {
                if (coApplicantsMaster.applicantsList!!.isEmpty()) {
                    applicantTab?.add(leadAndLoanDetail.getDefaultApplicant(currentPosition, mLead!!.leadNumber!!))
                } else {
                    applicantTab = coApplicantsMaster.applicantsList
                }

                binding.rcApplicants.layoutManager = LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false)
                applicantAdapter = ApplicantsAdapter(context!!, applicantTab!!)
                binding.rcApplicants.adapter = applicantAdapter
                applicantAdapter!!.setOnItemClickListener(this)
                currentTab = applicantTab!![0]
            }
        })
    }

    private fun showData(applicantList: ArrayList<EmploymentApplicantsModel>?) {
        if (applicantList != null) {
            if (applicantList.size < applicantTab!!.size) {
                for (tab in applicantList.size..applicantTab!!.size) {
                    eApplicantList?.add(EmploymentApplicantsModel())
                }
            }
            for (applicant in applicantList) {
                if (applicant.isMainApplicant) {
                    currentApplicant = applicant
                    currentApplicant.leadApplicantNumber = currentTab.leadApplicantNumber
                    currentApplicant.isMainApplicant = currentTab.isMainApplicant!!
                    eAddressDetail = currentApplicant.addressBean!!
                }
            }
        }
        getDropDownsFromDB()
        fillFormWithCurrentApplicant(currentApplicant)
    }

    private fun setDatePicker() {
        binding.layoutSalary.etJoiningDate.setOnClickListener {
            SelectDate(binding.layoutSalary.etJoiningDate, mContext)
        }

        binding.layoutSenp.etIncorporationDate.setOnClickListener {
            DateDifference(mContext, binding.layoutSenp.etIncorporationDate, binding.layoutSenp.etBusinessVintage)
        }
    }

    override fun onApplicantClick(position: Int, coApplicant: CoApplicantsList) {
        if (formValidation.validateSalaryEmployment(binding.layoutSalary, salarySpinnerList) || formValidation.validateSenpEmployment(binding.layoutSenp, senpSpinnerList)) {
            saveCurrentApplicant()
            ClearEmploymentForm(binding, mContext, allMasterDropDown, states).clearAll()
            currentTab = coApplicant
            currentPosition = position
            waitFor1Sec(position)
        } else showToast(getString(R.string.mandatory_field_missing))
    }

    private fun waitFor1Sec(position: Int) {
        val progress = ProgressDialog(mContext)
        progress.setMessage(getString(R.string.msg_saving))
        progress.setCancelable(false)
        progress.show()
        val handler = Handler()
        handler.postDelayed({
            getParticularApplicantData(position)
            progress.dismiss()
        }, 1000)
        applicantAdapter!!.notifyDataSetChanged()
    }

    private fun getParticularApplicantData(position: Int) {
        currentApplicant = if (position >= eApplicantList!!.size) {
            EmploymentApplicantsModel()
        } else eApplicantList!![position]
        currentApplicant.isMainApplicant = currentTab.isMainApplicant!!
        currentApplicant.leadApplicantNumber = currentTab.leadApplicantNumber
        fillFormWithCurrentApplicant(currentApplicant)
    }

    private fun saveCurrentApplicant() {
        if (eApplicantList!!.size > currentPosition) {
            eApplicantList!![currentPosition] = getCurrentApplicant()
        } else eApplicantList!!.add(currentPosition, getCurrentApplicant())
    }

    private fun setClickListeners() {
        binding.btnPrevious.setOnClickListener { AppEvents.fireEventLoanAppChangeNavFragmentPrevious() }
        binding.btnNext.setOnClickListener {
            saveCurrentApplicant()
            when (formType) {
                SALARY -> validateSalary()
                SENP -> validateSenp()
            }
        }

        binding.ivDocumentUpload.setOnClickListener {}
        salaryIncomeListener(binding.layoutSalary.etGrossIncome, AppEnums.INCOME_TYPE.GROSS_INCOME)
        salaryIncomeListener(binding.layoutSalary.etDeduction, AppEnums.INCOME_TYPE.DEDUCTION)
        senpIncomeListener(binding.layoutSenp.etLastYearIncome, AppEnums.INCOME_TYPE.LAST_YEAR_INCOME)
        senpIncomeListener(binding.layoutSenp.etCurrentYearIncome, AppEnums.INCOME_TYPE.CURRENT_YEAR_INCOME)
        pinCodeListener(binding.layoutSenp.layoutAddress.etPinCode, AppEnums.ADDRESS_TYPE.SENP)
        pinCodeListener(binding.layoutSalary.layoutAddress.etPinCode, AppEnums.ADDRESS_TYPE.SALARY)

        CurrencyConversion().convertToCurrencyType(binding.layoutSalary.etGrossIncome)
        CurrencyConversion().convertToCurrencyType(binding.layoutSalary.etNetIncome)
        CurrencyConversion().convertToCurrencyType(binding.layoutSalary.etDeduction)
        CurrencyConversion().convertToCurrencyType(binding.layoutSenp.etLastYearIncome)
        CurrencyConversion().convertToCurrencyType(binding.layoutSenp.etCurrentYearIncome)
        CurrencyConversion().convertToCurrencyType(binding.layoutSenp.etAverageMonthlyIncome)
        CurrencyConversion().convertToCurrencyType(binding.layoutSenp.etMonthlyIncome)
    }

    private fun salaryIncomeListener(amountField: TextInputEditText?, type: AppEnums.INCOME_TYPE) {
        amountField!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                when (type) {
                    AppEnums.INCOME_TYPE.GROSS_INCOME -> grossIncome = getIncomeValue(amountField.text.toString())
                    AppEnums.INCOME_TYPE.DEDUCTION -> deduction = getIncomeValue(amountField.text.toString())
                    else -> return
                }

                if (grossIncome > deduction) {
                    netIncome = (grossIncome - deduction).toString()
                    binding.layoutSalary.etNetIncome.setText(netIncome)
                }
            }
        })
    }

    private fun senpIncomeListener(amountField: TextInputEditText?, type: AppEnums.INCOME_TYPE) {
        amountField!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                when (type) {
                    AppEnums.INCOME_TYPE.LAST_YEAR_INCOME -> lastYearIncome = getIncomeValue(amountField.text.toString())
                    AppEnums.INCOME_TYPE.CURRENT_YEAR_INCOME -> currentYearIncome = getIncomeValue(amountField.text.toString())
                    else -> return
                }
                averageMonthlyIncome = ((lastYearIncome + currentYearIncome) / 2).toString()
                binding.layoutSenp.etAverageMonthlyIncome.setText(averageMonthlyIncome)
            }
        })
    }

    private fun getIncomeValue(amount: String): Float {
        if (amount.exIsNotEmptyOrNullOrBlank()) {
            val stringAmount = CurrencyConversion().convertToNormalValue(amount)
            val income = stringAmount.toFloat()
            return income
        }
        return 0.0f
    }

    private fun validateSalary() {
        if (formValidation.validateSalaryEmployment(binding.layoutSalary, salarySpinnerList)) {
            ClearEmploymentForm(binding, mContext, allMasterDropDown, states).clearSenpForm(senpSpinnerList)
            presenter.callNetwork(ConstantsApi.CALL_POST_LOAN_APP, dmiConnector = CallPostLoanApp())
        } else showToast(getString(R.string.validation_error))
    }

    private fun validateSenp() {
        if (formValidation.validateSenpEmployment(binding.layoutSenp, senpSpinnerList)) {
            ClearEmploymentForm(binding, mContext, allMasterDropDown, states).clearSalaryForm(salarySpinnerList)
            presenter.callNetwork(ConstantsApi.CALL_POST_LOAN_APP, dmiConnector = CallPostLoanApp())
        } else showToast(getString(R.string.validation_error))
    }

    private fun pinCodeListener(pinCodeField: TextInputEditText?, formType: AppEnums.ADDRESS_TYPE? = null) {
        pinCodeField!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (pinCodeField.text!!.length == 6) {
                    mPinCode = pinCodeField.text.toString()
                    pinCodePresenter.callPinCodeDetailApi(addressType = formType)
                }
            }
        })
    }

    private fun getDropDownsFromDB() {
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(viewLifecycleOwner, Observer { masterDrownDownValues ->
            masterDrownDownValues?.let {
                allMasterDropDown = masterDrownDownValues
                setMasterDropDownValue(allMasterDropDown)
            }
        })
        dataBase.provideDataBaseSource().statesDao().getAllStates().observe(viewLifecycleOwner, Observer { stateMaster ->
            stateMaster?.let {
                states = stateMaster
                setStateDropDownValue()
            }
        })
    }

    private fun setStateDropDownValue() {
        binding.layoutSalary.layoutAddress.spinnerDistrict.adapter = DistrictSpinnerAdapter(mContext, ArrayList())
        binding.layoutSenp.layoutAddress.spinnerDistrict.adapter = DistrictSpinnerAdapter(mContext, ArrayList())
        binding.layoutSalary.layoutAddress.spinnerCity.adapter = CitySpinnerAdapter(mContext, ArrayList())
        binding.layoutSenp.layoutAddress.spinnerCity.adapter = CitySpinnerAdapter(mContext, ArrayList())
        setDropDownForState(binding.layoutSalary.layoutAddress.spinnerState, AppEnums.ADDRESS_TYPE.SALARY)
        setDropDownForState(binding.layoutSenp.layoutAddress.spinnerState, AppEnums.ADDRESS_TYPE.SENP)
    }

    private fun setMasterDropDownValue(dropDown: AllMasterDropDown) {
        binding.spinnerSubProfile.adapter = MasterSpinnerAdapter(mContext, ArrayList())
        setUpProfileSegmentDropDown(binding.spinnerProfileSegment, dropDown)
        setSenpDropDown(binding.layoutSenp, dropDown)
        setSalaryDropDown(binding.layoutSalary, dropDown)
        selectProfileValue(binding.spinnerProfileSegment, currentApplicant)
        fillSenpForm(binding.layoutSenp, currentApplicant)
        fillSalaryForm(binding.layoutSalary, currentApplicant)
    }

    private fun setDropDownForState(spinner: MaterialSpinner, type: AppEnums.ADDRESS_TYPE) {
        spinner.adapter = StatesSpinnerAdapter(mContext, states)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0) {
                    val state = parent.selectedItem as StatesMaster
                    mStateId = state.stateID.toString()
                    districtPresenter.callDistrictApi(addressType = type)
                }
            }
        }
    }

    private fun setUpProfileSegmentDropDown(spinner: MaterialSpinner, dropDown: AllMasterDropDown) {
        spinner.adapter = MasterSpinnerAdapter(mContext, dropDown.ProfileSegment!!)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0) {
                    val profile = parent.selectedItem as DropdownMaster
                    createSubProfileSegmentList(profile.typeDetailID, dropDown.SubProfileSegment)
                }
            }
        }
    }

    private fun createSubProfileSegmentList(profileId: Int?, subProfile: ArrayList<DropdownMaster>?) {
        val subProfileSegment: ArrayList<DropdownMaster> = ArrayList()
        for (sub in subProfile!!) {
            if (sub.refTypeDetailID == profileId) {
                subProfileSegment.add(sub)
            }
        }
        setUpSubProfileSegmentDropDown(binding.spinnerSubProfile, subProfileSegment)
    }

    private fun setUpSubProfileSegmentDropDown(spinner: MaterialSpinner, subProfile: ArrayList<DropdownMaster>) {
        spinner.adapter = MasterSpinnerAdapter(mContext, subProfile)
        selectSubProfileValue(binding.spinnerSubProfile, currentApplicant)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0) {
                    val profileSub = parent.selectedItem as DropdownMaster
                    when (profileSub.typeDetailID) {
                        CASH_SALARY, BANK_SALARY -> {
                            formType = SALARY
                            showSalaryForm(profileSub.typeDetailID!!)
                        }
                        ITR, ASSESED_INCOME -> {
                            formType = SENP
                            showSenpForm(profileSub.typeDetailID!!)
                        }
                    }
                }
            }
        }
    }

    private fun showSalaryForm(id: Int) {
        if (id == BANK_SALARY) {
            binding.layoutSalary.cbIsPensioner.visibility = View.VISIBLE
        } else {
            binding.layoutSalary.cbIsPensioner.visibility = View.GONE
        }
        binding.layoutSalary.llSalary.visibility = View.VISIBLE
        binding.layoutSenp.llSenp.visibility = View.GONE
        ClearEmploymentForm(binding, mContext, allMasterDropDown, states).clearSenpForm(senpSpinnerList)
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
        ClearEmploymentForm(binding, mContext, allMasterDropDown, states).clearSalaryForm(salarySpinnerList)
    }

    private fun setSalaryDropDown(binding: LayoutSalaryBinding, dropDown: AllMasterDropDown) {
        binding.spinnerSector.adapter = MasterSpinnerAdapter(mContext, dropDown.Sector!!)
        binding.spinnerIndustry.adapter = MasterSpinnerAdapter(mContext, dropDown.Industry!!)
        binding.spinnerEmploymentType.adapter = MasterSpinnerAdapter(mContext, dropDown.EmploymentType!!)
    }

    private fun setSenpDropDown(binding: LayoutSenpBinding, dropDown: AllMasterDropDown) {
        binding.spinnerConstitution.adapter = MasterSpinnerAdapter(mContext, dropDown.Constitution!!)
        binding.spinnerIndustry.adapter = MasterSpinnerAdapter(mContext, dropDown.Industry!!)
        binding.spinnerBusinessSetUpType.adapter = MasterSpinnerAdapter(mContext, dropDown.BusinessSetupType!!)
    }

    private fun fillFormWithCurrentApplicant(currentApplicant: EmploymentApplicantsModel) {
        selectProfileValue(binding.spinnerProfileSegment, currentApplicant)
        selectSubProfileValue(binding.spinnerSubProfile, currentApplicant)
        fillSenpForm(binding.layoutSenp, currentApplicant)
        fillSalaryForm(binding.layoutSalary, currentApplicant)
        checkSubmission()
    }

    private fun checkSubmission() {
        if (mLead!!.status == AppEnums.LEAD_TYPE.SUBMITTED.type) {
            DisableEmploymentForm(binding)
        }
    }

    private fun selectProfileValue(spinner: Spinner, currentApplicant: EmploymentApplicantsModel) {
        for (index in 0 until spinner.count - 1) {
            val obj = spinner.getItemAtPosition(index) as DropdownMaster
            if (obj.typeDetailID == currentApplicant.profileSegmentTypeDetailID) {
                spinner.setSelection(index + 1)
                createSubProfileSegmentList(obj.typeDetailID, allMasterDropDown.SubProfileSegment)
                return
            }
        }
    }

    private fun selectSubProfileValue(spinner: Spinner, currentApplicant: EmploymentApplicantsModel) {
        for (index in 0 until spinner.count - 1) {
            val obj = spinner.getItemAtPosition(index) as DropdownMaster
            if (obj.typeDetailID == currentApplicant.subProfileTypeDetailID) {
                spinner.setSelection(index + 1)
                return
            }
        }
    }

    private fun fillSenpForm(binding: LayoutSenpBinding, applicant: EmploymentApplicantsModel) {
        binding.etIncorporationDate.setText(currentApplicant.dateOfIncorporation)
        binding.etBusinessName.setText(currentApplicant.companyName)
        binding.etGstRegistration.setText(currentApplicant.gstRegistration)
        binding.etBusinessVintage.setText(currentApplicant.businessVinatgeInYear.toString())
        binding.etLastYearIncome.setText(currentApplicant.incomeDetail?.itrLastYearIncome.toString())
        binding.etCurrentYearIncome.setText(currentApplicant.incomeDetail?.itrCurrentYearIncome.toString())
        binding.etAverageMonthlyIncome.setText(currentApplicant.incomeDetail?.itrAverageMonthlyIncome.toString())
        binding.etMonthlyIncome.setText(currentApplicant.incomeDetail?.assessedMonthlyIncome.toString())
        binding.cbAllEarningMember.isChecked = currentApplicant.allEarningMembers
        selectMasterDropdownValue(binding.spinnerBusinessSetUpType, applicant.businessSetupTypeDetailID)
        selectMasterDropdownValue(binding.spinnerConstitution, applicant.constitutionTypeDetailID)
        selectMasterDropdownValue(binding.spinnerIndustry, applicant.industryTypeDetailID)
        fillAddress(binding.layoutAddress, applicant.addressBean)
    }

    private fun fillSalaryForm(binding: LayoutSalaryBinding, applicant: EmploymentApplicantsModel) {
        binding.etJoiningDate.setText(currentApplicant.dateOfJoining)
        binding.etTotalExperience.setText(currentApplicant.totalExperience)
        binding.etRetirementAge.setText(currentApplicant.retirementAge.toString())
        binding.etOfficialMailId.setText(currentApplicant.officialMailID)
        binding.etDesignation.setText(currentApplicant.designation)
        binding.etEmployeeId.setText(currentApplicant.employeeID)
        binding.etCompanyName.setText(currentApplicant.companyName)
        binding.cbIsPensioner.isChecked = currentApplicant.isPensioner
        binding.etGrossIncome.setText(currentApplicant.incomeDetail?.salariedGrossIncome.toString())
        binding.etDeduction.setText(currentApplicant.incomeDetail?.salariedDeduction.toString())
        binding.etNetIncome.setText(currentApplicant.incomeDetail?.salariedNetIncome.toString())
        selectMasterDropdownValue(binding.spinnerSector, applicant.sectorTypeDetailID)
        selectMasterDropdownValue(binding.spinnerEmploymentType, applicant.employmentTypeDetailID)
        selectMasterDropdownValue(binding.spinnerIndustry, applicant.industryTypeDetailID)
        fillAddress(binding.layoutAddress, applicant.addressBean)
    }

    private fun fillAddress(binding: LayoutEmploymentAddressBinding, address: AddressDetail?) {
        binding.etAddress.setText(address!!.address1)
        binding.etLandmark.setText(address.landmark)
        binding.etPinCode.setText(address.zip)
        binding.etContactNum.setText(address.contactNum)
    }

    private fun selectMasterDropdownValue(spinner: Spinner, id: Int?) {
        for (index in 0 until spinner.count - 1) {
            val obj = spinner.getItemAtPosition(index) as DropdownMaster
            if (obj.typeDetailID == id) {
                spinner.setSelection(index + 1)
                return
            }
        }
    }

    private fun getDataFromDB() {
        dataBase.provideDataBaseSource().employmentDao().getEmployment(leadIdForApplicant).observe(this, Observer { employmentInfo ->
            employmentInfo?.let {
                employmentMaster = employmentInfo
                eDraftData = employmentMaster.draftData
                eApplicantList = eDraftData.applicantDetails
                if (eApplicantList!!.size < 0) {
                    eApplicantList!!.add(EmploymentApplicantsModel())
                }
            }
            setCoApplicants()
            showData(eApplicantList)
        })
    }

    override val pinCode: String
        get() = mPinCode

    override fun getPinCodeSuccess(value: Response.ResponsePinCodeDetail, addressType: AppEnums.ADDRESS_TYPE?) {
        if (value.responseObj!!.size > 0) {
            pinCodeObj = value.responseObj[0]
            when (addressType) {
                AppEnums.ADDRESS_TYPE.SENP -> selectStateValue(binding.layoutSenp.layoutAddress.spinnerState, AppEnums.ADDRESS_TYPE.SENP)
                AppEnums.ADDRESS_TYPE.SALARY -> selectStateValue(binding.layoutSalary.layoutAddress.spinnerState, AppEnums.ADDRESS_TYPE.SALARY)
                else -> return
            }
        } else clearPinCodes(addressType?.type)
    }

    private fun selectStateValue(spinner: MaterialSpinner, type: AppEnums.ADDRESS_TYPE) {
        for (index in 0 until spinner.count - 1) {
            val obj = spinner.getItemAtPosition(index) as StatesMaster
            if (obj.stateID == pinCodeObj!!.stateID) {
                spinner.setSelection(index + 1)
                mStateId = pinCodeObj!!.stateID.toString()
                districtPresenter.callDistrictApi(addressType = type)
                spinner.isEnabled = false
                return
            }
        }
    }

    private fun clearPinCodes(formType: String? = null) {
        when (formType) {
            AppEnums.ADDRESS_TYPE.SALARY.type -> clearSalaryPinCodeField()
            AppEnums.ADDRESS_TYPE.SENP.type -> clearSenpPinCodeField()
        }
    }

    private fun clearSalaryPinCodeField() {
        binding.layoutSalary.layoutAddress.spinnerState.isEnabled = true
        binding.layoutSalary.layoutAddress.spinnerDistrict.isEnabled = true
        binding.layoutSalary.layoutAddress.spinnerCity.isEnabled = true
        binding.layoutSalary.layoutAddress.spinnerState.adapter = StatesSpinnerAdapter(mContext, states)
        binding.layoutSalary.layoutAddress.spinnerDistrict.adapter = DistrictSpinnerAdapter(mContext, ArrayList())
        binding.layoutSalary.layoutAddress.spinnerCity.adapter = CitySpinnerAdapter(mContext, ArrayList())
    }

    private fun clearSenpPinCodeField() {
        binding.layoutSenp.layoutAddress.spinnerState.isEnabled = true
        binding.layoutSenp.layoutAddress.spinnerDistrict.isEnabled = true
        binding.layoutSenp.layoutAddress.spinnerCity.isEnabled = true
        binding.layoutSenp.layoutAddress.spinnerState.adapter = StatesSpinnerAdapter(mContext, states)
        binding.layoutSenp.layoutAddress.spinnerDistrict.adapter = DistrictSpinnerAdapter(mContext, ArrayList())
        binding.layoutSenp.layoutAddress.spinnerCity.adapter = CitySpinnerAdapter(mContext, ArrayList())
    }

    override fun getPinCodeFailure(msg: String) = clearPinCodes()

    override val stateId: String
        get() = mStateId

    override fun getDistrictSuccess(value: Response.ResponseDistrict, addressType: AppEnums.ADDRESS_TYPE?) {
        if (value.responseObj != null && value.responseObj.size > 0) {
            when (addressType) {
                AppEnums.ADDRESS_TYPE.SENP -> setDistrict(binding.layoutSenp.layoutAddress.spinnerDistrict, value, AppEnums.ADDRESS_TYPE.SENP)
                AppEnums.ADDRESS_TYPE.SALARY -> setDistrict(binding.layoutSalary.layoutAddress.spinnerDistrict, value, AppEnums.ADDRESS_TYPE.SALARY)
                else -> return
            }
        }
    }

    private fun setDistrict(spinner: MaterialSpinner, response: Response.ResponseDistrict, type: AppEnums.ADDRESS_TYPE) {
        spinner.adapter = DistrictSpinnerAdapter(mContext, response.responseObj!!)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0) {
                    val district = parent.selectedItem as Response.DistrictObj
                    mDistrictId = district.districtID.toString()
                    cityPresenter.callCityApi(addressType = type)
                }
            }
        }
        selectDistrictValue(spinner, type)
    }

    private fun selectDistrictValue(spinner: Spinner, type: AppEnums.ADDRESS_TYPE) {
        for (index in 0 until spinner.count - 1) {
            val obj = spinner.getItemAtPosition(index) as Response.DistrictObj
            if (obj.districtID == pinCodeObj!!.districtID) {
                spinner.setSelection(index + 1)
                mDistrictId = obj.districtID.toString()
                cityPresenter.callCityApi(addressType = type)
                spinner.isEnabled = false
                return
            }
        }
    }

    override fun getDistrictFailure(msg: String) = showToast(msg)

    override val districtId: String
        get() = mDistrictId

    override fun getCitySuccess(value: Response.ResponseCity, addressType: AppEnums.ADDRESS_TYPE?) {
        if (value.responseObj != null && value.responseObj.size > 0) {
            when (addressType) {
                AppEnums.ADDRESS_TYPE.SENP -> setCityValue(binding.layoutSenp.layoutAddress.spinnerCity, value.responseObj)
                AppEnums.ADDRESS_TYPE.SALARY -> setCityValue(binding.layoutSalary.layoutAddress.spinnerCity, value.responseObj)
                else -> return
            }
        }
    }

    private fun setCityValue(spinner: MaterialSpinner, responseObj: ArrayList<Response.CityObj>) {
        spinner.adapter = CitySpinnerAdapter(mContext, responseObj)
        for (index in 0 until spinner.count - 1) {
            val obj = spinner.getItemAtPosition(index) as Response.CityObj
            if (obj.cityID == pinCodeObj!!.cityID) {
                spinner.setSelection(index + 1)
                spinner.isEnabled = false
                return
            }
        }
    }

    override fun getCityFailure(msg: String) = showToast(msg)

    private fun getCurrentApplicant(): EmploymentApplicantsModel {
        val applicant = EmploymentApplicantsModel()
        val profile = binding.spinnerProfileSegment.selectedItem as DropdownMaster?
        val subProfile = binding.spinnerSubProfile.selectedItem as DropdownMaster?
        applicant.profileSegmentTypeDetailID = profile?.typeDetailID
        applicant.subProfileTypeDetailID = subProfile?.typeDetailID
        applicant.isMainApplicant = currentTab.isMainApplicant!!
        applicant.leadApplicantNumber = currentTab.leadApplicantNumber
        when (formType) {
            SALARY -> return getSalaryForm(binding.layoutSalary, applicant)
            SENP -> return getSenpForm(binding.layoutSenp, applicant)
        }
        return applicant
    }

    private fun getSalaryForm(binding: LayoutSalaryBinding, applicant: EmploymentApplicantsModel): EmploymentApplicantsModel {
        val sector = binding.spinnerSector.selectedItem as DropdownMaster?
        val industry = binding.spinnerIndustry.selectedItem as DropdownMaster?
        val employment = binding.spinnerEmploymentType.selectedItem as DropdownMaster?
        applicant.companyName = binding.etCompanyName.text.toString()
        applicant.sectorTypeDetailID = sector?.typeDetailID
        applicant.industryTypeDetailID = industry?.typeDetailID
        applicant.employmentTypeDetailID = employment?.typeDetailID
        applicant.designation = binding.etDesignation.text.toString()
        applicant.dateOfJoining = binding.etJoiningDate.text.toString()
        applicant.totalExperience = binding.etTotalExperience.text.toString()
        applicant.retirementAge = binding.etRetirementAge.text.toString().toInt()
        applicant.officialMailID = binding.etOfficialMailId.text.toString()
        applicant.addressBean = getAddress(binding.layoutAddress)
        applicant.incomeDetail = getSalaryIncomeDetail(binding)
        applicant.employeeID = binding.etEmployeeId.text.toString()
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
        incomeDetail.assessedMonthlyIncome = CurrencyConversion().convertToNormalValue(binding.etMonthlyIncome.text.toString()).toFloat()
        incomeDetail.itrAverageMonthlyIncome = CurrencyConversion().convertToNormalValue(binding.etAverageMonthlyIncome.text.toString()).toFloat()
        incomeDetail.itrLastYearIncome = CurrencyConversion().convertToNormalValue(binding.etLastYearIncome.text.toString()).toFloat()
        incomeDetail.itrCurrentYearIncome = CurrencyConversion().convertToNormalValue(binding.etCurrentYearIncome.text.toString()).toFloat()
        return incomeDetail
    }

    private fun getSenpForm(binding: LayoutSenpBinding, applicant: EmploymentApplicantsModel): EmploymentApplicantsModel {
        val constitution = binding.spinnerConstitution.selectedItem as DropdownMaster?
        val industry = binding.spinnerIndustry.selectedItem as DropdownMaster?
        val businessSetupType = binding.spinnerBusinessSetUpType.selectedItem as DropdownMaster?
        applicant.companyName = binding.etBusinessName.text.toString()
        applicant.constitutionTypeDetailID = constitution?.typeDetailID
        applicant.businessSetupTypeDetailID = businessSetupType?.typeDetailID
        applicant.industryTypeDetailID = industry?.typeDetailID
        applicant.dateOfIncorporation = binding.etIncorporationDate.text.toString()
        applicant.gstRegistration = binding.etGstRegistration.text.toString()
        applicant.businessVinatgeInYear = binding.etBusinessVintage.text.toString().toInt()
        applicant.addressBean = getAddress(binding.layoutAddress)
        applicant.allEarningMembers = binding.cbAllEarningMember.isChecked
        applicant.incomeDetail = getSenpIncomeDetail(binding)
        return applicant
    }

    private fun getAddress(binding: LayoutEmploymentAddressBinding): AddressDetail {
        val address = AddressDetail()
        val state = binding.spinnerState.selectedItem as StatesMaster?
        val district = binding.spinnerDistrict.selectedItem as Response.DistrictObj?
        val city = binding.spinnerCity.selectedItem as Response.CityObj?
        address.address1 = binding.etAddress.text.toString()
        address.zip = binding.etPinCode.text.toString()
        address.landmark = binding.etLandmark.text.toString()
        address.contactNum = binding.etContactNum.text.toString()
        address.cityID = city?.cityID
        address.stateID = state?.stateID
        address.districtID = district?.districtID
        return address
    }

    private fun getEmploymentMaster(): EmploymentMaster {
        eDraftData.applicantDetails = eApplicantList
        employmentMaster.draftData = eDraftData
        employmentMaster.leadID = leadIdForApplicant.toInt()
        return employmentMaster
    }

    private fun saveDataToDB(employment: EmploymentMaster) {
        GlobalScope.launch {
            dataBase.provideDataBaseSource().employmentDao().insertEmployment(employment)
        }
    }

    inner class CallGetLoan : ViewGeneric<ArrayList<String>?, Response.ResponseGetLoanApplication>(context = mContext) {

        override val apiRequest: ArrayList<String>?
            get() = arrayListOf(mLead!!.leadID.toString(), employmentMaster.storageType)

        override fun getApiSuccess(value: Response.ResponseGetLoanApplication) {
            if (value.responseCode == Constants.SUCCESS) {
                value.responseObj?.let {
                    employmentMaster = LeadRequestResponseConversion().toEmploymentMaster(value.responseObj)
                    eDraftData = employmentMaster.draftData
                    eApplicantList = eDraftData.applicantDetails
                }
                setCoApplicants()
                showData(eApplicantList)
            } else getDataFromDB()
        }
    }

    inner class CallPostLoanApp : ViewGeneric<LoanApplicationRequest, Response.ResponseGetLoanApplication>(context = mContext) {
        override val apiRequest: LoanApplicationRequest
            get() = RequestConversion().employmentRequest(getEmploymentMaster())

        override fun getApiSuccess(value: Response.ResponseGetLoanApplication) {
            if (value.responseCode == Constants.SUCCESS) {
                saveDataToDB(getEmploymentMaster())
                AppEvents.fireEventLoanAppChangeNavFragmentNext()
            } else saveDataToDB(getEmploymentMaster())
        }
    }

}
*/
