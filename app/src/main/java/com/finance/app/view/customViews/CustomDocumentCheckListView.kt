package com.finance.app.view.customViews

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.CustomviewDocumentchecklistBinding
import com.finance.app.databinding.LayoutCustomviewAssetliabilityBinding
import com.finance.app.persistence.model.*
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.adapters.recycler.adapter.AssetDetailAdapter
import com.finance.app.view.adapters.recycler.adapter.CheckListAdapter
import com.finance.app.view.adapters.recycler.adapter.DocumentCheckListAdapter
import com.finance.app.view.utils.getTypeDetailId
import kotlinx.android.synthetic.main.activity_update_call.view.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.util.AppUtilExtensions
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import javax.inject.Inject

class CustomDocumentCheckListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), CheckListAdapter.CheckListClickListener {
    private lateinit var binding: CustomviewDocumentchecklistBinding
    private lateinit var activity: FragmentActivity
    private var selectedApplicant: PersonalApplicantsModel? = null
    private var checklistAdapter: CheckListAdapter? = null
    @Inject
    lateinit var dataBase: DataBaseUtil
    //private var leadDetail: AllDocumentCheckListMaster? = null
    private var documentDetail: AllDocumentCheckListMaster? = null
    private var allMasterDropDown: AllMasterDropDown? = null


    init {
        ArchitectureApp.instance.component.inject(this)
        binding = AppUtilExtensions.initCustomViewBinding(context = context, layoutId = R.layout.customview_documentchecklist, container = this)
        initViews()
    }

    private fun initViews() {
        //Fetch Document details from database..
        //fetchDocumentsDetails()
    }

    fun initApplicantDetails(it: FragmentActivity, applicant: PersonalApplicantsModel) {
        selectedApplicant = applicant
        activity = it
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(activity, Observer { masterDrownDownValues ->
            masterDrownDownValues?.let {
                allMasterDropDown = it

            }
        })
        fetchDocumentDetails()

    }


    private fun fetchDocumentDetails() {
        LeadMetaData.getLeadObservable().observe(activity, Observer {
            it?.let { documentDetails ->
                val selectedApplicantDocumentDetails = documentDetails.documentData.documentDetailList.filter { documentDetail -> documentDetail.leadApplicantNumber.equals(selectedApplicant?.leadApplicantNumber, true) }
                if (selectedApplicantDocumentDetails.isNotEmpty())
                    selectedApplicantDocumentDetails[0].checklistDetails?.let { it1 -> setDocumentCheckListAdapter(it1) }
                else {
                    System.out.println("LoanId>>>>"+LeadMetaData.getLeadData()?.loanProductID)
                    val documentList = dataBase.provideDataBaseSource().allDocumentDao().getDocumentCheckList(LeadMetaData.getLeadData()?.loanProductID)
                    documentList.observeForever {
                        it?.let { documentMaster ->
                            setDocumentCheckListAdapter(documentMaster.checklistDetails)
                            documentDetail = documentMaster
                            System.out.println("DocumentCheckliSt>>>>"+documentDetail!!.checklistDetails[0].typeDetailId)
                        }
                    }
                }
            }
        })
    }

    //get current applicant
    fun getCurrentApplicant(): DocumentCheckList {
        var currentApplicant: DocumentCheckList = DocumentCheckList()
        return currentApplicant

    }

    private fun setDocumentCheckListAdapter(checkList: ArrayList<DocumentCheckListDetailModel>) {
        binding.recyclerviewChecklist.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        checklistAdapter = CheckListAdapter(context, checkList)
        binding.recyclerviewChecklist.adapter = checklistAdapter
        //checklistAdapter.addItem()
        checklistAdapter?.setOnCheckListClickListener(this)

    }

    fun getDocumentChecklist(): DocumentCheckList {
        val documentCheckList = DocumentCheckList()
        documentCheckList.productID = documentDetail?.productID
        documentCheckList.productName = documentDetail?.productName
        documentCheckList.isMainApplicant = selectedApplicant?.isMainApplicant!!

        documentCheckList.leadApplicantNumber = selectedApplicant?.leadApplicantNumber
        val updatedList = checklistAdapter?.getItemList()?.map { item ->
            if (item.selectedCheckListValue == ChecklistAnswerType.YES ) {
                item.typeDetailId = allMasterDropDown?.ReviewerResponseType?.getTypeDetailId(item?.selectedCheckListValue.toString())
                System.out.println("typeDetailId>>>>Yes"+item.typeDetailId)

                item.typeDetailDisplayText = item?.selectedCheckListValue.toString()
            } else if (item.selectedCheckListValue == ChecklistAnswerType.NO) {
                item.typeDetailId = allMasterDropDown?.ReviewerResponseType?.getTypeDetailId(item?.selectedCheckListValue.toString())
                System.out.println("typeDetailId>>>>No"+item.typeDetailId)
                item.typeDetailDisplayText = item?.selectedCheckListValue.toString()
            } else if (item.selectedCheckListValue == ChecklistAnswerType.NA) {
                item.typeDetailId = allMasterDropDown?.ReviewerResponseType?.getTypeDetailId(item?.selectedCheckListValue.toString())
                System.out.println("typeDetailId>>>>NA"+item.typeDetailId)
                item.typeDetailDisplayText = item?.selectedCheckListValue.toString()
            }
            item
        }

        documentCheckList.checklistDetails = if(updatedList.isNullOrEmpty()) ArrayList() else ArrayList(updatedList)
        System.out.println("documentCheckList"+documentCheckList.checklistDetails.size)
                //if (updatedList.isNullOrEmpty()) ArrayList() else ArrayList(updatedList)
        return documentCheckList
    }
    fun isDocumentDetailsValid() : Boolean{
        var checkedError = false
        var errorCount = 0

        for (i in 0 until getDocumentChecklist()?.checklistDetails.size){
            System.out.println("Checked error>>>value"+getDocumentChecklist()?.checklistDetails?.get(i)?.typeDetailDisplayText)

            if(getDocumentChecklist()?.checklistDetails?.get(i)?.typeDetailDisplayText.exIsNotEmptyOrNullOrBlank()){

            }
            else
            {
                errorCount++
            }

            //checkedError = !getDocumentChecklist()?.checklistDetails?.get(i)?.typeDetailDisplayText.equals("null")
            System.out.println("Checked error>>>"+checkedError)

            //checkedError = getDocumentChecklist()?.checklistDetails?.get(i)?.typeDetailDisplayText != null

            //checkedError = getDocumentChecklist()?.checklistDetails?.get(i)?.typeDetailDisplayText != null
        }
        checkedError = errorCount == 0
        System.out.println("Checked error>>>Final"+checkedError)

        return checkedError
    }


}