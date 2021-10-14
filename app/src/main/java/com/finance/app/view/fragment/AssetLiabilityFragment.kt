////package com.finance.app.view.fragment
////
////import android.app.AlertDialog
////import android.app.Dialog
////import android.app.ProgressDialog
////import android.content.Context
////import android.os.Bundle
////import android.os.Handler
////import android.util.Log
////import android.view.LayoutInflater
////import android.view.MenuItem
////import android.view.View
////import android.view.ViewGroup
////import android.widget.Spinner
////import androidx.core.view.isVisible
////import androidx.databinding.DataBindingUtil
////import androidx.lifecycle.Observer
////import androidx.recyclerview.widget.LinearLayoutManager
////import androidx.recyclerview.widget.RecyclerView
////import com.finance.app.R
////import com.finance.app.databinding.*
////import com.finance.app.eventBusModel.AppEvents
////import com.finance.app.persistence.model.*
////import com.finance.app.presenter.connector.LoanApplicationConnector
////import com.finance.app.presenter.presenter.LoanAppGetPresenter
////import com.finance.app.presenter.presenter.LoanAppPostPresenter
////import com.finance.app.utility.*
////import com.finance.app.view.adapters.recycler.adapter.ApplicantsAdapter
////import com.finance.app.view.adapters.recycler.adapter.AssetDetailAdapter
////import com.finance.app.view.adapters.recycler.adapter.CardDetailAdapter
////import com.finance.app.view.adapters.recycler.adapter.ObligationAdapter
////import com.finance.app.view.adapters.recycler.spinner.MasterSpinnerAdapter
////import kotlinx.android.synthetic.main.add_assests_dialog.cancel_bttn
////import kotlinx.android.synthetic.main.add_obligation_dialog.*
////import kotlinx.android.synthetic.main.asset_creditcard_dialog.*
////import kotlinx.android.synthetic.main.delete_dialog.view.*
////import kotlinx.coroutines.GlobalScope
////import kotlinx.coroutines.launch
////import motobeans.architecture.application.ArchitectureApp
////import motobeans.architecture.constants.ConstantsApi
////import motobeans.architecture.customAppComponents.activity.BaseFragment
////import motobeans.architecture.development.interfaces.DataBaseUtil
////import motobeans.architecture.development.interfaces.FormValidation
////import motobeans.architecture.development.interfaces.SharedPreferencesUtil
////import motobeans.architecture.retrofit.response.Response
////import org.greenrobot.eventbus.EventBus
////import org.greenrobot.eventbus.Subscribe
////import org.greenrobot.eventbus.ThreadMode
////import javax.inject.Inject
////
/////*class AssetLiabilityFragment {
////
////}*/
////
////
//class AssetLiabilityFragment : BaseFragment(), LoanApplicationConnector.PostLoanApp,
//        LoanApplicationConnector.GetLoanApp, ApplicantsAdapter.ItemClickListener,
//        AssetDetailAdapter.AssetClickListener, CardDetailAdapter.CardClickListener,
//        ObligationAdapter.ObligationClickListener {
//
//    @Inject
//    lateinit var sharedPreferences: SharedPreferencesUtil
//    @Inject
//    lateinit var formValidation: FormValidation
//    @Inject
//    lateinit var dataBase: DataBaseUtil
//    private lateinit var binding: FragmentAssetLiablityBinding
//    private lateinit var mContext: Context
//    private var mLead: AllLeadMaster? = null
//    private lateinit var allMasterDropDown: AllMasterDropDown
//    private val loanAppPostPresenter = LoanAppPostPresenter(this)
//    private val loanAppGetPresenter = LoanAppGetPresenter(this)
//    private var applicantAdapter: ApplicantsAdapter? = null
//    private lateinit var assetAdapter: AssetDetailAdapter
//    private lateinit var cardDetailAdapter: CardDetailAdapter
//    private lateinit var obligationAdapter: ObligationAdapter
//    private var applicantTab: ArrayList<CoApplicantsList>? = ArrayList()
//    private var assetLiabilityMaster: AssetLiabilityMaster = AssetLiabilityMaster()
//    private var aApplicantList: ArrayList<AssetLiabilityModel>? = ArrayList()
//    private var currentApplicant: AssetLiabilityModel = AssetLiabilityModel()
//    private var aDraftData = AssetLiabilityList()
//    private var assetsList: ArrayList<AssetLiability>? = ArrayList()
//    private var currentAsset: AssetLiability? = AssetLiability()
//    private var cardDetailList: ArrayList<CardDetail>? = ArrayList()
//    private var currentCardDetail: CardDetail? = CardDetail()
//    private var obligationsList: ArrayList<ObligationDetail>? = ArrayList()
//    private var currentObligation: ObligationDetail? = ObligationDetail()
//    private var currentPosition = 0
//    private lateinit var currentTab: CoApplicantsList
//    private lateinit var deleteDialog: Dialog
//    private lateinit var addAssestsDialog: Dialog
//    private lateinit var addCreditCardDialog: Dialog
//    private lateinit var addObligationDialog: Dialog
//    private lateinit var obligationItemDetailDialog: Dialog
//    var obligationCounter:Int=0
//
//    companion object {
//        private val leadAndLoanDetail = LeadAndLoanDetail()
//        private val responseConversion = ResponseConversion()
//        private val requestConversion = RequestConversion()
//        const val ASSET = 1
//        const val CARD = 2
//        const val OBLIGATION = 3
//    }
//
//    fun onCreate() {
//        //super.onCreate()
//        EventBus.getDefault().register(this)
//    }
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        binding = initBinding(inflater, container, R.layout.fragment_asset_liablity)
//        init()
//        return binding.root
//    }
//
//    override fun init() {
//        ArchitectureApp.instance.component.inject(this)
//        mContext = context!!
//        setClickListeners()
//        getAssetLiabilityInfo()
//        SetAssetLiabilityMandatoryField(binding)
//        setDatePicker()
//        setClickListeners()
////        checkIncomeConsideration()
//    }
//
//    private fun checkIncomeConsideration() {
//    }
//
//    private fun getAssetLiabilityInfo() {
//
//        mLead = sharedPreferences.getLeadDetail()
//        loanAppGetPresenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP)
//    }
//
//    override val leadId: String
//        get() = mLead!!.leadID.toString()
//
//    override val storageType: String
//        get() = assetLiabilityMaster.storageType
//
//    override fun getLoanAppGetFailure(msg: String) = getDataFromDB()
//
//    override fun getLoanAppGetSuccess(value: Response.ResponseGetLoanApplication) {
//        value.responseObj?.let {
//            assetLiabilityMaster = responseConversion.toAssetLiabilityMaster(value.responseObj)
//            aDraftData = assetLiabilityMaster.draftData!!
//            aApplicantList = aDraftData.applicantDetails
//        }
//        setCoApplicants()
//        showData(aApplicantList)
//    }
//
//    private fun setCoApplicants() {
//        dataBase.provideDataBaseSource().coApplicantsDao().getCoApplicants(mLead!!.leadID!!).observe(viewLifecycleOwner, Observer { coApplicantsMaster ->
//            coApplicantsMaster.let {
//                if (coApplicantsMaster.applicantsList!!.isEmpty()) {
//                    applicantTab?.add(leadAndLoanDetail.getDefaultApplicant(currentPosition, mLead!!.leadNumber!!))
//                } else {
//                    applicantTab = coApplicantsMaster.applicantsList
//                }
//
//                binding.rcApplicants.layoutManager = LinearLayoutManager(context,
//                        LinearLayoutManager.HORIZONTAL, false)
//                applicantAdapter = ApplicantsAdapter(context!!, applicantTab!!)
//                binding.rcApplicants.adapter = applicantAdapter
//                applicantAdapter!!.setOnItemClickListener(this)
//            }
//        })
//    }
//
//    override fun onApplicantClick(position: Int, coApplicant: CoApplicantsList) {
//        //if (formValidation.validateAssetLiabilityForm(binding)) {
//            saveCurrentApplicant()
//            ClearAssetLiabilityForm(binding, mContext, allMasterDropDown)
//            currentPosition = position
//            currentTab = coApplicant
//            waitFor1Sec(position)
//        EventBus.getDefault().post(coApplicant)
//       // } else showToast(getString(R.string.mandatory_field_missing))
//    }
//
//    private fun waitFor1Sec(position: Int) {
//        val progress = ProgressDialog(mContext)
//        progress.setMessage(getString(R.string.msg_saving))
//        progress.setCancelable(false)
//        progress.show()
//        val handler = Handler()
//        handler.postDelayed({
//            getParticularApplicantData(position)
//            progress.dismiss()
//        }, 1000)
//        applicantAdapter!!.notifyDataSetChanged()
//    }
//
//    private fun getParticularApplicantData(position: Int) {
//
//        currentApplicant = aApplicantList!![position]
//
//
//    }
//
//    private fun showData(applicantList: ArrayList<AssetLiabilityModel>?) {
//        if (applicantList != null) {
//            if (applicantList.size < applicantTab!!.size) {
//                for (tab in applicantList.size..applicantTab!!.size) {
//                    applicantList.add(AssetLiabilityModel())
//                }
//            }
//            for (applicant in applicantList) {
//                if (applicant.isMainApplicant) {
//                    currentApplicant = applicant
//                }
//            }
//        }
//        setUpCurrentApplicantDetails(currentApplicant)
//        getDropDownsFromDB()
//    }
//
//    private fun getDropDownsFromDB() {
//        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(viewLifecycleOwner, Observer { masterDrownDownValues ->
//            masterDrownDownValues.let {
//                allMasterDropDown = masterDrownDownValues
//                setMasterDropDownValue(allMasterDropDown)
//            }
//        })
//    }
//
//    private fun setMasterDropDownValue(allMasterDropDown: AllMasterDropDown?) {
//        binding.spinnerDocumentProof.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown!!.DocumentProof!!)
//        binding.spinnerAssetSubType.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.AssetSubType!!)
//        binding.spinnerAssetType.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.AssetDetail!!)
//        binding.spinnerOwnership.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.AssetOwnership!!)
//        binding.layoutCreditCard.spinnerBankName.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.BankName!!)
//        binding.layoutCreditCard.spinnerObligate.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.CreditCardObligation!!)
//        binding.layoutObligations.spinnerObligate.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.Obligate!!)
//        binding.layoutObligations.spinnerLoanOwnership.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.LoanOwnership!!)
//        binding.layoutObligations.spinnerLoanType.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.LoanType!!)
//        binding.layoutObligations.spinnerRepaymentBank.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.RepaymentBank!!)
//        binding.layoutObligations.spinnerEmiPaidInSameMonth.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.BounceEmiPaidInSameMonth!!)
//    }
//
//    private fun setUpCurrentApplicantDetails(currentApplicant: AssetLiabilityModel) {
//        assetsList = currentApplicant.applicantAssetLiabilityList
//        cardDetailList = currentApplicant.applicantCreditCardDetailList
//        obligationsList = currentApplicant.applicantExistingObligationList
//        setUpAssetAdapter(assetsList!!)
//        setUpCardDetailAdapter(cardDetailList!!)
//        setUpObligationAdapter(obligationsList!!)
//
//    }
//
//    private fun setUpAssetAdapter(assets: ArrayList<AssetLiability>) {
//        binding.rcAsset.layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
//        assetAdapter = AssetDetailAdapter(mContext, assets)
//        binding.rcAsset.adapter = assetAdapter
//        assetAdapter.setOnAssetClickListener(this)
//        binding.pageIndicatorAsset.attachTo(binding.rcAsset)
//      //  binding.pageIndicatorAsset.visibility = View.VISIBLE
//      //  binding.rcAsset.visibility = View.VISIBLE
//        binding.assetcounter.setText(assets.size.toString())
//    }
//
//    private fun setUpCardDetailAdapter(cards: ArrayList<CardDetail>) {
//        binding.layoutCreditCard.rcCreditCard.layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
//        cardDetailAdapter = CardDetailAdapter(mContext, cards)
//        binding.layoutCreditCard.rcCreditCard.adapter = cardDetailAdapter
//        cardDetailAdapter.setOnCardClickListener(this)
//        binding.layoutCreditCard.pageIndicatorCreditCard.attachTo(binding.layoutCreditCard.rcCreditCard)
//       // binding.pageIndicatorAsset.visibility = View.VISIBLE
//        //binding.layoutCreditCard.rcCreditCard.visibility = View.VISIBLE
//        binding.layoutCreditCard.creditcardcounter.setText(cards.size.toString())
//
//    }
//
//    private fun setUpObligationAdapter(obligations: ArrayList<ObligationDetail>) {
//        binding.layoutObligations.rcObligation.layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
//        obligationAdapter = ObligationAdapter(mContext, obligations)
//        binding.layoutObligations.rcObligation.adapter = obligationAdapter
//        obligationAdapter.setOnObligationClickListener(this)
//        binding.layoutObligations.pageIndicatorObligation.attachTo(binding.layoutObligations.rcObligation)
//       // binding.pageIndicatorAsset.visibility = View.VISIBLE
//       // binding.layoutObligations.rcObligation.visibility = View.VISIBLE
//        binding.layoutObligations.obligationcounter.setText(obligations.size.toString())
//
//
//    }
//
//    private fun selectMasterDropdownValue(spinner: Spinner, id: Int?) {
//        for (index in 0 until spinner.count - 1) {
//            val obj = spinner.getItemAtPosition(index) as DropdownMaster
//            if (obj.typeDetailID == id) {
//                spinner.setSelection(index + 1)
//                return
//            }
//        }
//    }
//
//    private fun setDatePicker() {
//        binding.layoutCreditCard.etLastPaymentDate.setOnClickListener {
//            SelectDate(binding.layoutCreditCard.etLastPaymentDate, mContext)
//        }
//        binding.layoutObligations.etDisbursementDate.setOnClickListener {
//            SelectDate(binding.layoutObligations.etDisbursementDate, mContext)
//        }
//    }
//
//    private fun setClickListeners() {
//        assetFormListeners(binding)
//        cardDetailFormListeners(binding.layoutCreditCard)
//        obligationFormListeners(binding.layoutObligations)
//        binding.btnPrevious.setOnClickListener { AppEvents.fireEventLoanAppChangeNavFragmentPrevious() }
//        binding.btnNext.setOnClickListener {
//            if (assetsList!!.size > 0
//                    || cardDetailList!!.size > 0 || obligationsList!!.size > 0) {
//                saveCurrentApplicant()
//                loanAppPostPresenter.callNetwork(ConstantsApi.CALL_POST_LOAN_APP)
//            } else showToast(getString(R.string.validation_error))
//        }
//    }
//
//    private fun obligationFormListeners(bindingObligation: LayoutObligationBinding) {
//        bindingObligation.collapseForm.setOnClickListener {
//            bindingObligation.formObligation.visibility = View.GONE
//            bindingObligation.collapseForm.visibility = View.GONE
//            bindingObligation.expandForm.visibility = View.VISIBLE
//
//            showDialogtoAddObligation()
//        }
//        bindingObligation.expandForm.setOnClickListener {
//            bindingObligation.expandForm.visibility = View.GONE
//            bindingObligation.formObligation.visibility = View.VISIBLE
//            bindingObligation.collapseForm.visibility = View.VISIBLE
//            showDialogtoAddObligation()
//        }
//        CurrencyConversion().convertToCurrencyType(binding.layoutObligations.etLoanAmount)
//        CurrencyConversion().convertToCurrencyType(binding.layoutObligations.etEmiAmount)
//        bindingObligation.btnAddObligation.setOnClickListener {
//            if (formValidation.validateObligations(bindingObligation)) {
//                saveCurrentObligations()
//                ClearAssetLiabilityForm(binding, mContext, allMasterDropDown).clearObligationForm(bindingObligation)
//            } else showToast(getString(R.string.validation_error))
//        }
//    }
//
//    private fun cardDetailFormListeners(bindingCardDetail: LayoutCreditCardDetailsBinding) {
//        bindingCardDetail.collapseForm.setOnClickListener {
//            bindingCardDetail.formCreditCard.visibility = View.GONE
//            bindingCardDetail.collapseForm.visibility = View.GONE
//            bindingCardDetail.expandForm.visibility = View.VISIBLE
//
//        }
//        bindingCardDetail.expandForm.setOnClickListener {
//            bindingCardDetail.expandForm.visibility = View.GONE
//            bindingCardDetail.collapseForm.visibility = View.VISIBLE
//            bindingCardDetail.formCreditCard.visibility = View.VISIBLE
//
//
//        }
//        bindingCardDetail.btnAddCreditCard.setOnClickListener {
//            if (formValidation.validateCards(bindingCardDetail)) {
//                saveCurrentCardDetails()
//                ClearAssetLiabilityForm(binding, mContext, allMasterDropDown).clearCardForm(bindingCardDetail)
//            } else showToast(getString(R.string.validation_error))
//        }
//    }
//
//    private fun assetFormListeners(binding: FragmentAssetLiablityBinding) {
//        binding.collapseForm.setOnClickListener {
//            binding.llAssetDetail.visibility = View.GONE
//            binding.collapseForm.visibility = View.GONE
//            binding.expandForm.visibility = View.VISIBLE
//
//
//        }
//        binding.expandForm.setOnClickListener {
//            binding.expandForm.visibility = View.GONE
//            binding.collapseForm.visibility = View.VISIBLE
//            binding.llAssetDetail.visibility = View.VISIBLE
//        }
//
//        binding.btnAddAsset.setOnClickListener {
//            if (formValidation.validateAssets(binding)) {
//                saveCurrentAsset()
//                ClearAssetLiabilityForm(binding, mContext, allMasterDropDown).clearAssetForm(binding)
//            } else showToast(getString(R.string.validation_error))
//        }
//        CurrencyConversion().convertToCurrencyType(binding.etValue)
//
//        // changes due t0 new Design
//        binding.addasset.setOnClickListener() {
//
//            //show dialog
//            showDialogtoAddAssests()
//
//        }
//
//        binding.layoutCreditCard.addcreditdilaog.setOnClickListener(){
//
//            showDialogtoAddCreditCard()
//        }
//
//        binding.layoutObligations.addcreditdilaog.setOnClickListener(){
//
//            showDialogtoAddObligation()
//
//        }
//
//        binding.assetcounter.setOnClickListener() {
//            binding.llAssetDetail.visibility = View.VISIBLE
//            binding.pageIndicatorAsset.visibility = View.VISIBLE
//            binding.rcAsset.visibility = View.VISIBLE
//
//            binding.layoutObligations.pageIndicatorObligation.visibility=View.GONE
//            binding.layoutObligations.rcObligation.visibility = View.GONE
//
//            binding.layoutCreditCard.pageIndicatorCreditCard.visibility = View.GONE
//            binding.layoutCreditCard.rcCreditCard.visibility = View.GONE
//        }
//
//        binding.layoutObligations.obligationcounter.setOnClickListener(){
//            binding.layoutObligations.pageIndicatorObligation.visibility=View.VISIBLE
//            binding.layoutObligations.rcObligation.visibility = View.VISIBLE
//
//
//            binding.layoutCreditCard.pageIndicatorCreditCard.visibility = View.GONE
//            binding.layoutCreditCard.rcCreditCard.visibility = View.GONE
//            binding.pageIndicatorAsset.visibility = View.GONE
//            binding.rcAsset.visibility = View.GONE
//        }
//        binding.layoutCreditCard.creditcardcounter.setOnClickListener(){
//
//                binding.layoutCreditCard.rcCreditCard.visibility = View.VISIBLE
//                binding.layoutCreditCard.pageIndicatorCreditCard.visibility = View.VISIBLE
//
//
//                binding.pageIndicatorAsset.visibility = View.GONE
//                binding.rcAsset.visibility = View.GONE
//
//                binding.layoutObligations.pageIndicatorObligation.visibility=View.GONE
//                binding.layoutObligations.rcObligation.visibility = View.GONE
//
//        }
//
//    }
//
//
//    private fun saveCurrentObligations() {
//        obligationsList?.add(getCurrentObligation())
//        obligationAdapter.notifyDataSetChanged()
//    }
//
//    private fun saveCurrentCardDetails() {
//        cardDetailList?.add(getCurrentCardDetail())
//        cardDetailAdapter.notifyDataSetChanged()
//    }
//
//    private fun saveCurrentAsset() {
//        assetsList?.add(getCurrentAsset())
//        assetAdapter.notifyDataSetChanged()
//    }
//
//
//    private fun saveCurrentApplicant() {
//        if (aApplicantList!!.size > 0) {
//            aApplicantList!![currentPosition] = getCurrentApplicant()
//        } else aApplicantList!!.add(currentPosition, getCurrentApplicant())
//    }
//
//    private fun getCurrentApplicant(): AssetLiabilityModel {
//        val currentApplicant = AssetLiabilityModel()
//        currentApplicant.isMainApplicant = currentPosition == 0
//        currentApplicant.leadApplicantNumber =leadAndLoanDetail.getLeadApplicantNum(currentPosition + 1, mLead!!.leadNumber!!)
//        currentApplicant.applicantAssetLiabilityList = assetsList
//        currentApplicant.applicantCreditCardDetailList = cardDetailList
//        currentApplicant.applicantExistingObligationList = obligationsList
//        return currentApplicant
//    }
//
//    private fun getDataFromDB() {
//        dataBase.provideDataBaseSource().assetLiabilityDao().getAssetLiability(leadId).observe(this, Observer { assetInfo ->
//            assetInfo?.let {
//                assetLiabilityMaster = assetInfo
//                aDraftData = assetLiabilityMaster.draftData!!
//                aApplicantList = aDraftData.applicantDetails
//            }
//            setCoApplicants()
//            showData(aApplicantList)
//        })
//    }
//
//    private fun getAssetLiabilityMaster(): AssetLiabilityMaster {
//        aDraftData.applicantDetails = aApplicantList
//        assetLiabilityMaster.draftData = aDraftData
//        assetLiabilityMaster.leadID = leadId.toInt()
//        return assetLiabilityMaster
//    }
//
//    override val loanAppRequestPost: LoanApplicationRequest
//        get() = requestConversion.assetLiabilityRequest(getAssetLiabilityMaster())
//
//    override fun getLoanAppPostSuccess(value: Response.ResponseGetLoanApplication) {
//        saveDataToDB(getAssetLiabilityMaster())
//        AppEvents.fireEventLoanAppChangeNavFragmentNext()
//    }
//
//    override fun getLoanAppPostFailure(msg: String) {
//        saveDataToDB(getAssetLiabilityMaster())
//        showToast(msg)
//    }
//
//    private fun saveDataToDB(assetLiability: AssetLiabilityMaster) {
//        GlobalScope.launch {
//            dataBase.provideDataBaseSource().assetLiabilityDao().insertAssetLiability(assetLiability)
//        }
//    }
//
//    override fun onAssetDeleteClicked(position: Int) = showAlertDialog(position, ASSET)
//
//    override fun onObligationDeleteClicked(position: Int) = showAlertDialog(position, OBLIGATION)
//
//    override fun onCardDetailDeleteClicked(position: Int) = showAlertDialog(position, CARD)
//
//    override fun onAssetEditClicked(position: Int, asset: AssetLiability) {
//        currentAsset = asset
//        fillAssetFormWithCurrentAsset(currentAsset!!)
//    }
//
//    override fun onCardDetailEditClicked(position: Int, card: CardDetail) {
//        currentCardDetail = card
//        fillCardDetailFormWithCurrentCard(binding.layoutCreditCard, currentCardDetail!!)
//    }
//
//
//    override fun onObligationEditClicked(position: Int, obligation: ObligationDetail) {
//        currentObligation = obligation
//       // fillObligationFormWithCurrentObligation(binding.layoutObligations, currentObligation!!)
//
//  obligationItemDetail = LayoutInflater.from(mContext).inflate(R.layout.obligation_item_dialog, null)
//        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(mContext)
//                .setView(obligationItemDetail)
//                .setCancelable(true)
//
//
//        var   binding: ObligationItemDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.obligation_item_dialog, null, true)
//
//
//        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(mContext)
//                .setView(binding.root)
//                .setCancelable(true)
//
//
//        obligationItemDetailDialog = mBuilder.show()
//       // binding.tvLoanOwnership.setText(allMasterDropDown.LoanOwnership?.get(position)?.typeDetailCode)
//        for (i in 0 until allMasterDropDown.LoanOwnership!!.size) {
//
//            if (obligation.loanOwnershipTypeDetailID == allMasterDropDown.LoanOwnership?.get(i)?.typeDetailID) {
//
//                binding.tvLoanOwnership.setText(allMasterDropDown.LoanOwnership?.get(i)?.typeDetailCode)
//
//            }
//        }
//
//
//
//        for (i in 0 until allMasterDropDown.LoanType!!.size) {
//
//            if (obligation.loanTypeTypeDetailID == allMasterDropDown.LoanType?.get(i)?.typeDetailID) {
//                //binding.tvObligate.setText(allMasterDropDown.Obligate?.get(i)?.typeDetailCode)
//                binding.tvLoanType.setText(allMasterDropDown.LoanType?.get(i)?.typeDetailCode)
//
//            }
//        }
//
//        for (i in 0 until allMasterDropDown.Obligate!!.size) {
//
//            if (obligation.obligateTypeDetailID == allMasterDropDown.Obligate?.get(i)?.typeDetailID) {
//                binding.tvObligate.setText(allMasterDropDown.Obligate?.get(i)?.typeDetailCode)
//
//            }
//        }
//
//        binding.tvFinancerName.setText(obligation.financerName)
//        binding.tvTenure.setText(obligation.tenure.toString())
//        binding.tvBalanceTenure.setText(obligation.balanceTenure.toString())
//        binding.tvEMI.setText(obligation.emiAmount.toString())
//        binding.tvNumOfBouncesInSixMonths.setText(obligation.numberOfBouncesInLastSixMonth.toString())
//        binding.tvNumOfBouncesInNineMonths.setText(obligation.numberOfBouncesInLastNineMonth.toString())
//        binding.tvLoanAcNum.setText(obligation.loanAccountNumber)
//        binding.tvEmiPaid.setText(obligation.bounseEmiPaidInSameMonth.toString())
//        binding.tvLoanAmount.setText(obligation.loanAmount.toString())
//       // binding.tvDisbursementDate.setText(obligation.)
//        for (i in 0 until allMasterDropDown.RepaymentBank!!.size) {
//
//            if(obligation.repaymentBankTypeDetailID==allMasterDropDown.RepaymentBank?.get(i)?.typeDetailID){
//                binding.tvRepaymentBank.setText(allMasterDropDown.RepaymentBank?.get(i)?.typeDetailCode)
//
//            }
//
//
//        }
//
//    }
//
//
//    private fun fillAssetFormWithCurrentAsset(currentAsset: AssetLiability) {
//        binding.etValue.setText(currentAsset.assetValue.toString())
//        selectMasterDropdownValue(binding.spinnerOwnership, currentAsset.ownershipTypeDetailID)
//        selectMasterDropdownValue(binding.spinnerAssetType, currentAsset.assetDetailsTypeDetailID)
//        selectMasterDropdownValue(binding.spinnerAssetSubType, currentAsset.subTypeOfAssetTypeDetailID)
//        selectMasterDropdownValue(binding.spinnerDocumentProof, currentAsset.documentedProofTypeDetailID)
//
//    }
//
//    private fun fillCardDetailFormWithCurrentCard(binding: LayoutCreditCardDetailsBinding, currentCardDetail: CardDetail) {
//        binding.etCreditCardLimit.setText(currentCardDetail.cardLimit.toString())
//        binding.etCurrentUtilization.setText(currentCardDetail.currentUtilization.toString())
//        binding.etLastPaymentDate.setText(currentCardDetail.lastPaymentDate.toString())
//        selectMasterDropdownValue(binding.spinnerBankName, currentCardDetail.bankNameTypeDetailID)
//        selectMasterDropdownValue(binding.spinnerObligate, currentCardDetail.obligateTypeDetail)
//    }
//
//    private fun fillObligationFormWithCurrentObligation(binding: LayoutObligationBinding, currentObligation: ObligationDetail) {
//        binding.etFinancierName.setText(currentObligation.financerName)
//        binding.etLoanAmount.setText(currentObligation.loanAmount.toString())
//        binding.etAccountNum.setText(currentObligation.loanAccountNumber)
//        binding.etTenure.setText(currentObligation.tenure.toString())
//        binding.etBalanceTenure.setText(currentObligation.balanceTenure.toString())
//        binding.etEmiAmount.setText(currentObligation.emiAmount.toString())
//        binding.etBouncesInLastSixMonths.setText(currentObligation.numberOfBouncesInLastSixMonth.toString())
//        binding.etBouncesInLastNineMonths.setText(currentObligation.numberOfBouncesInLastNineMonth.toString())
//
//    }
//
//    private fun showAlertDialog(position: Int, formType: Int) {
//        val deleteDialogView = LayoutInflater.from(activity).inflate(R.layout.delete_dialog, null)
//        val mBuilder = AlertDialog.Builder(mContext)
//                .setView(deleteDialogView)
//                .setTitle("Delete Detail")
//        deleteDialog = mBuilder.show()
//        deleteDialogView.tvDeleteConfirm.setOnClickListener {
//            when (formType) {
//                ASSET -> deleteAsset(position)
//                CARD -> deleteCard(position)
//                OBLIGATION -> deleteObligation(position)
//            }
//        }
//        deleteDialogView.tvDonotDelete.setOnClickListener { deleteDialog.dismiss() }
//    }
//
//    private fun deleteAsset(position: Int) {
//        assetsList?.removeAt(position)
//        binding.rcAsset.adapter!!.notifyItemRemoved(position)
//        deleteDialog.dismiss()
//    }
//
//    private fun deleteCard(position: Int) {
//        cardDetailList?.removeAt(position)
//        binding.layoutCreditCard.rcCreditCard.adapter!!.notifyItemRemoved(position)
//        deleteDialog.dismiss()
//    }
//
//    private fun deleteObligation(position: Int) {
//        obligationsList?.removeAt(position)
//        binding.layoutObligations.rcObligation.adapter!!.notifyItemRemoved(position)
//        deleteDialog.dismiss()
//    }
//
//    private fun getCurrentObligation(): ObligationDetail {
//        val currentObligation = ObligationDetail()
//        val loanOwnership = binding.layoutObligations.spinnerLoanOwnership.selectedItem as DropdownMaster?
//        val obligate = binding.layoutObligations.spinnerObligate.selectedItem as DropdownMaster?
//        val loanType = binding.layoutObligations.spinnerLoanType.selectedItem as DropdownMaster?
//        val repaymentBank = binding.layoutObligations.spinnerRepaymentBank.selectedItem as DropdownMaster?
//        val emiPaidInSameMonth = binding.layoutObligations.spinnerEmiPaidInSameMonth.selectedItem as DropdownMaster?
//
//        currentObligation.numberOfBouncesInLastNineMonth = binding.layoutObligations.etBouncesInLastNineMonths.text.toString().toInt()
//        currentObligation.numberOfBouncesInLastSixMonth = binding.layoutObligations.etBouncesInLastSixMonths.text.toString().toInt()
//        currentObligation.financerName = binding.layoutObligations.etFinancierName.text.toString()
//        currentObligation.loanAmount = CurrencyConversion().convertToNormalValue(binding.layoutObligations.etLoanAmount.text.toString()).toInt()
//        currentObligation.emiAmount = CurrencyConversion().convertToNormalValue(binding.layoutObligations.etEmiAmount.text.toString()).toInt()
//        currentObligation.loanAccountNumber = binding.layoutObligations.etAccountNum.text.toString()
//        currentObligation.tenure = binding.layoutObligations.etTenure.text.toString().toInt()
//        currentObligation.balanceTenure = binding.layoutObligations.etBalanceTenure.text.toString().toInt()
//        currentObligation.loanOwnershipTypeDetailID = loanOwnership?.typeDetailID
//        currentObligation.obligateTypeDetailID = obligate?.typeDetailID
//        currentObligation.loanTypeTypeDetailID = loanType?.typeDetailID
//        currentObligation.repaymentBankTypeDetailID = repaymentBank?.typeDetailID
//        currentObligation.bounseEmiPaidInSameMonth = emiPaidInSameMonth?.typeDetailID
//        return currentObligation
//    }
//
//    private fun getCurrentCardDetail(): CardDetail {
//        val currentCard = CardDetail()
//        val bankName = binding.layoutCreditCard.spinnerBankName.selectedItem as DropdownMaster?
//        val obligate = binding.layoutCreditCard.spinnerObligate.selectedItem as DropdownMaster?
//        currentCard.lastPaymentDate = binding.layoutCreditCard.etLastPaymentDate.text.toString()
//        currentCard.cardLimit = binding.layoutCreditCard.etCreditCardLimit.text.toString().toInt()
//        currentCard.currentUtilization = binding.layoutCreditCard.etCurrentUtilization.text.toString().toInt()
//        currentCard.bankNameTypeDetailID = bankName?.typeDetailID
//        currentCard.obligateTypeDetail = obligate?.typeDetailID
//        return currentCard
//    }
//
//    private fun getCurrentAsset(): AssetLiability {
//        val currentAsset = AssetLiability()
//        val assetType = binding.spinnerAssetType.selectedItem as DropdownMaster?
//        val assetSubType = binding.spinnerAssetSubType.selectedItem as DropdownMaster?
//        val ownership = binding.spinnerOwnership.selectedItem as DropdownMaster?
//        val documentProof = binding.spinnerDocumentProof.selectedItem as DropdownMaster?
//
//        currentAsset.assetValue = CurrencyConversion().convertToNormalValue(binding.etValue.text.toString()).toInt()
//        currentAsset.assetDetailsTypeDetailID = assetType?.typeDetailID
//        currentAsset.subTypeOfAssetTypeDetailID = assetSubType?.typeDetailID
//        currentAsset.ownershipTypeDetailID = ownership?.typeDetailID
//        currentAsset.documentedProofTypeDetailID = documentProof?.typeDetailID
//        return currentAsset
//    }
//
//
//    private fun showDialogtoAddAssests() {
//
//
//        val binding:AddAssestsDialogBinding =DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.add_assests_dialog, null, true)
//
//
//        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(mContext)
//                .setView(binding.root)
//                .setCancelable(false)
//
//
//        addAssestsDialog = mBuilder.show()
//        addAssestsDialog.cancel_bttn.setOnClickListener() {
//
//            addAssestsDialog.dismiss()
//        }
//
//
//        binding.spinnerAssetType.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.AssetDetail!!)
//        binding.spinnerAssetSubType.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.AssetSubType!!)
//        binding.spinnerOwnership.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.AssetOwnership!!)
//        binding.spinnerDocumentProof.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown!!.DocumentProof!!)
//
//
//        binding.btnAddAsset.setOnClickListener() {
//             if (formValidation.validateAssetsDialog(binding)) {
//            val currentAsset = AssetLiability()
//            val assetType = binding.spinnerAssetType.selectedItem as DropdownMaster?
//            val assetSubType = binding.spinnerAssetSubType.selectedItem as DropdownMaster?
//            val ownership = binding.spinnerOwnership.selectedItem as DropdownMaster?
//            val documentProof = binding.spinnerDocumentProof.selectedItem as DropdownMaster?
//            currentAsset.assetValue = CurrencyConversion().convertToNormalValue(binding.etValue.text.toString()).toInt()
//            currentAsset.assetDetailsTypeDetailID = assetType?.typeDetailID
//            currentAsset.subTypeOfAssetTypeDetailID = assetSubType?.typeDetailID
//            currentAsset.ownershipTypeDetailID = ownership?.typeDetailID
//            currentAsset.documentedProofTypeDetailID = documentProof?.typeDetailID
//
//
//            assetsList?.add(currentAsset)
//            assetAdapter.notifyDataSetChanged()
//            addAssestsDialog.dismiss()
//            showToast("Asset add sucessfully")
//
//
//            setUpAssetAdapter(assetsList!!)
//
//
//            } else showToast(getString(R.string.validation_error))
//
//        }
//
//    }
//
//
//    private fun showDialogtoAddCreditCard() {
//
//        val   binding:AssetCreditcardDialogBinding= DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.asset_creditcard_dialog, null, true)
//
//        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(mContext)
//                .setView(binding.root)
//                .setCancelable(false)
//
//        addCreditCardDialog = mBuilder.show()
//        addCreditCardDialog.cancel_bttn.setOnClickListener() {
//
//            addCreditCardDialog.dismiss()
//        }
//
//        binding.spinnerBankName.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.BankName!!)
//        binding.spinnerObligate.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.CreditCardObligation!!)
//
//        binding.etLastPaymentDate.setOnClickListener() {
//            SelectDate(addCreditCardDialog.etLastPaymentDate, mContext)
//        }
//
//        addCreditCardDialog.btnAddCreditCard.setOnClickListener() {
//
//
//
//            if(formValidation.validateCardsDialog(binding)) {
//
//                val currentCard = CardDetail()
//                val bankName = binding.spinnerBankName.selectedItem as DropdownMaster?
//                val obligate = binding.spinnerObligate.selectedItem as DropdownMaster?
//                currentCard.lastPaymentDate = binding.etLastPaymentDate.text.toString()
//                currentCard.cardLimit = binding.etCreditCardLimit!!.text.toString().toInt()
//                currentCard.currentUtilization = binding.etCurrentUtilization!!.text.toString().toInt()
//                currentCard.bankNameTypeDetailID = bankName?.typeDetailID
//                currentCard.obligateTypeDetail = obligate?.typeDetailID
//
//                cardDetailList?.add(currentCard)
//                cardDetailAdapter.notifyDataSetChanged()
//            addCreditCardDialog.dismiss()
//        }else{
//                showToast(getString(R.string.validation_error))
//            }
//        }
//
//
//    }
//
//
//    private fun showDialogtoAddObligation() {
//      var   binding: AddObligationDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.add_obligation_dialog, null, true)
//
//
//        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(mContext)
//                .setView(binding.root)
//                .setCancelable(false)
//
//        addObligationDialog = mBuilder.show()
//
//
//        binding.spinnerObligate.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.Obligate!!)
//        binding.spinnerObligate.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.Obligate!!)
//        binding.spinnerLoanOwnership.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.LoanOwnership!!)
//        binding.spinnerLoanType.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.LoanType!!)
//        binding.spinnerRepaymentBank.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.RepaymentBank!!)
//        binding.spinnerEmiPaidInSameMonth.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.BounceEmiPaidInSameMonth!!)
//
//        addObligationDialog.etDisbursementDate.setOnClickListener() {
//            SelectDate(addObligationDialog.etDisbursementDate, mContext)
//        }
//
//        addObligationDialog.cancel_bttn.setOnClickListener() {
//
//            addObligationDialog.dismiss()
//        }
//        addObligationDialog.btnAddObligation.setOnClickListener() {
//
//
//
//            if(formValidation.validateObligations(binding)) {
//
//                val currentObligation = ObligationDetail()
//                val loanOwnership = binding.spinnerLoanOwnership.selectedItem as DropdownMaster?
//                val obligate = binding.spinnerObligate.selectedItem as DropdownMaster?
//                val loanType = binding.spinnerLoanType.selectedItem as DropdownMaster?
//                val repaymentBank = binding.spinnerRepaymentBank.selectedItem as DropdownMaster?
//                val emiPaidInSameMonth = binding.spinnerEmiPaidInSameMonth.selectedItem as DropdownMaster?
//                currentObligation.numberOfBouncesInLastNineMonth = binding.etBouncesInLastNineMonths.text.toString().toInt()
//                currentObligation.numberOfBouncesInLastSixMonth = binding.etBouncesInLastSixMonths.text.toString().toInt()
//                currentObligation.financerName = binding.etFinancierName.text.toString()
//                currentObligation.loanAmount = CurrencyConversion().convertToNormalValue(binding.etLoanAmount.text.toString()).toInt()
//                currentObligation.emiAmount = CurrencyConversion().convertToNormalValue(binding.etEmiAmount.text.toString()).toInt()
//                currentObligation.loanAccountNumber = binding.etAccountNum.text.toString()
//                currentObligation.tenure = binding.etTenure.text.toString().toInt()
//                currentObligation.balanceTenure = binding.etBalanceTenure.text.toString().toInt()
//                currentObligation.loanOwnershipTypeDetailID = loanOwnership?.typeDetailID
//                currentObligation.obligateTypeDetailID = obligate?.typeDetailID
//                currentObligation.loanTypeTypeDetailID = loanType?.typeDetailID
//                currentObligation.repaymentBankTypeDetailID = repaymentBank?.typeDetailID
//                currentObligation.bounseEmiPaidInSameMonth = emiPaidInSameMonth?.typeDetailID
//
//                obligationsList?.add(currentObligation)
//                obligationAdapter.notifyDataSetChanged()
//                addObligationDialog.dismiss()
//
//            }else{
//
//                showToast(getString(R.string.validation_error))
//            }
//        }
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        EventBus.getDefault().unregister(this)
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun customEventReceived(event: AssetLiabilityFragment?) {
//        // Toast.makeText(mContext,"custom event",Toast.LENGTH_SHORT)
//    }
//
//}