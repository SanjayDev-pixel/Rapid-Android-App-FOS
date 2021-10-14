package com.finance.app.view.customViews

import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.*
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.*
import com.finance.app.utility.CurrencyConversion
import com.finance.app.utility.LeadMetaData
import com.finance.app.utility.SelectDate
import com.finance.app.view.adapters.recycler.adapter.AssetDetailAdapter
import com.finance.app.view.adapters.recycler.adapter.CardDetailAdapter
import com.finance.app.view.adapters.recycler.adapter.ObligationAdapter
import com.finance.app.view.adapters.recycler.spinner.MasterSpinnerAdapter
import kotlinx.android.synthetic.main.add_assests_dialog.*
import kotlinx.android.synthetic.main.add_assests_dialog.cancel_bttn
import kotlinx.android.synthetic.main.add_assests_dialog.view.*
import kotlinx.android.synthetic.main.add_obligation_dialog.*
import kotlinx.android.synthetic.main.asset_creditcard_dialog.*
import kotlinx.android.synthetic.main.delete_dialog.view.*
import kotlinx.android.synthetic.main.layout_credit_card_details_new.etCreditCardLimit
import kotlinx.android.synthetic.main.layout_credit_card_details_new.etCurrentUtilization
import kotlinx.android.synthetic.main.layout_credit_card_details_new.etLastPaymentDate
import kotlinx.android.synthetic.main.layout_credit_card_details_new.spinnerObligate
import kotlinx.android.synthetic.main.obligation_item_dialog.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.util.AppUtilExtensions
import motobeans.architecture.util.DateUtil
import javax.inject.Inject

class CustomAssetLiabilityViewInfo @JvmOverloads constructor(context: Context , attrs: AttributeSet? = null) : LinearLayout(context , attrs) , AssetDetailAdapter.AssetClickListener , CardDetailAdapter.CardClickListener ,
        ObligationAdapter.ObligationClickListener {

    private val TAG = this.javaClass.canonicalName
    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var formValidation: FormValidation


    private lateinit var binding: LayoutCustomviewAssetliabilityBinding
    private lateinit var activity: FragmentActivity
    private var allMasterDropDown: AllMasterDropDown? = null
    private var assetAdapter: AssetDetailAdapter? = null
    private var cardDetailAdapter: CardDetailAdapter? = null
    private var obligationAdapter: ObligationAdapter? = null
    private var currentApplicant: AssetLiabilityModel = AssetLiabilityModel()
    private var assetsList: ArrayList<AssetLiability> = ArrayList()
    private var cardDetailList: ArrayList<CardDetail> = ArrayList()
    private var obligationsList: ArrayList<ObligationDetail> = ArrayList()
    private var currentPosition = 0
    private var deleteDialog: Dialog? = null
    private var addAssestsDialog: Dialog? = null
    private var addCreditCardDialog: Dialog? = null
    private var addObligationDialog: Dialog? = null
    private val ASSET: Int = 1
    private val CARD: Int = 2
    private val OBLIGATION: Int = 3

    private lateinit var selectedApplicant: PersonalApplicantsModel
    private lateinit var obligationItemDetailDialogView: View
    private lateinit var obligationItemDetailDialog: Dialog


    init {
        ArchitectureApp.instance.component.inject(this)
        binding = AppUtilExtensions.initCustomViewBinding(context = context , layoutId = R.layout.layout_customview_assetliability , container = this)

        initViews()
        setOnClickListener()

        LeadMetaData.getLeadData()?.let {
            if (it.status.equals(AppEnums.LEAD_TYPE.SUBMITTED.type , true)) {
                binding.addasset.visibility = View.GONE
                binding.layoutCreditCard.addcreditdilaog.visibility = View.GONE
                binding.layoutObligations.addcreditdilaog.visibility = View.GONE
            } else {
                binding.addasset.visibility = View.VISIBLE
                binding.layoutCreditCard.addcreditdilaog.visibility = View.VISIBLE
                binding.layoutObligations.addcreditdilaog.visibility = View.VISIBLE
            }
        }


    }


    private fun initViews() {


    }

    private fun setOnClickListener() {
        binding.addasset.setOnClickListener { showDialogtoAddAssests() }
        binding.layoutCreditCard.addcreditdilaog.setOnClickListener { showDialogtoAddCreditCard() }
        binding.layoutObligations.addcreditdilaog.setOnClickListener { showDialogtoAddObligation() }


        binding.assetcounter.setOnClickListener() {
            binding.llAssetDetail.visibility = View.VISIBLE
            binding.rcAsset.visibility = View.VISIBLE
            binding.layoutObligations.rcObligation.visibility = View.GONE
            binding.layoutCreditCard.rcCreditCard.visibility = View.GONE

        }

        binding.tvAssetdetail.setOnClickListener() {
            binding.llAssetDetail.visibility = View.VISIBLE
            binding.rcAsset.visibility = View.VISIBLE
            binding.layoutObligations.rcObligation.visibility = View.GONE
            binding.layoutCreditCard.rcCreditCard.visibility = View.GONE

        }

        cardDetailFormListeners(binding.layoutCreditCard)
        obligationFormListeners(binding.layoutObligations)


    }


    fun initApplicantDetails(fragmentActivity: FragmentActivity , applicant: PersonalApplicantsModel) {
        activity = fragmentActivity
        selectedApplicant = applicant

        //fetch details
        fetchDropDownsFromDB()
        fetchApplicantAssetsAndLibilityDetailsById(applicant.leadApplicantNumber.toString())
    }

    private fun setAssetAdapter(assets: ArrayList<AssetLiability> , allMasterDropDown: AllMasterDropDown?) {
        binding.rcAsset.layoutManager = LinearLayoutManager(context , RecyclerView.HORIZONTAL , false)
        assetAdapter = AssetDetailAdapter(context , assets , allMasterDropDown)
        binding.rcAsset.adapter = assetAdapter
        assetAdapter?.setOnAssetClickListener(this)
//        binding.pageIndicatorAsset.attachTo(binding.rcAsset)
        binding.assetcounter.setText(assets.size.toString())
    }

    private fun setAssetCounter(asset: ArrayList<AssetLiability>?) {
        binding.assetcounter.setText(asset?.size.toString())

    }

    private fun setCreditCardCounter(cards: ArrayList<CardDetail>?) {
        binding.layoutCreditCard.creditcardcounter.setText(cards?.size.toString())
    }

    private fun setObligationCounter(obligations: ArrayList<ObligationDetail>?) {
        binding.layoutObligations.obligationcounter.setText(obligations?.size.toString())

    }

    private fun setCardDetailAdapter(cards: ArrayList<CardDetail> , allMasterDropDown: AllMasterDropDown?) {
        binding.layoutCreditCard.rcCreditCard.layoutManager = LinearLayoutManager(context , RecyclerView.HORIZONTAL , false)
        cardDetailAdapter = CardDetailAdapter(context , cards , allMasterDropDown)
        binding.layoutCreditCard.rcCreditCard.adapter = cardDetailAdapter
        cardDetailAdapter?.setOnCardClickListener(this)
//        binding.layoutCreditCard.pageIndicatorCreditCard.attachTo(binding.layoutCreditCard.rcCreditCard)
        binding.layoutCreditCard.creditcardcounter.setText(cards.size.toString())
    }

    private fun setObligationAdapter(obligations: ArrayList<ObligationDetail> , allMasterDropDown: AllMasterDropDown?) {
        binding.layoutObligations.rcObligation.layoutManager = LinearLayoutManager(context , RecyclerView.HORIZONTAL , false)
        obligationAdapter = ObligationAdapter(context , obligations , allMasterDropDown)
        binding.layoutObligations.rcObligation.adapter = obligationAdapter
        obligationAdapter?.setOnObligationClickListener(this)
//        binding.layoutObligations.pageIndicatorObligation.attachTo(binding.layoutObligations.rcObligation)
        binding.layoutObligations.obligationcounter.setText(obligations.size.toString())


    }


    private fun fetchApplicantAssetsAndLibilityDetailsById(applicantNumber: String) {

        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(activity ,
                Observer { allMasterDropdown ->
                    allMasterDropdown?.let {
                        allMasterDropDown = it
                        callMydata(allMasterDropdown , applicantNumber)
                    }
                })


    }

    private fun callMydata(allMasterDropdown: AllMasterDropDown , applicantNumber: String) {

        LeadMetaData.getLeadObservable().observe(activity , Observer { allLeadDetails ->
            allLeadDetails?.let {
                val selectedApplicantList = it.assetLiabilityData.loanApplicationObj.filter { assetsLiability -> applicantNumber.equals(assetsLiability.leadApplicantNumber , true) }

                if (selectedApplicantList.isNotEmpty()) {
                    setAssetAdapter(selectedApplicantList[0].applicantAssetDetailList , allMasterDropDown)
                    setCardDetailAdapter(selectedApplicantList[0].applicantCreditCardDetailList , allMasterDropDown)
                    setObligationAdapter(selectedApplicantList[0].applicantExistingObligationList , allMasterDropDown)
                } else {
                    setAssetAdapter(ArrayList() , allMasterDropDown)
                    setCardDetailAdapter(ArrayList() , allMasterDropDown)
                    setObligationAdapter(ArrayList() , allMasterDropDown)
                }

            }
        })


    }


    /* private fun initializeViews(applicant: AssetLiabilityModel) {
         proceedFurther(applicant)
 //        setClickListeners()
         showData(applicant)
     }


     private fun showData(applicant: AssetLiabilityModel) {

     //    setUpCurrentApplicantDetails(applicant)

     }

     private fun showSetDataOnView(applicantList: ArrayList<AssetLiabilityModel>?) {
         for (applicant in applicantList!!) {
             if (applicant.isMainApplicant) {
                 currentApplicant = applicant
             }
         }

         //setUpCurrentApplicantDetails(currentApplicant)

     }


     private fun setUpCurrentApplicantDetails(currentApplicant: AssetLiabilityModel) {
         assetsList = currentApplicant.applicantAssetDetailList
         cardDetailList = currentApplicant.applicantCreditCardDetailList
         obligationsList = currentApplicant.applicantExistingObligationList
         setAssetAdapter(assetsList,allMasterDropDown)
         setCardDetailAdapter(cardDetailList)
         setObligationAdapter(obligationsList)


     }*/

    /* private fun proceedFurther(applicant: AssetLiabilityModel?) {

         fetchDropDownsFromDB()
     }
 */

    private fun fetchDropDownsFromDB() {
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(activity ,
                Observer { allMasterDropdown ->
                    allMasterDropdown?.let {
                        allMasterDropDown = it
                    }
                })
    }


    private fun assetFormListeners(binding: LayoutCustomviewAssetliabilityBinding) {

        binding.addasset.setOnClickListener() {
            showDialogtoAddAssests()
        }
    }

    // dialog for add asset
    private fun showDialogtoAddAssests() {

        val binding = DataBindingUtil.inflate<AddAssestsDialogBinding>(LayoutInflater.from(context) , R.layout.add_assests_dialog , null , false)
        val mBuilder = AlertDialog.Builder(context)
                .setView(binding.root)
                .setCancelable(false)

        addAssestsDialog = mBuilder.show()
        binding.cancelBttn.cancel_bttn?.setOnClickListener() {
            addAssestsDialog?.dismiss()
        }

        addAssestsDialog?.spinnerAssetType?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.AssetDetail!!)
        addAssestsDialog?.spinnerAssetSubType?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.AssetSubType!!)
        addAssestsDialog?.spinnerOwnership?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.AssetOwnership!!)
        addAssestsDialog?.spinnerDocumentProof?.adapter = MasterSpinnerAdapter(context , allMasterDropDown!!.DocumentProof!!)

        addAssestsDialog?.btnAddAsset?.setOnClickListener() {
            if (formValidation.validateAssetsDialog(binding)) {
                val currentAsset = AssetLiability()
                val assetType = addAssestsDialog?.spinnerAssetType?.selectedItem as DropdownMaster?
                val assetSubType = addAssestsDialog?.spinnerAssetSubType?.selectedItem as DropdownMaster?
                val ownership = addAssestsDialog?.spinnerOwnership?.selectedItem as DropdownMaster?
                val documentProof = addAssestsDialog?.spinnerDocumentProof?.selectedItem as DropdownMaster?
                currentAsset.assetValue = CurrencyConversion().convertToNormalValue(addAssestsDialog?.etValue?.text.toString()).toInt()
                currentAsset.assetDetailsTypeDetailID = assetType?.typeDetailID
                currentAsset.subTypeOfAssetTypeDetailID = assetSubType?.typeDetailID
                currentAsset.ownershipTypeDetailID = ownership?.typeDetailID
                currentAsset.documentedProofTypeDetailID = documentProof?.typeDetailID

                assetAdapter?.addItem(currentPosition , currentAsset)
                addAssestsDialog?.dismiss()
                setAssetCounter(assetAdapter?.getItemList())
                showAssetRecyclerView("asset")


            } else {
                //showToast(getString(R.string.validation_error))
                Toast.makeText(context , context.getString(R.string.validation_error) , Toast.LENGTH_SHORT).show()
            }
        }


    }


    //add obligation Detail
    private fun showDialogtoAddObligation() {
        val binding = DataBindingUtil.inflate<AddObligationDialogBinding>(LayoutInflater.from(context) , R.layout.add_obligation_dialog , null , false)
        val mBuilder = AlertDialog.Builder(context)
                .setView(binding.root)
                .setCancelable(false)

        addObligationDialog = mBuilder.show()
        addObligationDialog?.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        addObligationDialog?.cancel_bttn?.setOnClickListener() {
            addObligationDialog?.dismiss()

        }

        addObligationDialog?.spinnerObligate?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.Obligate!!)
        addObligationDialog?.spinnerObligate?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.Obligate!!)
        addObligationDialog?.spinnerLoanOwnership?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.LoanOwnership!!)
        addObligationDialog?.spinnerLoanType?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.LoanType!!)
        val adapter = ArrayAdapter(context!! , android.R.layout.simple_list_item_1 , allMasterDropDown?.RepaymentBank!!)
        addObligationDialog?.spinnerLoanType?.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>? , view: View? , position: Int , id: Long) {
              val loanType = parent?.selectedItem as DropdownMaster?
                loanType.let { loan->
                    if(loan?.typeDetailID == Constants.GOLD_LOAN || loan?.typeDetailID ==Constants.EMPLOYER_LOAN || loan?.typeDetailID == Constants.KCC_LOAN)
                    {
                        addObligationDialog?.spinnerRepaymentBank?.visibility = View.GONE
                        addObligationDialog?.spinnerRepaymentBank?.setAdapter(adapter)
                    }
                    else
                    {
                        addObligationDialog?.spinnerRepaymentBank?.visibility = View.VISIBLE
                    }
                }
            }

        }
        // addObligationDialog?.spinnerRepaymentBank?.adapter = MasterSpinnerAdapter(context, allMasterDropDown?.RepaymentBank!!)
        addObligationDialog?.spinnerEmiPaidInSameMonth?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.BounceEmiPaidInSameMonth!!)


        addObligationDialog?.spinnerRepaymentBank?.setAdapter(adapter)
        addObligationDialog?.spinnerRepaymentBank?.setOnItemClickListener { parent , view , position , id ->
            addObligationDialog?.spinnerRepaymentBank?.tag = parent.getItemAtPosition(position) as DropdownMaster
        }

        addObligationDialog?.etDisbursementDate?.setOnClickListener() {
            SelectDate(addObligationDialog?.etDisbursementDate!! , context)
        }

        addObligationDialog?.cancel_bttn?.setOnClickListener() {

            addObligationDialog?.dismiss()
        }
        addObligationDialog?.btnAddObligation?.setOnClickListener() {


            if (formValidation.validateObligationDialog(binding)) {
                val dateUtil: DateUtil = DateUtil()
                val currentObligation = ObligationDetail()
                val loanOwnership = binding.spinnerLoanOwnership.selectedItem as DropdownMaster?
                val obligate = addObligationDialog?.spinnerObligate?.selectedItem as DropdownMaster?
                val loanType = addObligationDialog?.spinnerLoanType?.selectedItem as DropdownMaster?
                val repaymentBank = addObligationDialog?.spinnerRepaymentBank?.tag as DropdownMaster?
                val emiPaidInSameMonth = addObligationDialog?.spinnerEmiPaidInSameMonth?.selectedItem as DropdownMaster?
                currentObligation.numberOfBouncesInLastSixMonth = addObligationDialog?.etBouncesInLastSixMonths?.text.toString().toInt()
                currentObligation.numberOfBouncesInLastNineMonth = addObligationDialog?.etBouncesInLastNineMonths?.text.toString().toInt()
                currentObligation.financerName = addObligationDialog?.etFinancierName?.text.toString()
                currentObligation.loanAmount = CurrencyConversion().convertToNormalValue(addObligationDialog?.etLoanAmount?.text.toString()).toInt()
                currentObligation.emiAmount = CurrencyConversion().convertToNormalValue(addObligationDialog?.etEmiAmount?.text.toString()).toInt()
                currentObligation.loanAccountNumber = addObligationDialog?.etAccountNum?.text.toString()
                currentObligation.tenure = addObligationDialog?.etTenure?.text.toString().toInt()
                currentObligation.balanceTenure = addObligationDialog?.etBalanceTenure?.text.toString().toInt()
                currentObligation.loanOwnershipTypeDetailID = loanOwnership?.typeDetailID
                currentObligation.obligateTypeDetailID = obligate?.typeDetailID
                currentObligation.loanTypeTypeDetailID = loanType?.typeDetailID
                if(loanType?.typeDetailID == Constants.KCC_LOAN || loanType?.typeDetailID == Constants.EMPLOYER_LOAN ||loanType?.typeDetailID == Constants.GOLD_LOAN)
                {

                }
                else {
                    currentObligation.repaymentBankTypeDetailID = repaymentBank?.typeDetailID
                }
                currentObligation.bounseEmiPaidInSameMonth = emiPaidInSameMonth?.typeDetailID
                currentObligation.loanDisbursementDate = dateUtil.getFormattedDate(DateUtil.dateFormattingType.TYPE_NORMAL_1 , DateUtil.dateFormattingType.TYPE_API_REQUEST_2 , addObligationDialog!!.etDisbursementDate?.text.toString())

                obligationAdapter?.addItem(currentPosition , currentObligation)
                addObligationDialog?.dismiss()
                setObligationCounter(obligationAdapter?.getItemList())
                showAssetRecyclerView("obligation")


            } else {

                //showToast(context.getString(R.string.validation_error))
                Toast.makeText(context , context.getString(R.string.validation_error) , Toast.LENGTH_SHORT).show()
            }
        }
    }

    // diaolog for credit card
    private fun showDialogtoAddCreditCard() {
        val binding = DataBindingUtil.inflate<AssetCreditcardDialogBinding>(LayoutInflater.from(context) , R.layout.asset_creditcard_dialog , null , false)
        val mBuilder = AlertDialog.Builder(context)
                .setView(binding.root)
                .setCancelable(false)

        addCreditCardDialog = mBuilder.show()
        addCreditCardDialog?.cancel_bttn?.setOnClickListener() {
            addCreditCardDialog?.dismiss()
        }

        //addCreditCardDialog?.spinnerBankName?.adapter = MasterSpinnerAdapter(context, allMasterDropDown?.BankName!!)

        val adapter = ArrayAdapter(context!! , android.R.layout.simple_list_item_1 , allMasterDropDown?.BankName!!)
        binding.spinnerBankName.setAdapter(adapter)
        binding.spinnerBankName.setOnItemClickListener { parent , view , position , id ->
            binding.spinnerBankName.tag = parent.getItemAtPosition(position) as DropdownMaster
        }


        addCreditCardDialog?.spinnerObligate?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.CreditCardObligation!!)
        addCreditCardDialog?.etLastPaymentDate?.setOnClickListener {
            SelectDate(addCreditCardDialog?.etLastPaymentDate!! , context)
        }
        addCreditCardDialog?.btnAddcrdetail?.setOnClickListener() {
            if (formValidation.validateCardsDialog(binding)) {
                val mydate: DateUtil = DateUtil()
                val currentCard = CardDetail()
                //val bankName = addCreditCardDialog?.spinnerBankName?.selectedItem as DropdownMaster?
                val bankName = binding?.spinnerBankName?.tag as DropdownMaster?
                val obligate = addCreditCardDialog?.spinnerObligate?.selectedItem as DropdownMaster?
                currentCard.lastPaymentDate = mydate.getFormattedDate(DateUtil.dateFormattingType.TYPE_NORMAL_1 , DateUtil.dateFormattingType.TYPE_API_REQUEST_2 , addCreditCardDialog?.etLastPaymentDate?.text.toString()) //addCreditCardDialog?.etLastPaymentDate?.text.toString()
                currentCard.cardLimit = addCreditCardDialog?.etCreditCardLimit!!.text.toString().toInt()
                currentCard.currentUtilization = addCreditCardDialog?.etCurrentUtilization!!.text.toString().toInt()
                currentCard.bankNameTypeDetailID = bankName?.typeDetailID
                currentCard.obligateTypeDetail = obligate?.typeDetailID
                cardDetailAdapter?.addItem(currentPosition , currentCard)
                addCreditCardDialog?.dismiss()
                setCreditCardCounter(cardDetailAdapter?.getItemList())
                showAssetRecyclerView("card")
            } else {
                Toast.makeText(context , context.getString(R.string.validation_error) , Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cardDetailFormListeners(layoutCreditCard: LayoutCreditCardDetailsBinding?) {
        binding.layoutCreditCard.tvCreditdetail.setOnClickListener() {
            binding.layoutCreditCard.rcCreditCard.visibility = View.VISIBLE

            binding.rcAsset.visibility = View.GONE
            binding.layoutObligations.rcObligation.visibility = View.GONE

        }

        binding.layoutCreditCard.creditcardcounter.setOnClickListener() {

            binding.layoutCreditCard.rcCreditCard.visibility = View.VISIBLE
            binding.rcAsset.visibility = View.GONE
            binding.layoutObligations.rcObligation.visibility = View.GONE

        }

    }

    private fun obligationFormListeners(layoutObligations: LayoutObligationBinding?) {
        binding.layoutObligations.tvObligationdetail.setOnClickListener() {
            binding.layoutObligations.rcObligation.visibility = View.VISIBLE
            binding.layoutCreditCard.rcCreditCard.visibility = View.GONE
            binding.rcAsset.visibility = View.GONE
        }
        binding.layoutObligations.obligationcounter.setOnClickListener() {

            binding.layoutObligations.rcObligation.visibility = View.VISIBLE
            binding.layoutCreditCard.rcCreditCard.visibility = View.GONE
            binding.rcAsset.visibility = View.GONE
        }

    }


    private fun deleteAsset(position: Int) {
        assetAdapter?.deleteItem(position)
        deleteDialog?.dismiss()
        setAssetCounter(assetAdapter?.getItemList())

    }

    private fun deleteCard(position: Int) {
        cardDetailAdapter?.deleteItem(position)
        deleteDialog?.dismiss()
        setCreditCardCounter(cardDetailAdapter?.getItemList())

    }

    private fun deleteObligation(position: Int) {
        obligationAdapter?.deleteItem(position)
        deleteDialog?.dismiss()
        setObligationCounter(obligationAdapter?.getItemList())

    }


    override fun onAssetEditClicked(position: Int , asset: AssetLiability) {

        showDialogtoEditAssests(position , asset)
    }

    override fun onCardDetailEditClicked(position: Int , card: CardDetail) {
        showDialogEditCreditCard(position , card)
    }

    override fun onObligationEditNewClick(position: Int , obligation: ObligationDetail) {
        showDialogToEditObligation(position , obligation)
    }


    override fun onAssetDeleteClicked(position: Int) = showAlertDialog(position , ASSET)

    override fun onObligationDeleteClicked(position: Int) = showAlertDialog(position , OBLIGATION)

    override fun onCardDetailDeleteClicked(position: Int) = showAlertDialog(position , CARD)


    private fun showAlertDialog(position: Int , formType: Int) {
        val deleteDialogView = LayoutInflater.from(context).inflate(R.layout.delete_dialog , null)
        val mBuilder = android.app.AlertDialog.Builder(context)
                .setView(deleteDialogView)
                .setTitle("Delete Detail")
        deleteDialog = mBuilder.show()
        deleteDialogView.tvDeleteConfirm.setOnClickListener {
            when (formType) {
                ASSET -> deleteAsset(position)
                CARD -> deleteCard(position)
                OBLIGATION -> deleteObligation(position)
            }
        }
        deleteDialogView.tvDonotDelete.setOnClickListener { deleteDialog?.dismiss() }
    }


    fun isValidAssetApplicant(): AssetLiabilityModel? {
        return if (formValidation.validateAssetLiabilityInfo(binding)) getCurrentApplicant()
        else null
    }


    fun getCurrentApplicant(): AssetLiabilityModel {
        val currentApplicant = AssetLiabilityModel()
        currentApplicant.isMainApplicant = selectedApplicant.isMainApplicant
        currentApplicant.leadApplicantNumber = selectedApplicant.leadApplicantNumber
        currentApplicant.applicantAssetDetailList = assetAdapter?.getItemList() ?: ArrayList()
        currentApplicant.applicantCreditCardDetailList = cardDetailAdapter?.getItemList() ?: ArrayList()
        currentApplicant.applicantExistingObligationList = obligationAdapter?.getItemList() ?: ArrayList()

        return currentApplicant
    }


    override fun onObligationEditClicked(position: Int , obligation: ObligationDetail) {


        obligationItemDetailDialogView = LayoutInflater.from(context).inflate(R.layout.obligation_item_dialog , null)
        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(context)
                .setView(obligationItemDetailDialogView)
                .setCancelable(true)

        obligationItemDetailDialog = mBuilder.show()


        obligationItemDetailDialog.tvFinancerName.setText(obligation.financerName)
        obligationItemDetailDialog.tvTenure.setText(obligation.tenure.toString())
        obligationItemDetailDialog.tvBalanceTenure.setText(obligation.balanceTenure.toString())
        obligationItemDetailDialog.tvEMI.setText(obligation.emiAmount.toString())
        obligationItemDetailDialog.tvNumOfBouncesInSixMonths.setText(obligation.numberOfBouncesInLastSixMonth.toString())
        obligationItemDetailDialog.tvNumOfBouncesInNineMonths.setText(obligation.numberOfBouncesInLastNineMonth.toString())
        obligationItemDetailDialog.tvLoanAcNum.setText(obligation.loanAccountNumber)
//        obligationItemDetailDialog.tvEmiPaid.setText(obligation.bounseEmiPaidInSameMonth.toString())
        obligationItemDetailDialog.tvLoanAmount.setText(obligation.loanAmount.toString())
        val dateUtil: DateUtil = DateUtil()
        obligationItemDetailDialog.tvDisbursementDate.setText(dateUtil.getFormattedDate(DateUtil.dateFormattingType.TYPE_API_REQUEST_2 , DateUtil.dateFormattingType.TYPE_NORMAL_1 , obligation.loanDisbursementDate))




        for (i in 0 until allMasterDropDown?.RepaymentBank!!.size) {

            if (obligation.repaymentBankTypeDetailID == allMasterDropDown!!.RepaymentBank?.get(i)?.typeDetailID) {

                obligationItemDetailDialog.tvRepaymentBank.setText(allMasterDropDown!!.RepaymentBank?.get(i)?.typeDetailCode)

            }

        }

        for (i in 0 until allMasterDropDown?.LoanOwnership!!.size) {

            if (obligation.loanOwnershipTypeDetailID == allMasterDropDown!!.LoanOwnership?.get(i)?.typeDetailID) {

                obligationItemDetailDialog.tvLoanOwnership.setText(allMasterDropDown!!.LoanOwnership?.get(i)?.typeDetailCode)

            }
        }

        for (i in 0 until allMasterDropDown?.LoanType!!.size) {

            if (obligation.loanTypeTypeDetailID == allMasterDropDown!!.LoanType?.get(i)?.typeDetailID) {
                if(allMasterDropDown!!.LoanType?.get(i)?.typeDetailID == Constants.GOLD_LOAN || allMasterDropDown!!.LoanType?.get(i)?.typeDetailID == Constants.EMPLOYER_LOAN || allMasterDropDown!!.LoanType?.get(i)?.typeDetailID == Constants.KCC_LOAN)
                {
                    obligationItemDetailDialog.tvRepaymentBank.text = "N/A"
                }
                obligationItemDetailDialog.tvLoanType.setText(allMasterDropDown!!.LoanType?.get(i)?.typeDetailCode)

            }
        }

        for (i in 0 until allMasterDropDown?.Obligate!!.size) {

            if (obligation.obligateTypeDetailID == allMasterDropDown!!.Obligate?.get(i)?.typeDetailID) {
                obligationItemDetailDialog.tvObligate.setText(allMasterDropDown!!.Obligate?.get(i)?.typeDetailCode)

            }
        }
        for(i:Int in 0 until  allMasterDropDown?.BounceEmiPaidInSameMonth!!.size){
            if(obligation.bounseEmiPaidInSameMonth == allMasterDropDown?.BounceEmiPaidInSameMonth?.get(i)?.typeDetailID){
                obligationItemDetailDialog.tvEmiPaid.setText(allMasterDropDown?.BounceEmiPaidInSameMonth?.get(i)?.typeDetailCode)
            }
        }


    }


    private fun selectMasterDropdownValue(spinner: Spinner , id: Int?) {
        for (index in 0 until spinner.count - 1) {
            val obj = spinner.getItemAtPosition(index) as DropdownMaster
            if (obj.typeDetailID == id) {
                spinner.setSelection(index + 1)
                return
            }
        }
    }


    //edit asset item
    private fun showDialogtoEditAssests(position: Int , asset: AssetLiability) {
        val binding = DataBindingUtil.inflate<AddAssestsDialogBinding>(LayoutInflater.from(context) , R.layout.add_assests_dialog , null , false)
        val mBuilder = AlertDialog.Builder(context)
                .setView(binding.root)
                .setCancelable(true)

        addAssestsDialog = mBuilder.show()
        binding.cancelBttn.cancel_bttn?.setOnClickListener() {
            addAssestsDialog?.dismiss()
        }

        binding?.spinnerAssetType?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.AssetDetail!!)
        binding?.spinnerAssetSubType?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.AssetSubType!!)
        binding?.spinnerOwnership?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.AssetOwnership!!)
        binding?.spinnerDocumentProof?.adapter = MasterSpinnerAdapter(context , allMasterDropDown!!.DocumentProof!!)

        selectMasterDropdownValue(binding.spinnerAssetType , asset.assetDetailsTypeDetailID)
        selectMasterDropdownValue(binding.spinnerAssetSubType , asset.subTypeOfAssetTypeDetailID)
        selectMasterDropdownValue(binding.spinnerOwnership , asset.ownershipTypeDetailID)
        selectMasterDropdownValue(binding.spinnerDocumentProof , asset.documentedProofTypeDetailID)
        asset.assetValue?.let { binding.etValue.setText(it.toString()) }
        binding.btnAddAsset.setText(R.string.edit)


        addAssestsDialog?.btnAddAsset?.setOnClickListener() {
            if (formValidation.validateAssetsDialog(binding)) {
                val currentAsset = AssetLiability()
                val assetType = binding?.spinnerAssetType?.selectedItem as DropdownMaster?
                val assetSubType = binding?.spinnerAssetSubType?.selectedItem as DropdownMaster?
                val ownership = binding?.spinnerOwnership?.selectedItem as DropdownMaster?
                val documentProof = binding?.spinnerDocumentProof?.selectedItem as DropdownMaster?
                currentAsset.assetValue = CurrencyConversion().convertToNormalValue(binding?.etValue?.text.toString()).toInt()
                currentAsset.assetDetailsTypeDetailID = assetType?.typeDetailID
                currentAsset.subTypeOfAssetTypeDetailID = assetSubType?.typeDetailID
                currentAsset.ownershipTypeDetailID = ownership?.typeDetailID
                currentAsset.documentedProofTypeDetailID = documentProof?.typeDetailID

                assetAdapter?.updateItem(position , currentAsset)
                addAssestsDialog?.dismiss()
                assetAdapter?.notifyDataSetChanged()


            } else {
                //showToast(getString(R.string.validation_error))
                Toast.makeText(context , context.getString(R.string.validation_error) , Toast.LENGTH_SHORT).show()
            }
        }
    }

    //edit credit card item
    private fun showDialogEditCreditCard(position: Int , card: CardDetail) {
        val binding = DataBindingUtil.inflate<AssetCreditcardDialogBinding>(LayoutInflater.from(context) , R.layout.asset_creditcard_dialog , null , false)
        val mBuilder = AlertDialog.Builder(context)
                .setView(binding.root)
                .setCancelable(true)

        addCreditCardDialog = mBuilder.show()
        addCreditCardDialog?.cancel_bttn?.setOnClickListener() {
            addCreditCardDialog?.dismiss()
        }
        val dateUtils: DateUtil = DateUtil()
        // addCreditCardDialog?.spinnerBankName?.adapter = MasterSpinnerAdapter(context, allMasterDropDown?.BankName!!)
        val adapter = ArrayAdapter(context!! , android.R.layout.simple_list_item_1 , allMasterDropDown?.BankName!!)
        binding.spinnerBankName.setAdapter(adapter)
        binding.spinnerBankName.setOnItemClickListener { parent , view , position , id ->
            binding.spinnerBankName.tag = parent.getItemAtPosition(position) as DropdownMaster
        }
        allMasterDropDown?.BankName?.forEachIndexed { index , dropdownMaster ->
            if (dropdownMaster.typeDetailID == card.bankNameTypeDetailID) {
                (binding.spinnerBankName.setText(dropdownMaster.typeDetailDisplayText.toString() , false))
                binding.spinnerBankName.tag = dropdownMaster
                return@forEachIndexed
            }
        }

        addCreditCardDialog?.spinnerObligate?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.CreditCardObligation!!)
        //  selectMasterDropdownValue(binding.spinnerBankName, card.bankNameTypeDetailID)
        selectMasterDropdownValue(binding.spinnerObligate , card.obligateTypeDetail)
        card.cardLimit?.let { binding.etCreditCardLimit.setText(it.toString()) }
        card.currentUtilization?.let { binding.etCurrentUtilization.setText(it.toString()) }
        binding.etLastPaymentDate.setText(
                dateUtils.getFormattedDate(DateUtil.dateFormattingType.TYPE_API_REQUEST_2 , DateUtil.dateFormattingType.TYPE_NORMAL_1 , card.lastPaymentDate.toString())
        )
        binding.btnAddcrdetail.setText(R.string.edit)



        addCreditCardDialog?.etLastPaymentDate?.setOnClickListener {
            SelectDate(addCreditCardDialog?.etLastPaymentDate!! , context)
        }

        addCreditCardDialog?.btnAddcrdetail?.setOnClickListener() {

            if (formValidation.validateCardsDialog(binding)) {
                var mydate: DateUtil = DateUtil()
                val currentCard = CardDetail()
                //  val bankName = addCreditCardDialog?.spinnerBankName?.selectedItem as DropdownMaster?

                //For Auto complete view..
                val bankName = binding?.spinnerBankName?.tag as DropdownMaster?
                val obligate = addCreditCardDialog?.spinnerObligate?.selectedItem as DropdownMaster?
                currentCard.lastPaymentDate = mydate.getFormattedDate(DateUtil.dateFormattingType.TYPE_NORMAL_1 , DateUtil.dateFormattingType.TYPE_API_REQUEST_2 , addCreditCardDialog?.etLastPaymentDate?.text.toString())//addCreditCardDialog?.etLastPaymentDate?.text.toString()
                currentCard.cardLimit = addCreditCardDialog?.etCreditCardLimit!!.text.toString().toInt()
                currentCard.currentUtilization = addCreditCardDialog?.etCurrentUtilization!!.text.toString().toInt()
                currentCard.bankNameTypeDetailID = bankName?.typeDetailID
                currentCard.obligateTypeDetail = obligate?.typeDetailID


                cardDetailAdapter?.updateItem(position , currentCard)
                addCreditCardDialog?.dismiss()
                cardDetailAdapter?.notifyDataSetChanged()


            } else {
                Toast.makeText(context , context.getString(R.string.validation_error) , Toast.LENGTH_SHORT).show()
            }


        }
    }

    //edit obligation item
    private fun showDialogToEditObligation(position: Int , obligation: ObligationDetail) {

        val binding = DataBindingUtil.inflate<AddObligationDialogBinding>(LayoutInflater.from(context) , R.layout.add_obligation_dialog , null , false)
        val mBuilder = AlertDialog.Builder(context)
                .setView(binding.root)
                .setCancelable(true)

        addObligationDialog = mBuilder.show()
        addObligationDialog?.cancel_bttn?.setOnClickListener() {
            addObligationDialog?.dismiss()

        }

        addObligationDialog?.spinnerObligate?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.Obligate!!)
        addObligationDialog?.spinnerLoanOwnership?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.LoanOwnership!!)
        addObligationDialog?.spinnerLoanType?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.LoanType!!)
        // addObligationDialog?.spinnerRepaymentBank?.adapter = MasterSpinnerAdapter(context, allMasterDropDown?.RepaymentBank!!)
        addObligationDialog?.spinnerEmiPaidInSameMonth?.adapter = MasterSpinnerAdapter(context , allMasterDropDown?.BounceEmiPaidInSameMonth!!)
        val adapter = ArrayAdapter(context!! , android.R.layout.simple_list_item_1 , allMasterDropDown?.RepaymentBank!!)
        addObligationDialog?.spinnerRepaymentBank?.setAdapter(adapter)
        addObligationDialog?.spinnerRepaymentBank?.setOnItemClickListener { parent , view , position , id ->
            addObligationDialog?.spinnerRepaymentBank?.tag = parent.getItemAtPosition(position) as DropdownMaster
        }


        allMasterDropDown?.RepaymentBank?.forEachIndexed { index , dropdownMaster ->
            if (dropdownMaster.typeDetailID == obligation.repaymentBankTypeDetailID) {
                (binding.spinnerRepaymentBank.setText(dropdownMaster.typeDetailDisplayText.toString() , false))
                binding.spinnerRepaymentBank.tag = dropdownMaster
                dropdownMaster
                return@forEachIndexed
            }
        }

        val dateUtil: DateUtil = DateUtil()

        selectMasterDropdownValue(binding!!.spinnerObligate , obligation.obligateTypeDetailID)
        selectMasterDropdownValue(binding.spinnerLoanOwnership , obligation.loanOwnershipTypeDetailID)
        selectMasterDropdownValue(binding.spinnerLoanType , obligation.loanTypeTypeDetailID)
        //selectMasterDropdownValue(binding.spinnerRepaymentBank, obligation.repaymentBankTypeDetailID)
        selectMasterDropdownValue(binding.spinnerEmiPaidInSameMonth , obligation.bounseEmiPaidInSameMonth)

        binding.etLoanAmount.setText(obligation.loanAmount.toString())
        binding.etFinancierName.setText(obligation.financerName.toString())
        binding.etAccountNum.setText(obligation.loanAccountNumber.toString())
        binding.etTenure.setText(obligation.tenure.toString())
        binding.etBalanceTenure.setText(obligation.balanceTenure.toString())
        binding.etLoanAmount.setText(obligation.loanAmount.toString())
        binding.etEmiAmount.setText(obligation.emiAmount.toString())
        binding.etBouncesInLastSixMonths.setText((obligation.numberOfBouncesInLastSixMonth.toString()))
        binding.etBouncesInLastNineMonths.setText((obligation.numberOfBouncesInLastNineMonth.toString()))
        binding.etDisbursementDate.setText(dateUtil.getFormattedDate(DateUtil.dateFormattingType.TYPE_API_REQUEST_2 , DateUtil.dateFormattingType.TYPE_NORMAL_1 , obligation.loanDisbursementDate))
        binding.btnAddObligation.setText(R.string.edit)




        addObligationDialog?.etDisbursementDate?.setOnClickListener() {
            SelectDate(addObligationDialog?.etDisbursementDate!! , context)
        }

        addObligationDialog?.cancel_bttn?.setOnClickListener() {

            addObligationDialog?.dismiss()
        }
        addObligationDialog?.btnAddObligation?.setOnClickListener() {


            if (formValidation.validateObligationDialog(binding)) {
                val dateUtil: DateUtil = DateUtil()
                val currentObligation = ObligationDetail()
                val loanOwnership = binding.spinnerLoanOwnership.selectedItem as DropdownMaster?
                val obligate = addObligationDialog?.spinnerObligate?.selectedItem as DropdownMaster?
                val loanType = addObligationDialog?.spinnerLoanType?.selectedItem as DropdownMaster?
                val repaymentBank = addObligationDialog?.spinnerRepaymentBank?.tag as DropdownMaster?
                val emiPaidInSameMonth = addObligationDialog?.spinnerEmiPaidInSameMonth?.selectedItem as DropdownMaster?
                currentObligation.numberOfBouncesInLastNineMonth = addObligationDialog?.etBouncesInLastNineMonths?.text.toString().toInt()
                currentObligation.numberOfBouncesInLastSixMonth = addObligationDialog?.etBouncesInLastSixMonths?.text.toString().toInt()
                currentObligation.financerName = addObligationDialog?.etFinancierName?.text.toString()
                currentObligation.loanAmount = CurrencyConversion().convertToNormalValue(addObligationDialog?.etLoanAmount?.text.toString()).toInt()
                currentObligation.emiAmount = CurrencyConversion().convertToNormalValue(addObligationDialog?.etEmiAmount?.text.toString()).toInt()
                currentObligation.loanAccountNumber = addObligationDialog?.etAccountNum?.text.toString()
                currentObligation.tenure = addObligationDialog?.etTenure?.text.toString().toInt()
                currentObligation.balanceTenure = addObligationDialog?.etBalanceTenure?.text.toString().toInt()
                currentObligation.loanOwnershipTypeDetailID = loanOwnership?.typeDetailID
                currentObligation.obligateTypeDetailID = obligate?.typeDetailID
                currentObligation.loanTypeTypeDetailID = loanType?.typeDetailID
                currentObligation.repaymentBankTypeDetailID = repaymentBank?.typeDetailID
                currentObligation.bounseEmiPaidInSameMonth = emiPaidInSameMonth?.typeDetailID
                currentObligation.loanDisbursementDate = dateUtil.getFormattedDate(DateUtil.dateFormattingType.TYPE_NORMAL_1 , DateUtil.dateFormattingType.TYPE_API_REQUEST_2 , addObligationDialog!!.etDisbursementDate.text.toString())

                obligationAdapter?.updateItem(position , currentObligation)
                addObligationDialog?.dismiss()
                obligationAdapter?.notifyDataSetChanged()


            } else {

                //showToast(context.getString(R.string.validation_error))
                Toast.makeText(context , context.getString(R.string.validation_error) , Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showAssetRecyclerView(flag: String) {
        if (flag.equals("asset")) {

            binding.llAssetDetail.visibility = View.VISIBLE
            binding.rcAsset.visibility = View.VISIBLE
            binding.layoutObligations.rcObligation.visibility = View.GONE
            binding.layoutCreditCard.rcCreditCard.visibility = View.GONE

        } else if (flag.equals("card")) {

            binding.layoutCreditCard.rcCreditCard.visibility = View.VISIBLE
            binding.rcAsset.visibility = View.GONE
            binding.layoutObligations.rcObligation.visibility = View.GONE


        } else {
            binding.layoutObligations.rcObligation.visibility = View.VISIBLE
            binding.layoutCreditCard.rcCreditCard.visibility = View.GONE
            binding.rcAsset.visibility = View.GONE

        }

    }


}
