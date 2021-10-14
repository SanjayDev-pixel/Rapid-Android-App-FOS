package com.finance.app.view.fragment

class LoanInfoFragment {

}

/*
class LoanInfoFragment : BaseFragment() {

@Inject
lateinit var formValidation: FormValidation
@Inject
lateinit var dataBase: DataBaseUtil
@Inject
lateinit var sharedPreferences: SharedPreferencesUtil
private lateinit var binding: FragmentLoanInformationBinding
private lateinit var appDataViewModel: AppDataViewModel
private val frag: Fragment = this
private lateinit var allMasterDropDown: AllMasterDropDown
private var loanProducts: ArrayList<LoanProductMaster> = ArrayList()
private var selectedLoanProduct: LoanProductMaster? = null
private var mContext: Context? = null
private val presenter = Presenter()
private var mLead: AllLeadMaster? = null
private var empId: String? = null
private var loanMaster: LoanInfoMaster? = LoanInfoMaster()
private var loanInfo: LoanInfoModel? = null
private var mChannelTypeId: String = ""
private lateinit var interestType: CustomSpinnerViewTest<DropdownMaster>
private lateinit var loanScheme: CustomSpinnerViewTest<DropdownMaster>
private lateinit var sourcingPartner: CustomSpinnerViewTest<DropdownMaster>
private lateinit var partnerName: CustomSpinnerViewTest<ChannelPartnerName>
private lateinit var loanProduct: CustomSpinnerViewTest<LoanProductMaster>
private lateinit var loanPurpose: CustomSpinnerViewTest<LoanPurpose>

companion object {
    private lateinit var mBranchId: String
    private const val SELECT_PDF_CODE = 1
    private const val DIRECT = 53
    private const val CLICK_IMAGE_CODE = 2
    private const val SELECT_IMAGE_CODE = 3
    private var image: Bitmap? = null
    private var pdf: Uri? = null
    private val responseConversion = LeadRequestResponseConversion()
    private val requestConversion = RequestConversion()

    fun newInstance(): LoanInfoFragment {
        val args = Bundle()
        val fragment = LoanInfoFragment()
        fragment.arguments = args
        return fragment
    }
}

override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = initBinding(inflater, container, R.layout.fragment_loan_information)
    init()
    return binding.root
}

override fun init() {
    ArchitectureApp.instance.component.inject(this)
    mContext = context!!
    val viewModelFactory: ViewModelProvider.Factory = Injection.provideViewModelFactory(activity!!)
    appDataViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(AppDataViewModel::class.java)
    mLead = sharedPreferences.getLeadDetail()
    mLead?.let {
        getDropDownsFromDB()
        getLoanInfo(mLead?.leadID.toString())
        SetLoanInfoMandatoryField(binding)
        setClickListeners()
    }
}

private fun getLoanInfo(leadId: String) {
    appDataViewModel.getLoanInfo(leadId).observe(this, Observer { loanData ->
        loanData?.let {
            loanMaster = loanData
            loanMaster?.draftData?.let { loanModel ->
                fillFormWithLoanData(loanModel)
            }
        }
    })
//        presenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP, CallGetLoan())
}

private fun showData(loanInfo: LoanInfoModel?) {
    getDropDownsFromDB()
    if (loanInfo != null) {
        fillFormWithLoanData(loanInfo)
    }
}

private fun saveDataToDB(loanInfoMaster: LoanInfoMaster) {
    GlobalScope.launch {
        dataBase.provideDataBaseSource().loanInfoDao().insertLoanInfo(loanInfoMaster)
    }
}

private fun getDropDownsFromDB() {
    appDataViewModel.getAllMasterDropdown().observe(viewLifecycleOwner, Observer { masterDrownDownValues ->
        masterDrownDownValues?.let {
            allMasterDropDown = masterDrownDownValues
            setMasterDropDownValue(allMasterDropDown)
        }
    })

    appDataViewModel.getLoanProductMaster().observe(viewLifecycleOwner, Observer { loanProductValue ->
        loanProductValue?.let {
            val arrayListOfLoanProducts = ArrayList<LoanProductMaster>()
            arrayListOfLoanProducts.addAll(loanProductValue)
            loanProducts = arrayListOfLoanProducts
            setLoanProductDropdown(loanProducts)
        }
    })
}

private fun getDataFromDB() {
    dataBase.provideDataBaseSource().loanInfoDao().getLoanInfo(mLead!!.leadID.toString()).observe(this, Observer { loanInfoMaster ->
        loanInfoMaster?.let {
            loanMaster = it
            loanInfo = loanMaster?.draftData
        }
        showData(loanInfo)
    })
}

private fun setLoanProductDropdown(products: ArrayList<LoanProductMaster>) {
    loanProduct = CustomSpinnerViewTest(context = mContext!!, dropDowns = products, label = "Loan Product *", iSpinnerMainView = object : IspinnerMainView<LoanProductMaster> {
        override fun getSelectedValue(value: LoanProductMaster) {
            setLoanPurposeDropdown(value)
        }
    })
    binding.layoutLoanProduct.addView(loanProduct)

    if (loanInfo != null) {
        loanProduct.setSelection(loanInfo?.productID.toString())
    } else loanProduct.setSelection(mLead?.loanProductID.toString())
}

private fun setLoanPurposeDropdown(loan: LoanProductMaster?) {
    loan?.let {
        binding.layoutLoanPurpose.removeAllViews()
        loanPurpose = CustomSpinnerViewTest(context = mContext!!, dropDowns = loan.loanPurposeList, label = "Loan Purpose")
        binding.layoutLoanPurpose.addView(loanPurpose)
    }

    loanInfo?.loanPurposeID?.let {
        loanPurpose.setSelection(loanInfo?.loanPurposeID.toString())
        loanInfo?.loanPurposeID = null
    }
}

private fun setMasterDropDownValue(allMasterDropDown: AllMasterDropDown) {
    setCustomSpinner(allMasterDropDown)
    loanInfo?.let { selectSpinnerValue() }
    checkSubmission()
}

private fun selectSpinnerValue() {
    interestType.setSelection(loanInfo?.interestTypeTypeDetailID?.toString())
    loanScheme.setSelection(loanInfo?.loanSchemeTypeDetailID?.toString())
    sourcingPartner.setSelection(loanInfo?.sourcingChannelPartnerTypeDetailID?.toString())
}

private fun checkSubmission() {
    if (mLead!!.status == AppEnums.LEAD_TYPE.SUBMITTED.type) {
        DisableLoanInfoForm(binding)
    }
}

private fun setClickListeners() {
    binding.ivUploadForm.setOnClickListener {
        UploadData(frag, mContext!!)
    }
    binding.ivThumbnail.setOnClickListener {
        UploadedFormDataActivity.start(mContext!!, image, pdf)
    }
    binding.btnNext.setOnClickListener {
        if (formValidation.validateLoanInformation(binding, selectedLoanProduct)) {
            checkPropertySelection()
            presenter.callNetwork(ConstantsApi.CALL_POST_LOAN_APP, dmiConnector = CallPostLoanApp())
        } else showToast(getString(R.string.validation_error))
    }

    CurrencyConversion().convertToCurrencyType(binding.etAmountRequest)
}

private fun fillFormWithLoanData(loanInfo: LoanInfoModel) {
    binding.etAmountRequest.setText(loanInfo.loanAmountRequest.toString())
    binding.etEmi.setText(loanInfo.affordableEMI!!.toInt().toString())
    binding.etTenure.setText(loanInfo.tenure!!.toInt().toString())
    binding.cbPropertySelected.isChecked = loanInfo.isPropertySelected!!
}

private fun setCustomSpinner(allMasterDropDown: AllMasterDropDown) {
    interestType = CustomSpinnerViewTest(context = mContext!!, dropDowns = allMasterDropDown.LoanInformationInterestType!!, label = "Interest Type")
    binding.layoutInterestType.addView(interestType)
    loanScheme = CustomSpinnerViewTest(context = mContext!!, dropDowns = allMasterDropDown.LoanScheme!!, label = "Loan Scheme")
    binding.layoutLoanScheme.addView(loanScheme)
    sourcingPartner = CustomSpinnerViewTest(context = mContext!!, dropDowns = allMasterDropDown.SourcingChannelPartner!!, label = "Sourcing Channel Partner *", iSpinnerMainView = object : IspinnerMainView<DropdownMaster> {
        override fun getSelectedValue(value: DropdownMaster) {
            getPartnerNameFromApi(value.getCompareValue())
        }
    })
    binding.layoutSourcingPartner.addView(sourcingPartner)
}

private fun getPartnerNameFromApi(channelId: String) {
    mChannelTypeId = channelId
    mBranchId = sharedPreferences.getLeadDetail()?.branchID!!
    empId = sharedPreferences.getEmpId()
    if (mChannelTypeId.toInt() != DIRECT) {
        presenter.callNetwork(ConstantsApi.CALL_SOURCE_CHANNEL_PARTNER_NAME, CallSourcingPartnerName())
        binding.layoutPartnerName.visibility = View.VISIBLE
    } else {
        presenter.callNetwork(ConstantsApi.CALL_SOURCE_CHANNEL_PARTNER_NAME, CallSourcingPartnerName())
        binding.layoutPartnerName.visibility = View.GONE
    }
}

private fun getLoanInfoObj(): LoanInfoModel {
    val loanInfoObj = LoanInfoModel()
    val sPartner = sourcingPartner.getSelectedValue()
    val cPartnerName = partnerName.getSelectedValue()
    val lProductDD = loanProduct.getSelectedValue()
    val lPurposeDD = loanPurpose.getSelectedValue()
    val lScheme = loanScheme.getSelectedValue()
    val iType = interestType.getSelectedValue()

    loanInfoObj.leadID = mLead!!.leadID!!.toInt()
    loanInfoObj.productID = lProductDD?.productID
    loanInfoObj.salesOfficerEmpID = empId!!.toInt()
    loanInfoObj.loanPurposeID = lPurposeDD?.loanPurposeID
    loanInfoObj.loanSchemeTypeDetailID = lScheme?.typeDetailID
    loanInfoObj.interestTypeTypeDetailID = iType?.typeDetailID
    loanInfoObj.sourcingChannelPartnerTypeDetailID = sPartner?.typeDetailID
    loanInfoObj.isPropertySelected = binding.cbPropertySelected.isChecked
    loanInfoObj.loanAmountRequest = CurrencyConversion().convertToNormalValue(binding.etAmountRequest.text.toString()).toInt()
    loanInfoObj.tenure = binding.etTenure.text.toString().toInt()
    loanInfoObj.channelPartnerDsaID = cPartnerName?.dsaID
    loanInfoObj.affordableEMI = binding.etEmi.text.toString().toDouble()
    return loanInfoObj
}

private fun getLoanInfoMaster(): LoanInfoMaster {
    loanMaster?.draftData = getLoanInfoObj()
    loanMaster?.leadID = mLead!!.leadID!!.toInt()
    return loanMaster!!
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == RESULT_OK) {
        val returnUri = data!!.data
        when (requestCode) {
            SELECT_PDF_CODE -> {
                Log.i("URI: ", returnUri?.toString())
                pdf = returnUri
                binding.ivThumbnail.visibility = View.GONE
                binding.ivPdf.visibility = View.VISIBLE
            }
            SELECT_IMAGE_CODE -> {
                val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, returnUri)
                image = bitmap
                binding.ivThumbnail.setImageBitmap(bitmap)
            }
            CLICK_IMAGE_CODE -> {
                val thumbnail = data.extras!!.get("data") as Bitmap
                image = thumbnail
                binding.ivThumbnail.setImageBitmap(thumbnail)
            }
        }
    }
}

private fun checkPropertySelection() {
    if (binding.cbPropertySelected.isChecked) {
        sharedPreferences.setPropertySelection("Yes")
    } else {
        sharedPreferences.setPropertySelection("No")
    }
}

inner class CallPostLoanApp : ViewGeneric<LoanApplicationRequest, Response.ResponseGetLoanApplication>(context = mContext!!) {
    override val apiRequest: LoanApplicationRequest
        get() = requestConversion.loanInfoRequest(getLoanInfoMaster())

    override fun getApiSuccess(value: Response.ResponseGetLoanApplication) {
        if (value.responseCode == Constants.SUCCESS) {
            saveDataToDB(getLoanInfoMaster())
            AppEvents.fireEventLoanAppChangeNavFragmentNext()
        } else saveDataToDB(getLoanInfoMaster())
    }
}

inner class CallSourcingPartnerName : ViewGeneric<ArrayList<String>, Response.ResponseSourceChannelPartnerName>(context = mContext!!) {
    override val apiRequest: ArrayList<String>
        get() = arrayListOf(mBranchId, mChannelTypeId, empId!!)

    override fun getApiSuccess(value: Response.ResponseSourceChannelPartnerName) {
        if (value.responseCode == Constants.SUCCESS) {
            binding.layoutPartnerName.removeAllViews()
            setChannelPartnerNameDropDown(value.responseObj)
        }
    }

    private fun setChannelPartnerNameDropDown(channelPartners: ArrayList<ChannelPartnerName>?) {
        partnerName = CustomSpinnerViewTest(context = mContext!!, dropDowns = channelPartners, label = "Channel Partner Name")
        binding.layoutPartnerName.addView(partnerName)

        loanInfo?.channelPartnerDsaID?.let {
            partnerName.setSelection(loanInfo!!.channelPartnerDsaID.toString())
            loanInfo?.channelPartnerDsaID = null
        }
    }
}

}

 */
