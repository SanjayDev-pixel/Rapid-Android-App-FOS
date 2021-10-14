package com.finance.app.view.customViews

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.app.R
import com.finance.app.databinding.LayoutKycFormBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.persistence.model.KYCDetail
import com.finance.app.utility.ConvertDate
import com.finance.app.utility.LeadMetaData
import com.finance.app.utility.SelectDate
import com.finance.app.view.activity.DocumentUploadingActivity
import com.finance.app.view.adapters.recycler.adapter.KycListAdapter
import com.finance.app.view.adapters.recycler.spinner.MasterSpinnerAdapter
import com.finance.app.view.utils.setSelectionFromList
import kotlinx.android.synthetic.main.delete_dialog.view.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.util.AppUtilExtensions
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class KycFormView @JvmOverloads constructor(context: Context , attrs: AttributeSet? = null , defStyleAttr: Int = 0) : LinearLayout(context , attrs , defStyleAttr) , KycListAdapter.ItemClickListener {

    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var formValidation: FormValidation

    private var lifecycleOwner: LifecycleOwner? = null
    private var allMasterDropDown: AllMasterDropDown? = null
    private var kycListAdapter: KycListAdapter? = null

    private var currentApplicantNumber: String? = null
    private var selectedKycDetailPosition = -1
    private var generatedKycDocumentId: String? = null

    private var rootBinding: LayoutKycFormBinding = AppUtilExtensions.initCustomViewBinding(context = context , layoutId = R.layout.layout_kyc_form , container = this)

    fun bindApplicantKycDetails(owner: LifecycleOwner , applicantNumber: String , kycDetailList: ArrayList<KYCDetail>) {
        ArchitectureApp.instance.component.inject(this)
        lifecycleOwner = owner
        currentApplicantNumber = applicantNumber

        initViews()


        //Set Kyc Adapter..
        setKycDetailListAdapter(kycDetailList)
        //fetch spinners data from db...
        fetchSpinnersDataFromDB()
        //Check whether kyc list has items or not....
        shouldDisplayKycListViews()
        setOnClickListener(kycDetailList)
        //For submited record
        disableView()

      /*  rootBinding.spinnerIdentificationType.onItemSelectedListener = object :  AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>? , view: View? , position: Int , id: Long) {
                if(position>=0)
                {
                    val identificationTypeSelected = parent?.selectedItem as DropdownMaster

                    System.out.println("Sanjay>>>>"+identificationTypeSelected)
                   *//* val lead = LeadMetaData.getLeadData()
                    for (i in 0 until lead?.personalData?.applicantDetails!!.size) {
                           val selectedIdentificationTypeMain = lead.personalData.applicantDetails[i].applicantKycList!![i].identificationTypeDetailID
                        System.out.println("selectedIdentificationTypeMain>>>"+selectedIdentificationTypeMain)

                    }*//*

                    *//*allMasterDropDown?.IdentificationType?.let { list -> kycDetailList[position].identificationTypeDetailID?.let { id -> rootBinding.spinnerIdentificationType.setSelectionFromList(list , id) } }*//*
                }
            }



        }*/

    }

    private fun disableView() {
        LeadMetaData.getLeadData()?.let {
            if (it.status.equals(AppEnums.LEAD_TYPE.SUBMITTED.type , true)) {
                rootBinding.imageAddKYC.visibility = View.INVISIBLE
                rootBinding.vwAdd.isEnabled = false
            } else {
                rootBinding.imageAddKYC.visibility = View.VISIBLE
                rootBinding.vwAdd.isEnabled = true
            }
        }
    }

    private fun initViews() {
        rootBinding.etIdNum.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
    }

    private fun setOnClickListener(list: ArrayList<KYCDetail>) {
        rootBinding.vwAdd.setOnClickListener {
            generatedKycDocumentId = Date().time.toString()
            showKycForm(true)
        }
        rootBinding.btnAddKYC.setOnClickListener {

            var checkError = 0


                if (isKycDetailsValid()) {
                    for(i in 0 until list.size)
                    {
                        if((rootBinding.etIdNum.text.toString()).equals(list[i].identificationNumber))
                        {
                            System.out.println("Sanjay Matched>>>"+list[i].identificationNumber)
                            rootBinding.etIdNum.error = "Identification number is duplicate"
                            checkError =1
                        }
                    }
                    if(checkError ==0) {
                        addOrUpdateKycDetails();showKycForm(false)
                    }
                }

        }
        rootBinding.btnUpdateKYC.setOnClickListener {
            var checkError = 0
            if (isKycDetailsValid()) {
                for(i in 0 until list.size)
                {
                    if((rootBinding.etIdNum.text.toString()).equals(list[i].identificationNumber))
                    {
                        System.out.println("Sanjay Matched>>>"+list[i].identificationNumber)
                        rootBinding.etIdNum.error = "Identification number is duplicate"
                        checkError =1
                    }
                }
                if(checkError ==0) {
                    addOrUpdateKycDetails(true);showKycForm(false)
                }

            }
        }
        rootBinding.btnCancel.setOnClickListener { showKycForm(false) }
        rootBinding.btnUploadKyc.setOnClickListener {
            if (isKycDetailsValid()) {
                allMasterDropDown?.let {
                    val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.KYC_DOCUMENT , true) }
                    val bundle = Bundle()
                    bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)//Hardcoded for KYC proof...
                    bundle.putString(Constants.KEY_TITLE , context.getString(R.string.kyc))
                    bundle.putString(Constants.KEY_APPLICANT_NUMBER , currentApplicantNumber)
                    bundle.putString(Constants.KEY_FORM_ID , generatedKycDocumentId)
                    DocumentUploadingActivity.startActivity(context , bundle)
                }
            }
        }
        rootBinding.etIssueDate.setOnClickListener { SelectDate(rootBinding.etIssueDate , context , maxDate = Date().time) }
        rootBinding.etExpiryDate.setOnClickListener { SelectDate(rootBinding.etExpiryDate , context , minDate = Date().time) }
    }

    private fun setKycDetailListAdapter(list: ArrayList<KYCDetail>) {
        kycListAdapter = KycListAdapter(context , list)
        kycListAdapter?.setOnItemClickListener(this)
        rootBinding.rcKycList.layoutManager = LinearLayoutManager(context , LinearLayoutManager.HORIZONTAL , false)
        rootBinding.rcKycList.adapter = kycListAdapter
        lifecycleOwner?.let {
            kycListAdapter?.getItemCountObserver()?.observe(it , Observer { listCount ->
                if (listCount > 0) {
                    showKycListView(true);showKycEmptyView(false)
                } else {
                    showKycListView(false);showKycEmptyView(true)
                }
            })
        }
    }

    private fun setKycSpinner(allMasterDropDown: AllMasterDropDown) {
        rootBinding.spinnerIdentificationType.adapter = MasterSpinnerAdapter(context , allMasterDropDown.IdentificationType!!)
        rootBinding.spinnerVerifiedStatus.adapter = MasterSpinnerAdapter(context , allMasterDropDown.VerifiedStatus!!)
        rootBinding.spinnerVerifiedStatus.setSelection(2)
        rootBinding.spinnerVerifiedStatus.isEnabled = false
        //Click Listner for SpinnerIdentificationType
        // binding.spinnerSalaryCredit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {


        }



    private fun showKycForm(show: Boolean , isUpdateKycForm: Boolean = false) {
        rootBinding.vwKycForm.visibility = if (show) View.VISIBLE else View.GONE
        rootBinding.btnUpdateKYC.visibility = if (isUpdateKycForm) View.VISIBLE else View.GONE
        rootBinding.btnAddKYC.visibility = if (isUpdateKycForm) View.GONE else View.VISIBLE

        if (show.not() || rootBinding.vwKycForm.isVisible) clearForm()
    }

    private fun showKycListView(show: Boolean) {
        rootBinding.rcKycList.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showKycEmptyView(show: Boolean) {
        rootBinding.vwEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun shouldDisplayKycListViews() {
        kycListAdapter?.let { adapter ->
            if (adapter.itemCount > 0) {
                showKycListView(true);showKycEmptyView(false)
            } else {
                showKycListView(false);showKycEmptyView(true)
            }
        }
    }

    private fun fetchSpinnersDataFromDB() {
        lifecycleOwner?.let { lifecycleOwner ->
            dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(lifecycleOwner , Observer { masterDrownDownValues ->
                masterDrownDownValues?.let {
                    allMasterDropDown = it
                    setKycSpinner(it)
                }
            })
        }
    }

    private fun fillKycDetails(kycDetail: KYCDetail) {
        generatedKycDocumentId = kycDetail.applicationDocumentID
        rootBinding.etIdNum.setText(kycDetail.identificationNumber)
        rootBinding.etIssueDate.setText(ConvertDate().convertToAppFormat(kycDetail.issueDate))
        rootBinding.etExpiryDate.setText(ConvertDate().convertToAppFormat(kycDetail.expireDate))
        allMasterDropDown?.IdentificationType?.let { list -> kycDetail.identificationTypeDetailID?.let { id -> rootBinding.spinnerIdentificationType.setSelectionFromList(list , id) } }
//        allMasterDropDown?.VerifiedStatus?.let { list -> kycDetail.verifiedStatusTypeDetailID?.let { id -> rootBinding.spinnerVerifiedStatus.setSelectionFromList(list, id) } }
    }

    private fun showKycDetailConfirmDeleteDialog() {
        val deleteDialogView = LayoutInflater.from(context).inflate(R.layout.delete_dialog , null)
        val mBuilder = AlertDialog.Builder(context)
                .setView(deleteDialogView)
                .setTitle("Delete Kyc Detail")
        val deleteDialog = mBuilder.show()
        deleteDialogView.tvDeleteConfirm.setOnClickListener {
            onDeleteKycDetail()
            deleteDialog.dismiss()
        }
        deleteDialogView.tvDonotDelete.setOnClickListener { deleteDialog.dismiss() }
    }

    private fun addOrUpdateKycDetails(shouldUpdate: Boolean = false) {
        val kycDetail = KYCDetail()

        if (shouldUpdate.not()) kycDetail.applicationDocumentID = generatedKycDocumentId
        else kycDetail.applicationDocumentID = kycListAdapter?.getItem(selectedKycDetailPosition)?.applicationDocumentID

        kycDetail.identificationTypeDetailID = (rootBinding.spinnerIdentificationType.selectedItem as DropdownMaster?)?.typeDetailID
        kycDetail.identificationTypeDetail = (rootBinding.spinnerIdentificationType.selectedItem as DropdownMaster?)?.typeDetailDisplayText
        kycDetail.identificationNumber = rootBinding.etIdNum.text.toString()
        kycDetail.issueDate = ConvertDate().convertToApiFormat(rootBinding.etIssueDate.text.toString())
        kycDetail.expireDate = ConvertDate().convertToApiFormat(rootBinding.etExpiryDate.text.toString())
        kycDetail.verifiedStatusTypeDetailID = (rootBinding.spinnerVerifiedStatus.selectedItem as DropdownMaster?)?.typeDetailID
        kycDetail.verifiedStatusTypeDetail = (rootBinding.spinnerVerifiedStatus.selectedItem as DropdownMaster?)?.typeDetailDisplayText

        if (shouldUpdate.not())
        {

            kycListAdapter?.addItem(kycDetail)
        }
        else kycListAdapter?.updateItem(selectedKycDetailPosition , kycDetail)
    }

    override fun onKycDetailDeleteClicked(position: Int) {
        selectedKycDetailPosition = position
        showKycDetailConfirmDeleteDialog()
    }

    override fun onKycDetailEditClicked(position: Int , kycDetail: KYCDetail) {
        selectedKycDetailPosition = position
        showKycForm(show = true , isUpdateKycForm = true)
        fillKycDetails(kycDetail)
    }

    private fun onDeleteKycDetail() {
        kycListAdapter?.deleteItem(selectedKycDetailPosition)
    }

    private fun clearForm() {
        rootBinding.etIdNum.setText("")
        rootBinding.etExpiryDate.setText("")
        rootBinding.etIssueDate.setText("")
//        rootBinding.spinnerVerifiedStatus.setSelection(0)
        rootBinding.spinnerIdentificationType.setSelection(0)
    }

    private fun isKycDetailsValid() = formValidation.validateKycDetail(rootBinding)

    fun getKycDetailsList(): ArrayList<KYCDetail> = kycListAdapter?.getItemList() ?: ArrayList()

}