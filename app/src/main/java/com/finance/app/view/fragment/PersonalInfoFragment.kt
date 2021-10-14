package com.finance.app.view.fragment

//import com.finance.app.view.customViews.Interfaces.IspinnerMainView

class PersonalInfoFragment{

}
/*
class PersonalInfoFragment : BaseFragment(), ApplicantsAdapter.ItemClickListener,
        ApplicantsAdapter.ItemLongClickListener{

        @Inject
        lateinit var sharedPreferences: SharedPreferencesUtil
        @Inject
        lateinit var formValidation: FormValidation
        @Inject
        lateinit var dataBase: DataBaseUtil
        private lateinit var binding: FragmentPersonalBinding
        private lateinit var mContext: Context
        private var mLead: AllLeadMaster? = null
        private var leadNumber: String = ""
        private val frag = this
        private val presenter = Presenter()
        private var applicantAdapter: ApplicantsAdapter? = null
        private var personalApplicantsList: ArrayList<PersonalApplicantsModel>? = ArrayList()
        private var personalInfoMaster: PersonalInfoMaster? = PersonalInfoMaster()
        private var currentApplicant: PersonalApplicantsModel? = PersonalApplicantsModel()
        private var contactDetail: ContactDetail? = ContactDetail()
        private var image: Bitmap? = null
        private var personalAddressDetail: ArrayList<AddressDetail>? = ArrayList()
        private var currentPosition = 0
        private var pDraftData = PersonalApplicantList()
        private var allMasterDropDown: AllMasterDropDown = AllMasterDropDown()
        private var applicantTab: ArrayList<CoApplicantsList>? = ArrayList()
        private var relationshipList: ArrayList<DropdownMaster> = ArrayList()
        private val conversion = CurrencyConversion()
        private lateinit var verifyOTPDialog: Dialog
        private var otp: Int? = 0
        private val coApplicant2: CoApplicantsList = CoApplicantsList()
        private lateinit var gender: CustomSpinnerViewTest<DropdownMaster>
        private lateinit var nationality: CustomSpinnerViewTest<DropdownMaster>
        private lateinit var dobProof: CustomSpinnerViewTest<DropdownMaster>
        private lateinit var livingStandard: CustomSpinnerViewTest<DropdownMaster>
        private lateinit var detailQualification: CustomSpinnerViewTest<DropdownMaster>
        private lateinit var qualification: CustomSpinnerViewTest<DropdownMaster>
        private lateinit var caste: CustomSpinnerViewTest<DropdownMaster>
        private lateinit var religion: CustomSpinnerViewTest<DropdownMaster>
        private lateinit var relationship: CustomSpinnerViewTest<DropdownMaster>
        private lateinit var currentAddressProof: CustomSpinnerViewTest<DropdownMaster>
        private lateinit var permanentAddressProof: CustomSpinnerViewTest<DropdownMaster>
        private lateinit var maritalStatus: CustomSpinnerViewTest<DropdownMaster>
        private lateinit var permanentResidenceType: CustomSpinnerViewTest<DropdownMaster>
        private lateinit var currentResidenceType: CustomSpinnerViewTest<DropdownMaster>

        companion object {
            private const val SINGLE = 63
            private const val SELF = 221
            private const val RENTED = 253
            private const val PERMANENT = "Permanent"
            private const val CURRENT = "Current"
            private lateinit var kycList: ArrayList<AddKyc>
            private val leadAndLoanDetail = LeadAndLoanDetail()
        }


        fun onCreate() {
            //super.onCreate()
            EventBus.getDefault().register(this)
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            binding = initBinding(inflater, container, R.layout.fragment_personal)


            init()
            return binding.root
        }

        override fun init() {
            ArchitectureApp.instance.component.inject(this)
            mLead = sharedPreferences.getLeadDetail()
            leadNumber = mLead!!.leadNumber!!
            kycList = ArrayList()
            mContext = context!!
            getLoanInfo()
            SetPersonalMandatoryField(binding)
            setDatePicker()
            checkKycDataList()
            setClickListeners()
            setUpCustomViews()
        }

        private fun getLoanInfo() {
            presenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP, CallGetLoan())
        }

        private val leadIdForApplicant: String
            get() = mLead!!.leadID.toString()

        private fun setUpCustomViews() {
            activity?.let {
                binding.personalAddressLayout.customCurrentZipAddressView.attachActivity(activity = activity!!)
                binding.personalAddressLayout.customPermanentZipAddressView.attachActivity(activity = activity!!)
            }
        }

        private fun saveDataToDB(personalInfo: PersonalInfoMaster) {
            GlobalScope.launch {
                dataBase.provideDataBaseSource().personalInfoDao().insertPersonalInfo(personalInfo)
            }
        }

        private fun getDropDownsFromDB() {
            dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(viewLifecycleOwner, Observer { masterDrownDownValues ->
                masterDrownDownValues.let {
                    allMasterDropDown = it
                    setRelationshipList(allMasterDropDown.Relationship)
                    setMasterDropDownValue(allMasterDropDown)
                }
            })
        }

        private fun setRelationshipList(dropDowns: ArrayList<DropdownMaster>?) {
            for (dropdown in dropDowns!!) {
                if (dropdown.typeDetailID != SELF) {
                    relationshipList.add(dropdown)
                }
            }
        }

        private fun getDataFromDB() {
            dataBase.provideDataBaseSource().personalInfoDao().getPersonalInfo(leadIdForApplicant).observe(this, Observer { personalMaster ->
                personalMaster?.let {
                    personalInfoMaster = personalMaster
                    pDraftData = personalInfoMaster?.draftData!!
                    personalApplicantsList = pDraftData.applicantDetails
                }
                setCoApplicants()
                showData(personalApplicantsList)
            })
        }

        private fun showData(applicantList: ArrayList<PersonalApplicantsModel>?) {
            if (applicantList == null || applicantList.size <= 0) {
                getApplicantFromLead()
            } else {
                for (applicant in applicantList) {
                    if (applicant.isMainApplicant!!) {
                        currentApplicant = applicant
                        personalAddressDetail = currentApplicant?.addressDetailList
                        contactDetail = currentApplicant?.contactDetail
                    }
                }
            }
            getDropDownsFromDB()
        }

        private fun updateCustomZipCode(customZipView: CustomZipAddressView, addressDetail: AddressDetail) {
            customZipView.updateAddressData(addressDetail = addressDetail)
        }

        private fun setMasterDropDownValue(dropDown: AllMasterDropDown) {
            setCustomSpinner(dropDown)
            setUpRelationshipValue()
            currentApplicant?.let {
                fillValueInMasterDropDown(currentApplicant!!)
            }
        }

        private fun setCustomSpinner(allMasterDropDown: AllMasterDropDown) {
            dobProof = CustomSpinnerViewTest(context = mContext, dropDowns = allMasterDropDown.DOBProof!!, label = "DOB Proof")
            binding.basicInfoLayout.layoutDobProof.addView(dobProof)
            livingStandard = CustomSpinnerViewTest(context = mContext, dropDowns = allMasterDropDown.LivingStandardIndicators!!, label = "Living Standard")
            binding.basicInfoLayout.layoutLivingStandard.addView(livingStandard)
            detailQualification = CustomSpinnerViewTest(context = mContext, dropDowns = allMasterDropDown.DetailQualification!!, label = "Detail Qualification")
            binding.basicInfoLayout.layoutDetailQualification.addView(detailQualification)
            qualification = CustomSpinnerViewTest(context = mContext, dropDowns = allMasterDropDown.Qualification!!, label = "Qualification")
            binding.basicInfoLayout.layoutQualification.addView(qualification)
            caste = CustomSpinnerViewTest(context = mContext, dropDowns = allMasterDropDown.Caste!!, label = "Caste")
            binding.basicInfoLayout.layoutCaste.addView(caste)
            religion = CustomSpinnerViewTest(context = mContext, dropDowns = allMasterDropDown.Religion!!, label = "Religion")
            binding.basicInfoLayout.layoutReligion.addView(religion)
            nationality = CustomSpinnerViewTest(context = mContext, dropDowns = allMasterDropDown.Nationality!!, label = "Nationality")
            binding.basicInfoLayout.layoutNationality.addView(nationality)
            gender = CustomSpinnerViewTest(context = mContext, dropDowns = allMasterDropDown.Gender!!, label = "Gender")
            binding.basicInfoLayout.layoutGender.addView(gender)
            permanentAddressProof = CustomSpinnerViewTest(context = mContext, dropDowns = allMasterDropDown.AddressProof!!, label = "Address Proof")
            binding.personalAddressLayout.layoutPermanentAddressProof.addView(permanentAddressProof)
            currentAddressProof = CustomSpinnerViewTest(context = mContext, dropDowns = allMasterDropDown.AddressProof!!, label = "Address Proof")
            binding.personalAddressLayout.layoutCurrentAddressProof.addView(currentAddressProof)
            maritalStatus = CustomSpinnerViewTest(context = mContext, dropDowns = allMasterDropDown.MaritalStatus!!, label = "Marital Status", ispinnerMainView = object : IspinnerMainView<DropdownMaster> {
                override fun getSelectedValue(value: DropdownMaster) {
                    if (value.typeDetailID == SINGLE) {
                        binding.basicInfoLayout.llSpouse.visibility = View.GONE
                    } else {
                        binding.basicInfoLayout.llSpouse.visibility = View.VISIBLE
                    }
                    binding.basicInfoLayout.layoutMaritalStatus.removeAllViews()
                }
            })
            binding.basicInfoLayout.layoutMaritalStatus.addView(maritalStatus)

            permanentResidenceType = CustomSpinnerViewTest(context = mContext, dropDowns = allMasterDropDown.ResidenceType!!, label = "Residence Type", ispinnerMainView = object : IspinnerMainView<DropdownMaster> {
                override fun getSelectedValue(value: DropdownMaster) {
                    if (value.typeDetailID == RENTED) {
                        binding.personalAddressLayout.inputLayoutPermanentRentAmount.visibility = View.VISIBLE
                    } else {
                        binding.personalAddressLayout.inputLayoutPermanentRentAmount.visibility = View.GONE
                    }
                }
            })

            binding.personalAddressLayout.layoutPermanentResidenceType.addView(permanentResidenceType)
            currentResidenceType = CustomSpinnerViewTest(context = mContext, dropDowns = allMasterDropDown.ResidenceType!!, label = "Residence Type", ispinnerMainView = object : IspinnerMainView<DropdownMaster> {
                override fun getSelectedValue(value: DropdownMaster) {
                    if (value.typeDetailID == RENTED) {
                        binding.personalAddressLayout.inputLayoutCurrentRentAmount.visibility = View.VISIBLE
                    } else {
                        binding.personalAddressLayout.inputLayoutCurrentRentAmount.visibility = View.GONE
                    }
                }
            })
            binding.personalAddressLayout.layoutCurrentResidenceType.addView(currentResidenceType)

        }

        private fun getApplicantFromLead(): PersonalApplicantsModel {
            currentApplicant = PersonalApplicantsModel()
            currentApplicant!!.firstName = mLead!!.applicantFirstName
            currentApplicant!!.middleName = mLead!!.applicantMiddleName
            currentApplicant!!.lastName = mLead!!.applicantLastName
            personalAddressDetail = currentApplicant!!.addressDetailList
            contactDetail!!.mobile = mLead!!.applicantContactNumber
            contactDetail!!.email = mLead!!.applicantEmail
            personalApplicantsList?.add(currentApplicant!!)

            return currentApplicant!!
        }

        private fun fillFormWithCurrentApplicant(currentApplicant: PersonalApplicantsModel?) {
            currentApplicant?.let {
                contactDetail?.let {
                    binding.basicInfoLayout.etEmail.setText(contactDetail!!.email)
                    binding.basicInfoLayout.etMobile.setText(contactDetail!!.mobile)
                    if (currentApplicant.contactDetail!!.isMobileVerified!!) {
                        binding.basicInfoLayout.etMobile.isEnabled = false
                        binding.basicInfoLayout.btnGetOTP.visibility = View.GONE
                        binding.basicInfoLayout.ivVerifiedStatus.visibility = View.VISIBLE
                    }
                }
            }

            binding.basicInfoLayout.etDOB.setText(ConvertDate().convertToAppFormat(currentApplicant?.dateOfBirth!!))
            binding.basicInfoLayout.cbIncomeConsidered.isChecked = currentApplicant.incomeConsidered!!
            binding.basicInfoLayout.etFatherLastName.setText(currentApplicant.fatherLastName)
            binding.basicInfoLayout.etFatherMiddleName.setText(currentApplicant.fatherMiddleName)
            binding.basicInfoLayout.etFatherFirstName.setText(currentApplicant.fatherFirstName)
            binding.basicInfoLayout.etFirstName.setText(currentApplicant.firstName)
            binding.basicInfoLayout.etMiddleName.setText(currentApplicant.middleName)
            binding.basicInfoLayout.etNumOfDependent.setText(currentApplicant.numberOfDependents.toString())
            binding.basicInfoLayout.etNumOfEarningMember.setText(currentApplicant.numberOfEarningMembers.toString())
            binding.basicInfoLayout.etLastName.setText(currentApplicant.lastName)
            binding.basicInfoLayout.etAge.setText(currentApplicant.age.toString())
            binding.basicInfoLayout.etAlternateNum.setText(currentApplicant.alternateContact.toString())
            if (currentApplicant.maritialStatusTypeDetailID != SINGLE) {
                binding.basicInfoLayout.etSpouseMiddleName.setText(currentApplicant.spouseMiddleName)
                binding.basicInfoLayout.etSpouseFirstName.setText(currentApplicant.spouseFirstName)
                binding.basicInfoLayout.etSpouseLastName.setText(currentApplicant.spouseLastName)
            }
            checkSubmission()
        }

        private fun checkSubmission() {
            if (mLead!!.status == AppEnums.LEAD_TYPE.SUBMITTED.type) {
                DisablePersonalForm(binding)
            }
        }

        private fun fillValueInMasterDropDown(currentApplicant: PersonalApplicantsModel) {
            gender.setSelection(currentApplicant.genderTypeDetailID?.toString())
            nationality.setSelection(currentApplicant.nationalityTypeDetailID?.toString())
            religion.setSelection(currentApplicant.religionTypeDetailID?.toString())
            caste.setSelection(currentApplicant.casteTypeDetailID?.toString())
            dobProof.setSelection(currentApplicant.dobProofTypeDetailID?.toString())
            qualification.setSelection(currentApplicant.qualificationTypeDetailID?.toString())
            detailQualification.setSelection(currentApplicant.detailQualificationTypeDetailID?.toString())
            livingStandard.setSelection(currentApplicant.livingStandardTypeDetailId?.toString())
            relationship.setSelection(currentApplicant.relationshipTypeDetailId?.toString())
            maritalStatus.setSelection(currentApplicant.maritialStatusTypeDetailID?.toString())

            fillFormWithCurrentApplicant(currentApplicant)
            if (personalAddressDetail != null && personalAddressDetail!!.size > 0) {
                fillAddressInfo(personalAddressDetail!!)
            }
        }

        private fun fillAddressInfo(addressDetailList: ArrayList<AddressDetail>) {
            fillPermanentAddressInfo(addressDetailList[1])
            fillCurrentAddressInfo(addressDetailList[0])
        }

        private fun fillCurrentAddressInfo(addressDetail: AddressDetail) {
            binding.personalAddressLayout.etCurrentAddress.setText(addressDetail.address1)
            binding.personalAddressLayout.etCurrentLandmark.setText(addressDetail.landmark)
            binding.personalAddressLayout.etCurrentRentAmount.setText(addressDetail.rentAmount.toString())
            binding.personalAddressLayout.etCurrentStaying.setText(addressDetail.stayingInYears.toString())
            currentAddressProof.setSelection(addressDetail.addressTypeDetailID?.toString())
            currentResidenceType.setSelection(addressDetail.residenceTypeTypeDetailID.toString())
            updateCustomZipCode(customZipView = binding.personalAddressLayout.customCurrentZipAddressView, addressDetail = addressDetail)
        }

        private fun fillPermanentAddressInfo(addressDetail: AddressDetail) {
            binding.personalAddressLayout.etPermanentAddress.setText(addressDetail.address1)
            binding.personalAddressLayout.etPermanentLandmark.setText(addressDetail.landmark)
            binding.personalAddressLayout.etPermanentRentAmount.setText(addressDetail.rentAmount.toString())
            binding.personalAddressLayout.etPermanentStaying.setText(addressDetail.stayingInYears.toString())
            permanentAddressProof.setSelection(addressDetail.addressTypeDetailID?.toString())
            permanentResidenceType.setSelection(addressDetail.residenceTypeTypeDetailID?.toString())
            updateCustomZipCode(customZipView = binding.personalAddressLayout.customPermanentZipAddressView, addressDetail = addressDetail)
        }

        private fun setUpRelationshipValue() {
            if (currentPosition == 0) {
                relationship = CustomSpinnerViewTest(context = mContext, dropDowns = allMasterDropDown.Relationship!!, label = "Relationship")
                binding.basicInfoLayout.layoutRelationShip.addView(relationship)

                relationship.setSelection(SELF.toString())
                relationship.disableSelf()
            } else {
                relationship = CustomSpinnerViewTest(context = mContext, dropDowns = relationshipList, label = "Relationship")
                binding.basicInfoLayout.layoutRelationShip.addView(relationship)

                relationship.setSelection(currentApplicant!!.relationshipTypeDetailId?.toString())
            }
        }

        private fun getCurrentApplicant(): PersonalApplicantsModel {
            val currentApplicant = PersonalApplicantsModel()
            val casteDD = caste.getSelectedValue()
            val dQualificationDD = detailQualification.getSelectedValue()
            val qDD = qualification.getSelectedValue()
            val dobProofDD = dobProof.getSelectedValue()
            val genderDD = gender.getSelectedValue()
            val nationalityDD = nationality.getSelectedValue()
            val religionDD = religion.getSelectedValue()
            val mStatusDD = maritalStatus.getSelectedValue()
            val livingStandardDD = livingStandard.getSelectedValue()
            val relationshipDD = relationship.getSelectedValue()
            val dependents = binding.basicInfoLayout.etNumOfDependent.text.toString()
            val earningMembers = binding.basicInfoLayout.etNumOfEarningMember.text.toString()

            currentApplicant.casteTypeDetailID = casteDD?.typeDetailID
            currentApplicant.detailQualificationTypeDetailID = dQualificationDD?.typeDetailID
            currentApplicant.qualificationTypeDetailID = qDD?.typeDetailID
            currentApplicant.dobProofTypeDetailID = dobProofDD?.typeDetailID
            currentApplicant.genderTypeDetailID = genderDD?.typeDetailID
            currentApplicant.nationalityTypeDetailID = nationalityDD?.typeDetailID
            currentApplicant.religionTypeDetailID = religionDD?.typeDetailID
            currentApplicant.relationshipTypeDetailId = relationshipDD?.typeDetailID
            currentApplicant.maritialStatusTypeDetailID = mStatusDD?.typeDetailID
            currentApplicant.livingStandardTypeDetailId = livingStandardDD?.typeDetailID
            currentApplicant.leadApplicantNumber = leadAndLoanDetail.getLeadApplicantNum(currentPosition + 1, leadNumber)
            currentApplicant.numberOfEarningMembers = if (earningMembers == "") 0 else earningMembers.toInt()
            currentApplicant.numberOfDependents = if (dependents == "") 0 else dependents.toInt()
            currentApplicant.firstName = binding.basicInfoLayout.etFirstName.text.toString()
            currentApplicant.middleName = binding.basicInfoLayout.etMiddleName.text.toString()
            currentApplicant.lastName = binding.basicInfoLayout.etLastName.text.toString()
            currentApplicant.spouseFirstName = binding.basicInfoLayout.etSpouseFirstName.text.toString()
            currentApplicant.spouseMiddleName = binding.basicInfoLayout.etSpouseMiddleName.text.toString()
            currentApplicant.spouseLastName = binding.basicInfoLayout.etSpouseLastName.text.toString()
            currentApplicant.fatherFirstName = binding.basicInfoLayout.etFatherFirstName.text.toString()
            currentApplicant.fatherMiddleName = binding.basicInfoLayout.etFatherMiddleName.text.toString()
            currentApplicant.fatherLastName = binding.basicInfoLayout.etFatherLastName.text.toString()
            currentApplicant.dateOfBirth = ConvertDate().convertToApiFormat(binding.basicInfoLayout.etDOB.text.toString())
            currentApplicant.age = binding.basicInfoLayout.etAge.text.toString().toInt()
            currentApplicant.isMainApplicant = currentPosition == 0
            currentApplicant.incomeConsidered = binding.basicInfoLayout.cbIncomeConsidered.isChecked
            currentApplicant.alternateContact = binding.basicInfoLayout.etAlternateNum.text.toString()
            currentApplicant.contactDetail = getContactDetail()
            currentApplicant.addressDetailList = getAddressDetailList()
            return currentApplicant
        }

        private fun getContactDetail(): ContactDetail? {
            contactDetail?.email = binding.basicInfoLayout.etEmail.text.toString()
            contactDetail?.mobile = binding.basicInfoLayout.etMobile.text.toString()
            return contactDetail
        }

        private fun getAddressDetailList(): ArrayList<AddressDetail>? {
            val cAddressDetail = AddressDetail()
            val cResidenceType = currentAddressProof.getSelectedValue()
            val cAddressProof = currentAddressProof.getSelectedValue()

            cAddressDetail.rentAmount = CurrencyConversion().convertToNormalValue(binding.personalAddressLayout.etCurrentRentAmount.text.toString())
            cAddressDetail.stayingInYears = binding.personalAddressLayout.etCurrentStaying.text.toString().toFloat()
            cAddressDetail.address1 = binding.personalAddressLayout.etCurrentAddress.text.toString()
            cAddressDetail.landmark = binding.personalAddressLayout.etCurrentLandmark.text.toString()
            cAddressDetail.zip = binding.personalAddressLayout.customCurrentZipAddressView.pinCode
            cAddressDetail.addressTypeDetail = CURRENT
            cAddressDetail.stateID = binding.personalAddressLayout.customCurrentZipAddressView.getStateId()
            cAddressDetail.districtID = binding.personalAddressLayout.customCurrentZipAddressView.getDistrictId()
            cAddressDetail.cityID = binding.personalAddressLayout.customCurrentZipAddressView.getCityId()
            cAddressDetail.residenceTypeTypeDetailID = cResidenceType?.typeDetailID
            cAddressDetail.addressTypeDetailID = cAddressProof?.typeDetailID

            var pAddressDetail = AddressDetail()
            if (binding.personalAddressLayout.cbSameAsCurrent.isChecked) {
                pAddressDetail = cAddressDetail
            } else {
                val pResidenceType = permanentResidenceType.getSelectedValue()
                val pAddressProof = permanentAddressProof.getSelectedValue()

                pAddressDetail.rentAmount = CurrencyConversion().convertToNormalValue(binding.personalAddressLayout.etPermanentRentAmount.text.toString())
                pAddressDetail.stayingInYears = binding.personalAddressLayout.etPermanentStaying.text.toString().toFloat()
                pAddressDetail.address1 = binding.personalAddressLayout.etPermanentAddress.text.toString()
                pAddressDetail.landmark = binding.personalAddressLayout.etPermanentLandmark.text.toString()
                pAddressDetail.zip = binding.personalAddressLayout.customPermanentZipAddressView.pinCode
                pAddressDetail.residenceTypeTypeDetailID = pResidenceType?.typeDetailID
                pAddressDetail.addressTypeDetailID = pAddressProof?.typeDetailID
                pAddressDetail.stateID = binding.personalAddressLayout.customPermanentZipAddressView.getStateId()
                pAddressDetail.districtID = binding.personalAddressLayout.customPermanentZipAddressView.getDistrictId()
                pAddressDetail.cityID = binding.personalAddressLayout.customPermanentZipAddressView.getCityId()
            }
            pAddressDetail.addressTypeDetail = PERMANENT
            if (personalAddressDetail.isNullOrEmpty()) {
                personalAddressDetail?.add(0, cAddressDetail)
                personalAddressDetail?.add(1, pAddressDetail)
            } else {
                personalAddressDetail!![0] = cAddressDetail
                personalAddressDetail!![1] = pAddressDetail
            }
            return personalAddressDetail
        }

        private fun setDatePicker() {
            binding.basicInfoLayout.etDOB.setOnClickListener {
                SelectDOB(mContext, binding.basicInfoLayout.etDOB, binding.basicInfoLayout.etAge)
            }
            binding.etIssueDate.setOnClickListener {
                SelectDate(binding.etIssueDate, mContext)
            }
            binding.etExpiryDate.setOnClickListener {
                SelectDate(binding.etExpiryDate, mContext)
            }
        }

        private fun setClickListeners() {
            binding.ivUploadKyc.setOnClickListener { UploadData(frag, mContext) }
            binding.basicInfoLayout.btnGetOTP.setOnClickListener {
                val mobileNo: String = binding.basicInfoLayout.etMobile.text.toString()
                if (mobileNo.length > 0 && mobileNo.length == 10) {
                    showVerifyOTPDialog()
                } else {
                    showToast(getString(R.string.mandatory_field_missing))
                }

            }
            binding.btnAddKYC.setOnClickListener {
                getKycData()
                clearKycData()
            }
            binding.basicInfoLayout.ivUploadDobProof.setOnClickListener { UploadData(frag, mContext) }
            binding.btnAddApplicant.setOnClickListener { onAddCoApplicantClick() }
            binding.btnPrevious.setOnClickListener { AppEvents.fireEventLoanAppChangeNavFragmentPrevious() }
            binding.btnNext.setOnClickListener {
                if (formValidation.validatePersonalInfo(binding)) {
                    saveCurrentApplicant()
                    presenter.callNetwork(ConstantsApi.CALL_POST_LOAN_APP, dmiConnector = CallPostLoanApp())
                } else {
                    showToast(getString(R.string.mandatory_field_missing))
                }
            }
            binding.personalAddressLayout.cbSameAsCurrent.setOnClickListener {
                if (binding.personalAddressLayout.cbSameAsCurrent.isChecked) {
                    binding.personalAddressLayout.llPermanentAddress.visibility = View.GONE
                } else {
                    binding.personalAddressLayout.llPermanentAddress.visibility = View.VISIBLE
                }
            }
            conversion.convertToCurrencyType(binding.personalAddressLayout.etPermanentRentAmount)
            conversion.convertToCurrencyType(binding.personalAddressLayout.etCurrentRentAmount)
        }

        private fun getPersonalInfoMaster(): PersonalInfoMaster {
            pDraftData.applicantDetails = personalApplicantsList
            personalInfoMaster?.draftData = pDraftData
            personalInfoMaster!!.leadID = leadIdForApplicant.toInt()
            return personalInfoMaster!!
        }

        private fun setCoApplicants() {
            dataBase.provideDataBaseSource().coApplicantsDao().getCoApplicants(mLead!!.leadID!!).observe(viewLifecycleOwner, Observer { coApplicantsMaster ->
                coApplicantsMaster.let {
                    val list = coApplicantsMaster.applicantsList
                    if (list.isNullOrEmpty()) {
                        applicantTab?.add(leadAndLoanDetail.getDefaultApplicant(currentPosition, leadNumber))
                    } else {
                        applicantTab = coApplicantsMaster.applicantsList
                    }

                    binding.rcApplicants.layoutManager = LinearLayoutManager(context,
                            LinearLayoutManager.HORIZONTAL, false)
                    applicantAdapter = ApplicantsAdapter(context!!, applicantTab!!)
                    binding.rcApplicants.adapter = applicantAdapter
                    applicantAdapter!!.setOnItemClickListener(this)
                    applicantAdapter!!.setOnLongClickListener(this)
                }
            })
        }

        private fun onAddCoApplicantClick() {
            when (mLead!!.status) {
                AppEnums.LEAD_TYPE.SUBMITTED.type -> showToast(getString(R.string.error_add_co_applicant))
                else -> if (formValidation.validatePersonalInfo(binding)) {
                    saveCurrentApplicant()

                    try {
                        applicantTab!!.add(leadAndLoanDetail.getDefaultCoApplicant(currentPosition, leadNumber))
                        val lastIndex = applicantTab!!.lastIndex
                        applicantAdapter?.onClickItem(lastIndex, applicantTab!![lastIndex])
                        currentPosition = lastIndex
                        personalApplicantsList!!.add(lastIndex, PersonalApplicantsModel())
                        applicantAdapter!!.notifyDataSetChanged()

                        ClearPersonalForm(binding = binding, context = mContext,
                                masterDropdown = allMasterDropDown, relationshipList = relationshipList)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else showToast(getString(R.string.mandatory_field_missing))
            }
        }

        override fun onApplicantClick(position: Int, coApplicant: CoApplicantsList) {
            when (AppEnums.LEAD_TYPE.SUBMITTED.type) {
                mLead!!.status -> {
                    saveCurrentApplicant()
                    ClearPersonalForm(binding = binding, context = mContext, masterDropdown = allMasterDropDown, relationshipList = relationshipList)
                    currentPosition = position
                    coApplicant.mobile = contactDetail?.mobile
                    waitFor1Sec(position, coApplicant)
                    DisablePersonalForm(binding)
                    EventBus.getDefault().post(coApplicant)
                }
                else -> when {
                    formValidation.validatePersonalInfo(binding) -> {
                        saveCurrentApplicant()
                        ClearPersonalForm(binding = binding, context = mContext, masterDropdown = allMasterDropDown, relationshipList = relationshipList)
                        currentPosition = position
                        coApplicant.mobile = contactDetail?.mobile
                        coApplicant.applicantType = if (position == 0) "Applicant" else "Co-Applicant" + position
                        waitFor1Sec(position, coApplicant)
                        EventBus.getDefault().post(coApplicant)
                    }
                    else -> showToast(getString(R.string.mandatory_field_missing))
                }
            }
        }

        private fun saveCurrentApplicant() {
            if (personalApplicantsList!!.size > 0) {
                personalApplicantsList!![currentPosition] = getCurrentApplicant()
            } else personalApplicantsList!!.add(currentPosition, getCurrentApplicant())
        }

        private fun waitFor1Sec(position: Int, coApplicant: CoApplicantsList) {
            val progress = ProgressDialog(mContext)
            progress.setMessage(getString(R.string.msg_saving))
            progress.setCancelable(false)
            progress.show()
            val handler = Handler()
            handler.postDelayed({
                getParticularApplicantData(position, coApplicant)
                progress.dismiss()
            }, 1000)
            applicantAdapter!!.notifyDataSetChanged()
        }

        private fun getParticularApplicantData(position: Int, coApplicant: CoApplicantsList) {
            currentApplicant = personalApplicantsList!![position]
            currentApplicant!!.isMainApplicant = coApplicant.isMainApplicant
            currentApplicant!!.leadApplicantNumber = coApplicant.leadApplicantNumber
            currentApplicant!!.firstName = coApplicant.firstName
            contactDetail = currentApplicant!!.contactDetail
            personalAddressDetail = currentApplicant!!.addressDetailList
            fillValueInMasterDropDown(currentApplicant!!)
        }

        override fun onApplicantLongClick(position: Int) = showAlertDialog(position)

        private fun showAlertDialog(position: Int) {
            if (mLead!!.status == AppEnums.LEAD_TYPE.SUBMITTED.type || currentPosition == 0) {
                return
            } else {
                val deleteDialogView = LayoutInflater.from(mContext).inflate(R.layout.delete_dialog, null)
                val progressDialog = ProgressDialog(mContext)
                val mBuilder = AlertDialog.Builder(mContext)
                        .setView(deleteDialogView)
                        .setCancelable(false)
                        .setTitle("Delete Applicant")
                val deleteDialog = mBuilder.show()
                deleteDialogView.tvDeleteConfirm.setOnClickListener {
                    progressDialog.setMessage("Deleting Applicant")
                    progressDialog.show()
                    Handler().postDelayed({
                        deleteApplicant(position)
                        deleteDialog.dismiss()
                        progressDialog.dismiss()
                    }, 1000)
                }

                deleteDialogView.tvDonotDelete.setOnClickListener {
                    deleteDialog.dismiss()
                }
            }
        }

        private fun deleteApplicant(deletePosition: Int) {
            applicantTab!!.removeAt(deletePosition)
            applicantAdapter!!.notifyItemRemoved(deletePosition)
            applicantAdapter!!.notifyItemRangeChanged(deletePosition, applicantTab!!.size)
            binding.rcApplicants.adapter!!.notifyDataSetChanged()

            try {
                val currentApplicantPosition = when (personalApplicantsList!!.size > deletePosition) {
                    true -> deletePosition
                    else -> personalApplicantsList!!.size
                }
                currentApplicant = personalApplicantsList!![currentApplicantPosition - 1]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            fillFormWithCurrentApplicant(currentApplicant)
        }

        private fun showKycDetail() {}

        private fun clearKycData() {
            binding.spinnerIdentificationType.isSelected = false
            binding.etIdNum.text?.clear()
            binding.etIssueDate.text?.clear()
            binding.etExpiryDate.text?.clear()
        }

        private fun checkKycDataList() {
            if (kycList.isNullOrEmpty()) {
                binding.rcKYC.visibility = View.GONE
            }
        }

        private val kyc: AddKyc
            get() {
                return AddKyc(expiryDate = binding.etExpiryDate.text.toString(),
                        idNum = binding.etIdNum.text.toString(), kycImage = image,
                        issueDate = binding.etIssueDate.text.toString(),
                        verifiedStatus = binding.spinnerVerifiedStatus.selectedItem.toString(),
                        idType = binding.spinnerIdentificationType.selectedItem.toString())
            }

        private fun getKycData() {
            kycList.add(kyc)
            showKycDetail()
        }

        private var verifyOTPDialogView: View? = null

        private fun showVerifyOTPDialog() {
            verifyOTPDialogView = LayoutInflater.from(mContext).inflate(R.layout.pop_up_verify_otp, null)
            val mBuilder = AlertDialog.Builder(mContext)
                    .setView(verifyOTPDialogView)
                    .setCancelable(false)

            verifyOTPDialog = mBuilder.show()
            val pinEntry = verifyOTPDialogView?.etOTP
            */
/* pinEntry?.setOnPinEnteredListener { pin ->
                 if (pin.toString().length == 4) {
                     otp = pin.toString().toInt()
                     presenter.callNetwork(ConstantsApi.CALL_VERIFY_OTP, CallVerifyOTP())
                 } else {
                     pinEntry.text = null
                  //   verifyOTPPresenter.callNetwork(ConstantsApi.CALL_VERIFY_OTP)
                 } else {
                     pinEntry.text = null
                 }
             }*//*


            verifyOTPDialog.tvMobileno.setText(getString(R.string.mprefix).plus(binding.basicInfoLayout.etMobile.text.toString()))
            verifyOTPDialog.verifyotp.setOnClickListener() {
                val myOtp: String = verifyOTPDialog.etOTP.text.toString()
                if (myOtp.length == 4) {

                    otp = myOtp.toInt()
                    presenter.callNetwork(ConstantsApi.CALL_VERIFY_OTP,CallVerifyOTP())
                } else {
                   // verifyOTPDialog.etOTP.setText("")
                    showToast(getString(R.string.fillotp))

                }


            }

            verifyOTPDialogView?.tvResendOTP?.setOnClickListener { handleResendOtpEvent() }
            verifyOTPDialogView?.ivCross?.setOnClickListener { dismissOtpVerificationDialog() }
            verifyOTPDialogView?.tvResendOTP?.callOnClick()
            timerOtpResend.start()

        }

        private fun handleResendOtpEvent() {
            //  verifyOTPDialogView?.tvResendOTP?.exGone()
            verifyOTPDialogView?.tvResendOTPTimeLeftInfo?.exVisible()
            timerOtpResend.start()
            presenter.callNetwork(ConstantsApi.CALL_SEND_OTP, CallSendOTP())
        }

        private fun handleOtbResendTimerEndEvent() {
            //   verifyOTPDialogView?.tvResendOTP?.exVisible()
            verifyOTPDialogView?.tvResendOTPTimeLeftInfo?.exGone()
            timerOtpResend.cancel()
        }

        private fun dismissOtpVerificationDialog() {
            timerOtpResend.cancel()
            verifyOTPDialog.dismiss()
        }

        private val minutes = 1L
        private val seconds = 60L
        private val millisecond = 1000L

        private val timerOtpResend = object : CountDownTimer(minutes * seconds * millisecond, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsUntilFinish = (millisUntilFinished / millisecond).toInt()
                verifyOTPDialogView?.tvResendOTPTimeLeftInfo?.text = "$secondsUntilFinish ${getString(R.string.seconds)}"
            }

            override fun onFinish() {
                handleOtbResendTimerEndEvent()
            }
        }

        inner class CallGetLoan : ViewGeneric<ArrayList<String>?, Response.ResponseGetLoanApplication>(context = mContext!!) {

            override val apiRequest: ArrayList<String>?
                get() = arrayListOf(mLead!!.leadID.toString(), personalInfoMaster?.storageType!!)

            override fun getApiSuccess(value: Response.ResponseGetLoanApplication) {
                if (value.responseCode == Constants.SUCCESS) {
                    value.responseObj?.let {
                        personalInfoMaster = ResponseConversion().toPersonalMaster(value.responseObj)
                        pDraftData = personalInfoMaster?.draftData!!
                        personalApplicantsList = pDraftData.applicantDetails
                    }
                    setCoApplicants()
                    showData(personalApplicantsList)
                } else getDataFromDB()
            }
        }

        inner class CallPostLoanApp : ViewGeneric<LoanApplicationRequest, Response.ResponseGetLoanApplication>(context = mContext!!) {
            override val apiRequest: LoanApplicationRequest
                get() = RequestConversion().personalInfoRequest(getPersonalInfoMaster())

            override fun getApiSuccess(value: Response.ResponseGetLoanApplication) {
                if (value.responseCode == Constants.SUCCESS) {
                    saveDataToDB(getPersonalInfoMaster())
                    presenter.callNetwork(ConstantsApi.CALL_COAPPLICANTS_LIST, dmiConnector = CallCoApplicantList())
                    AppEvents.fireEventLoanAppChangeNavFragmentNext()
                } else saveDataToDB(getPersonalInfoMaster())
            }
        }

        inner class CallCoApplicantList : ViewGeneric<String, Response.ResponseCoApplicants>(context = mContext) {
            override val apiRequest: String
                get() = mLead!!.leadID.toString()

            override fun getApiSuccess(value: Response.ResponseCoApplicants) {
                if (value.responseCode == Constants.SUCCESS) {
                    saveApplicantToDB(value.responseObj)
                }
            }

            private fun saveApplicantToDB(responseObj: ArrayList<CoApplicantsList>) {
                GlobalScope.launch {
                    val coApplicantMaster = CoApplicantsMaster()
                    coApplicantMaster.applicantsList = responseObj
                    coApplicantMaster.leadID = mLead!!.leadID
                    dataBase.provideDataBaseSource().coApplicantsDao().insertCoApplicants(coApplicantMaster)
                }
            }
        }

        inner class CallSendOTP : ViewGeneric<Requests.RequestSendOTP, Response.ResponseSendOTP>(context = mContext) {
            override val apiRequest: Requests.RequestSendOTP
                get() = otpSendRequest

            private val otpSendRequest: Requests.RequestSendOTP
                get() {
                    val leadId = mLead!!.leadID!!.toInt()
                    val mobile = binding.basicInfoLayout.etMobile.text.toString()
                    return Requests.RequestSendOTP(leadID = leadId, mobile = mobile)
                }

            override fun getApiSuccess(value: Response.ResponseSendOTP) {
                value.responseMsg?.let {
                    showToast(value.responseMsg)
                }
            }
        }

        inner class CallVerifyOTP : ViewGeneric<Requests.RequestVerifyOTP, Response.ResponseVerifyOTP>(context = mContext) {
            override val apiRequest: Requests.RequestVerifyOTP
                get() = otpVerifyRequest

            private val otpVerifyRequest: Requests.RequestVerifyOTP
                get() {
                    val leadId = mLead!!.leadID!!.toInt()
                    val mobile = binding.basicInfoLayout.etMobile.text.toString()
                    return Requests.RequestVerifyOTP(leadID = leadId, mobile = mobile, otpValue = otp!!)
                }

            override fun getApiSuccess(value: Response.ResponseVerifyOTP) {
                if (value.responseCode == Constants.SUCCESS) {
                    dismissOtpVerificationDialog()
                    currentApplicant?.contactDetail!!.isMobileVerified = true
                    binding.basicInfoLayout.etMobile.isEnabled = false
                    binding.basicInfoLayout.btnGetOTP.visibility = View.GONE
                    binding.basicInfoLayout.ivVerifiedStatus.visibility = View.VISIBLE
                }
            }
        }


        override fun onDestroy() {
            super.onDestroy()
            EventBus.getDefault().unregister(this)
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        fun customEventReceived(event: PersonalInfoFragment?) {
            // Toast.makeText(mContext,"custom event",Toast.LENGTH_SHORT)
        }
    }
*/
