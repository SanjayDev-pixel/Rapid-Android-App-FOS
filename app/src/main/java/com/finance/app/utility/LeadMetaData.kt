package com.finance.app.utility

import androidx.lifecycle.MutableLiveData
import com.finance.app.eventBusModel.AppEvents
import com.finance.app.persistence.model.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.development.interfaces.DataBaseUtil
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class LeadMetaData : Observable() {
    @Inject
    lateinit var dataBase: DataBaseUtil
    init {
        ArchitectureApp.instance.component.inject(this)
    }
    fun getAndPopulateLeadData(leadId: Int) {
        dataBase.provideDataBaseSource().allLeadsDao().getLead(leadId).observeForever { lead ->
            setLeadData(lead) //Bug: return null frequently..
        }
    }
    companion object {
        private var leadData = MutableLiveData<AllLeadMaster?>()
        fun setLeadData(leadDetail: AllLeadMaster?) {
            leadData.value = leadDetail
            System.out.println("lead Data>>>>" + leadData.value)
        }
        fun getLeadObservable() = leadData
        fun getLeadData() = leadData.value
        fun getLeadId() = leadData.value?.leadID
    }
     fun updateLeadStatusIntoDB(status : String , leadId: Int?){
         GlobalScope.launch {
            dataBase.provideDataBaseSource().allLeadsDao().updateLeadStatus(status,leadId)
        }
    }
    private fun insertLeadInfoIntoDB(lead: AllLeadMaster): Job {
        return GlobalScope.launch {
            lead.isSyncWithServer = false
            dataBase.provideDataBaseSource().allLeadsDao().insertLead(lead)
            AppEvents.fireEventBackgroundSync(AppEvents.BackGroundSyncEvent.LEAD_SYNC)
        }
    }

    fun saveLoanData(data: LoanInfoModel) {
        val lead = getLeadData()
        lead?.let {
            lead.loanData = data
            insertLeadInfoIntoDB(lead)
        }
    }

    fun savePersonalData(applicants: ArrayList<PersonalApplicantsModel>) {
        val lead = getLeadData()
        lead?.let {
            lead.personalData.applicantDetails = applicants
            insertLeadInfoIntoDB(lead)
        }
    }


    fun saveEmploymentData(applicants: ArrayList<EmploymentApplicantsModel>) {
        val lead = getLeadData()
        lead?.let {
            lead.employmentData.applicantDetails = applicants
            insertLeadInfoIntoDB(lead)
        }
    }

    fun saveBankData(bankDetailsList: ArrayList<BankDetailModel>) {
        val lead = getLeadData()
        lead?.let {
            lead.bankData.bankDetailList = bankDetailsList
            insertLeadInfoIntoDB(lead)
        }
    }

    fun saveReferenceData(referenceDetailsList: ArrayList<ReferenceModel>) {
        val lead = getLeadData()
        lead?.let {
            lead.referenceData.loanApplicationObj = referenceDetailsList
            insertLeadInfoIntoDB(lead)
        }
    }

    fun saveAssetLiabilityData(assetsAndLiability: ArrayList<AssetLiabilityModel>) {
        val lead: AllLeadMaster? = getLeadData()
        lead?.let {
            lead.assetLiabilityData?.loanApplicationObj = assetsAndLiability
            insertLeadInfoIntoDB(lead)
        }
    }

    fun savePropertyData(leadPropertyData: PropertyModel) {
        val lead: AllLeadMaster? = getLeadData()
        lead?.let {
            lead.propertyData = leadPropertyData
            insertLeadInfoIntoDB(lead)

        }
    }
    fun saveDocumentData(documentListData: ArrayList<DocumentCheckList>) {
        val lead = getLeadData()
        val leadId = getLeadId()
        lead?.let {
            lead.documentData.leadID = leadId
            lead.documentData.documentDetailList = documentListData
            insertLeadInfoIntoDB(lead)
        }
    }

}