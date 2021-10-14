package com.finance.app.view.fragment

import motobeans.architecture.customAppComponents.activity.BaseFragment

class BankDetailFragment() : BaseFragment() {
    override fun init() {

    }

}

//class BankDetailFragment : BaseFragment(), LoanApplicationConnector.PostLoanApp,
//        LoanApplicationConnector.GetLoanApp, ApplicantsAdapter.ItemClickListener,
//        BankDetailAdapter.ItemClickListener, BankDetailDialogFragment.OnBankDetailDialogCallback {
//
//    @Inject
//    lateinit var sharedPreferences: SharedPreferencesUtil
//    @Inject
//    lateinit var formValidation: FormValidation
//    @Inject
//    lateinit var dataBase: DataBaseUtil
//
//    private lateinit var binding: FragmentBankDetailBinding
//    private lateinit var mContext: Context
//    private lateinit var allMasterDropDown: AllMasterDropDown
//
//    private var mLead: AllLeadMaster? = null
//    private val loanAppGetPresenter = LoanAppGetPresenter(GetLoanApp = this)
//    private val loanAppPostPresenter = LoanAppPostPresenter(this)
//
//    private var applicantAdapter: ApplicantsAdapter? = null
//    private var bankAdapter: BankDetailAdapter? = null
//
//    private lateinit var appDataViewModel: AppDataViewModel
//
//    private var bankDetailMasterResponse: BankDetailMaster = BankDetailMaster()
//    private var applicantsBankDetailList: ArrayList<BankDetailModel>? = ArrayList()
//
//    private var selectedApplicant: CoApplicantsList? = null
//    private var selectedTabPosition = 0
//    private var selectedBankDetailPosition = -1
//
//    override val leadId: String
//        get() = mLead!!.leadID.toString()
//
//    override val storageType: String
//        get() = bankDetailMasterResponse.storageType
//
//    override val loanAppRequestPost: LoanApplicationRequest
//        get() = requestConversion.bankRequest(getBankDetailMaster())
//
//    companion object {
//        private val leadAndLoanDetail = LeadAndLoanDetail()
////        private val responseConversion = ResponseConversion()
////        private val requestConversion = RequestConversion()
//    }
//
//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//        mContext = context!!
//        ArchitectureApp.instance.component.inject(this)
//        mLead = sharedPreferences.getLeadDetail()
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        binding = initBinding(inflater, container, R.layout.fragment_bank_detail)
//
//        initViews()
//        setOnClickListeners()
//
//        return binding.root
//    }
//
//    override fun init() {
//    }
//
//    private fun initViews() {
//        //Set Applicant Tabs View.
//        setApplicantTabView()
//    }
//
//    private fun setApplicantTabView() {
//        dataBase.provideDataBaseSource().coApplicantsDao().getCoApplicants(leadId.toInt()).observe(viewLifecycleOwner, Observer { coApplicantsMaster ->
//            coApplicantsMaster?.let {
//                var applicantTabList: ArrayList<CoApplicantsList>? = ArrayList()
//                if (coApplicantsMaster.applicantsList.isNullOrEmpty()) {
//                    applicantTabList?.add(leadAndLoanDetail.getDefaultApplicant(selectedTabPosition, mLead!!.leadNumber!!))
//                } else {
//                    applicantTabList = coApplicantsMaster.applicantsList
//                }
//                //Set Applicant Tab Adapter...
//                applicantTabList?.let { setApplicantTabAdapter(it) }
//            }
//        })
//    }
//
//    private fun setApplicantTabAdapter(applicantTabList: ArrayList<CoApplicantsList>) {
//        binding.rcApplicants.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        applicantAdapter = ApplicantsAdapter(mContext, applicantTabList)
//        binding.rcApplicants.adapter = applicantAdapter
//        applicantAdapter?.setOnItemClickListener(this)
//        selectedApplicant = applicantTabList[0]
//    }
//
//    private fun setBankDetailAdapter(bankDetailList: ArrayList<BankDetailBean>) {
//        binding.rcBank.layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
//        bankAdapter = BankDetailAdapter(mContext, bankDetailList)
//        binding.rcBank.adapter = bankAdapter
//        bankAdapter?.setOnItemClickListener(this)
//        binding.pageIndicatorAsset.attachTo(binding.rcBank)
//        binding.pageIndicatorAsset.visibility = View.VISIBLE
//        binding.rcBank.visibility = View.VISIBLE
//    }
//
//    private fun setOnClickListeners() {
//        binding.vwAdd.setOnClickListener { showBankDetailFormDialog(BankDetailDialogFragment.Action.NEW) }
//        binding.btnNext.setOnClickListener { loanAppPostPresenter.callNetwork(ConstantsApi.CALL_POST_LOAN_APP) }
//        binding.btnPrevious.setOnClickListener { AppEvents.fireEventLoanAppChangeNavFragmentPrevious() }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        //Now fetch data from where-ever you want....
//        fetchSpinnersDataFromDB()
//        fetchBankDetail()
//    }
//
//    private fun refreshApplicantBankDetails(allApplicantsBankDetailList: ArrayList<BankDetailModel>?) {
//        allApplicantsBankDetailList?.let { mainList ->
//            val selectedApplicantBankDetailList = mainList.filter { it.leadApplicantNumber.equals(selectedApplicant?.leadApplicantNumber, true) }
//            selectedApplicantBankDetailList?.let { childList ->
//                if (childList.isNotEmpty()) {
//                    //Always have one item in the list after filtering, for now as developer knows....
//                    setBankDetailAdapter(childList[0].applicantBankDetailsBean)
//                }
//            }
//        }
//    }
//
//    private fun fetchSpinnersDataFromDB() {
////        appDataViewModel.getAllMasterDropdown().observe(viewLifecycleOwner, Observer { masterDrownDownValues ->
////            masterDrownDownValues?.let {
////                allMasterDropDown = it
////            }
////        })
//
//        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(viewLifecycleOwner, Observer { masterDrownDownValues ->
//            masterDrownDownValues?.let {
//                allMasterDropDown = it
//            }
//        })
//    }
//
//    private fun fetchBankDetail() {
//        loanAppGetPresenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP)
//    }
//
//    private fun fetchBankDetailsFromDB() {
//        mLead?.leadID?.let {
//            appDataViewModel.getLeadData(it).observe(viewLifecycleOwner, Observer { loanProductValue ->
//                loanProductValue?.let { loanDetails ->
//                    refreshApplicantBankDetails(loanDetails.bankData.applicantBankDetails)
//                }
//            })
//        }
//
////        dataBase.provideDataBaseSource().bankDetailDao().getBankDetail(leadId).observe(this, Observer { bankInfo ->
////            bankInfo?.let {
////                bankDetailMasterResponse = bankInfo
////                applicantsBankDetailList = bankDetailMasterResponse.draftData?.applicantBankDetails
////                //Now inflate data to views...
////                refreshApplicantBankDetails(applicantsBankDetailList)
////            }
////        })
//    }
//
//    override fun getLoanAppGetSuccess(value: Response.ResponseGetLoanApplication) {
//        value.responseObj?.let {
//            bankDetailMasterResponse = responseConversion.toBankDetailMaster(value.responseObj)
//            applicantsBankDetailList = bankDetailMasterResponse.draftData?.applicantBankDetails
//            //Now inflate data to views...
//            refreshApplicantBankDetails(applicantsBankDetailList)
//        }
//    }
//
//    override fun getLoanAppGetFailure(msg: String) = fetchBankDetailsFromDB()
//
//    override fun onApplicantClick(position: Int, coApplicant: CoApplicantsList) {
//        selectedApplicant = coApplicant
//        selectedTabPosition = position
//        refreshApplicantBankDetails(applicantsBankDetailList)
//        applicantAdapter?.notifyDataSetChanged() //TODO need to change this approach...
//    }
//
//    private fun getBankDetailMaster(): BankDetailList {
//        return BankDetailList().apply {
//            draftData = BankDetailList().apply { applicantBankDetails = applicantsBankDetailList }
//            leadID = leadId.toInt()
//        }
//    }
//
//    override fun getLoanAppPostFailure(msg: String) {
//        saveDataToDB(getBankDetailMaster())
//        showToast(msg)
//    }
//
//    override fun getLoanAppPostSuccess(value: Response.ResponseGetLoanApplication) {
//        saveDataToDB(getBankDetailMaster())
//        AppEvents.fireEventLoanAppChangeNavFragmentNext()
//    }
//
//    private fun saveDataToDB(bankDetail: BankDetailMaster) {
//        GlobalScope.launch {
//            dataBase.provideDataBaseSource().bankDetailDao().insertBankDetail(bankDetail)
//        }
//    }
//
//    override fun onBankDetailDeleteClicked(position: Int) {
//        selectedBankDetailPosition = position
//        showBankDetailConfirmDeleteDialog()
//    }
//
//    override fun onBankDetailEditClicked(position: Int, bank: BankDetailBean) {
//        selectedBankDetailPosition = position
//        showBankDetailFormDialog(
//                if (mLead?.status == AppEnums.LEAD_TYPE.SUBMITTED.type) BankDetailDialogFragment.Action.SUBMITTED
//                else BankDetailDialogFragment.Action.EDIT, bank)
//    }
//
//    private fun showBankDetailFormDialog(action: BankDetailDialogFragment.Action, bankDetail: BankDetailBean? = null) {
//        BankDetailDialogFragment.newInstance(action, this@BankDetailFragment, allMasterDropDown, bankDetail).show(fragmentManager, "Bank Detail")
//    }
//
//    private fun showBankDetailConfirmDeleteDialog() {
//        val deleteDialogView = LayoutInflater.from(activity).inflate(R.layout.delete_dialog, null)
//        val mBuilder = AlertDialog.Builder(mContext)
//                .setView(deleteDialogView)
//                .setTitle("Delete Bank Detail")
//        val deleteDialog = mBuilder.show()
//        deleteDialogView.tvDeleteConfirm.setOnClickListener {
//            onDeleteBankDetail()
//            deleteDialog.dismiss()
//        }
//        deleteDialogView.tvDonotDelete.setOnClickListener { deleteDialog.dismiss() }
//    }
//
//    override fun onSaveBankDetail(bankDetail: BankDetailBean) {
//        bankAdapter?.addItem(bankDetail = bankDetail)
//    }
//
//    override fun onEditBankDetail(bankDetail: BankDetailBean) {
//        bankAdapter?.updateItem(selectedBankDetailPosition, bankDetail)
//    }
//
//    private fun onDeleteBankDetail() {
//        bankAdapter?.deleteItem(selectedBankDetailPosition)
//    }
//}
