package com.finance.app.viewModel

import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.finance.app.others.AppEnums
import com.finance.app.persistence.db.MasterDB
import com.finance.app.persistence.model.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AppDataViewModel(val activity: FragmentActivity, private val masterDB: MasterDB) : BaseViewModel(activity, masterDB) {
    private var liveSearchParam = MutableLiveData<String>()
    private var leadsPending = MutableLiveData<List<AllLeadMaster>>()
    private var leadsSubmitted = MutableLiveData<List<AllLeadMaster>>()
    private var leadsRejected = MutableLiveData<List<AllLeadMaster>>()
    private var leadsAll = MutableLiveData<List<AllLeadMaster>>()
   init {
        leadsPending.value = ArrayList()
        leadsSubmitted.value = ArrayList()
        leadsRejected.value = ArrayList()
        leadsAll.value = ArrayList()
    }
    fun getAllLeads(): LiveData<List<AllLeadMaster>?> {
        return masterDB.allLeadsDao().getAllLeads()
    }
    fun searchAllLead(status: String) : LiveData<List<AllLeadMaster>?>{
        var searchParam = liveSearchParam?.value?:""
        return masterDB.allLeadsDao().getAllLeads(searchParam = searchParam)
    }
      fun getLeadsByStatus(status: String): LiveData<List<AllLeadMaster>?> {
        return masterDB.allLeadsDao().getLeadsByStatus(status)
    }
    fun searchLeadByStatus(status : String) : LiveData<List<AllLeadMaster>?>{
        var searchParam = liveSearchParam?.value?:""
        System.out.println("searchParam>>>"+searchParam)
        System.out.println("leadStatus>>>"+status)

        return masterDB.allLeadsDao().getLeadsByStatus(status,searchParam)
    }
       fun getAllStates(): LiveData<List<StatesMaster>?> {
        return masterDB.statesDao().getAllStates()
    }

    fun getAllMasterDropdown(): LiveData<AllMasterDropDown?> {
        return masterDB.allMasterDropDownDao().getMasterDropdownValue()
    }

    fun getLoanProductMaster(): LiveData<List<LoanProductMaster>?> {
        return masterDB.loanProductDao().getAllLoanProduct()
    }


    fun getCoapplicantsList(leadId: Int): LiveData<CoApplicantsMaster?> {
        return masterDB.coApplicantsDao().getCoApplicants(leadId)
    }

    fun getLeadData(leadId: Int): LiveData<AllLeadMaster?> {
        return masterDB.allLeadsDao().getLead(leadId)
    }

    fun saveLead(lead: AllLeadMaster) {
        GlobalScope.launch {
            masterDB.allLeadsDao().insertLead(lead)
        }
    }
    private fun getLoanProductFromId(id: Int): LiveData<LoanProductMaster?> {
        return masterDB.loanProductDao().getLoanProductWithId(id)
    }

    private fun getStateNameFromId(id: Int): LiveData<StatesMaster?> {
        return masterDB.statesDao().getState(id)
    }


    fun getLoanProductNameFromId(id: Int?, tvLoanProduct: TextView) {
        id?.let {
            val loanProductLiveData = getLoanProductFromId(id)
            loanProductLiveData.observeForever {
                it?.let { lpMaster ->
                    tvLoanProduct.text = lpMaster.productName
                }
            }
        }
    }

    fun getLoanPurposeNameFromId(productId: Int?, purposeId: Int?, tvLoanPurpose: TextView) {
        productId?.let {
            purposeId?.let {

                val loanProductLiveData = getLoanProductFromId(productId)
                loanProductLiveData.observeForever {
                    it?.let {
                        val purposeList = it.loanPurposeList
                        purposeList?.let {
                            purposeList.forEach { purpose ->

                                if (purpose.loanPurposeID == purposeId) {
                                    tvLoanPurpose.text = purpose.loanPurposeName
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getStateNameFromId(id: Int?, field: TextView) {
        id?.let {
            val stateLiveData = getStateNameFromId(id)
            stateLiveData.observeForever {
                it?.let { state ->
                    field.text = state.stateName
                }
            }
        }
    }

    fun getMasterDropdownNameFromId(id: Int?, column: AppEnums.DropdownMasterType, field: TextView) {
        id?.let {
            val allMasterDropDown = getAllMasterDropdown()
            allMasterDropDown.observeForever { dropdown ->
                val masterDataMap = dropdown?.getMasterDropDownMap()
                masterDataMap?.let {
                    field.text = returnMasterName(id, column, masterDataMap)
                }
            }
        }
    }


    private fun returnMasterName(id: Int, column: AppEnums.DropdownMasterType, master: HashMap<AppEnums.DropdownMasterType,
     ArrayList<DropdownMaster>?>): String? {

        val col = master[column]
        col?.let {
            col.forEach { dd ->
                if (dd.typeDetailID == id) {
                    return dd.toString()
                }
            }
        }
        return null
    }
    private fun initLeadsData() {
        observeAllLeads(activity = activity,leadStatus = AppEnums.LEAD_TYPE.ALL,leadLiveData = leadsAll)
         observeAllLeadsByStatus(activity = activity, leadStatus = AppEnums.LEAD_TYPE.PENDING, leadLiveData = leadsPending)
        observeAllLeadsByStatus(activity = activity, leadStatus = AppEnums.LEAD_TYPE.SUBMITTED, leadLiveData = leadsSubmitted)
        observeAllLeadsByStatus(activity = activity, leadStatus = AppEnums.LEAD_TYPE.REJECTED, leadLiveData = leadsRejected)
    }


    fun setSearch(searchParam: String,pagerPosition : Int) {
          System.out.println("Position Pager>>>"+pagerPosition)
        liveSearchParam.value = searchParam
        if(pagerPosition==0) {
            observeAllLeadsByStatus(activity = activity, leadStatus = AppEnums.LEAD_TYPE.PENDING, leadLiveData = leadsPending)
        }
        else if(pagerPosition == 1) {
            observeAllLeadsByStatus(activity = activity, leadStatus = AppEnums.LEAD_TYPE.SUBMITTED, leadLiveData = leadsSubmitted)
        }
        else if (pagerPosition == 2) {
            observeAllLeadsByStatus(activity = activity, leadStatus = AppEnums.LEAD_TYPE.REJECTED, leadLiveData = leadsRejected)
        }
        else if (pagerPosition == 3) {
            observeAllLeads(activity = activity,leadStatus = AppEnums.LEAD_TYPE.ALL,leadLiveData = leadsAll)
        }

        //initLeadsData()
    }
    private fun observeAllLeads(activity: FragmentActivity,leadStatus: AppEnums.LEAD_TYPE, leadLiveData: MutableLiveData<List<AllLeadMaster>>) {

        searchAllLead(leadStatus.type).observe(activity, Observer { itemsFromDB ->
            itemsFromDB?.let {
                leadLiveData.value = itemsFromDB
            }
        })

       }

    private fun observeAllLeadsByStatus(activity: FragmentActivity, leadStatus: AppEnums.LEAD_TYPE, leadLiveData: MutableLiveData<List<AllLeadMaster>>) {

        searchLeadByStatus(leadStatus.type).observe(activity, Observer { itemsFromDB ->
            System.out.println("itemsFromDB>>>"+itemsFromDB?.size)
            itemsFromDB?.let {
                leadLiveData.value = itemsFromDB
            }
        })
    }
    fun getLeadsLiveDataByStatus(leadStatusEnums: AppEnums.LEAD_TYPE): MutableLiveData<List<AllLeadMaster>>? {
        return when(leadStatusEnums) {
            AppEnums.LEAD_TYPE.ALL -> leadsAll
            AppEnums.LEAD_TYPE.PENDING -> leadsPending
            AppEnums.LEAD_TYPE.SUBMITTED -> leadsSubmitted
            AppEnums.LEAD_TYPE.REJECTED -> leadsRejected
            else -> null
        }

    }
}