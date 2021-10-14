package com.finance.app.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.assent.Assent
import com.afollestad.assent.AssentCallback
import com.finance.app.R
import com.finance.app.databinding.ActivityLeadDetailBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.FollowUpResponse
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.adapters.recycler.adapter.LeadDetailActivityAdapter
import com.finance.app.viewModel.LeadDataViewModel
import motobeans.architecture.appDelegates.ViewModelType
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import javax.inject.Inject

@Suppress("DEPRECATION")
class LeadDetailActivity : BaseAppCompatActivity() {
    private val UPDATE_CALL_REQUEST = 1000
    private val presenter = Presenter()
    private val binding: ActivityLeadDetailBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_lead_detail
    )
    private val leadDataViewModel: LeadDataViewModel by motobeans.architecture.appDelegates.viewModelProvider(this, ViewModelType.WITH_DAO)
    @Inject
    lateinit var dataBase: DataBaseUtil
    private var bundle: Bundle? = null
    private var lead: AllLeadMaster? = null
    private var isSelectedLeadSynced = false
    private var leadContact: Long? = null
    private var allMasterDropDown: AllMasterDropDown? = null
    private var followupResponse : ArrayList<FollowUpResponse> ? = null
    companion object {
        private const val KEY_LEAD = "leadApplicant"
        fun start(context: Context, lead: AllLeadMaster) {
            val intent = Intent(context, LeadDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(KEY_LEAD, lead)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        hideToolbar()
        hideSecondaryToolbar()

        //Initilise
        fetchSpinnerDataFromDB()
        getLead()
    }
    private fun getLead() {
        bundle = intent.extras
        bundle?.let {
            val leadBundleData = bundle?.getSerializable(KEY_LEAD)
            leadBundleData?.let {
                lead = leadBundleData as AllLeadMaster
                lead?.let {
                    useLeadData(it)
                    syncLeadMetaData(it.leadID)
                    leadDataViewModel.getLeadData(it)

                }
            }
        }
    }
    private fun syncLeadMetaData(id: Int?) {
        id?.let {
            dataBase.provideDataBaseSource().allLeadsDao().getLead(it).observeForever { lead ->
                lead?.let { leadDetails ->
                    isSelectedLeadSynced = true
                    LeadMetaData.setLeadData(leadDetails)
                }
            }
        }
//        id?.let {
//            LeadMetaData().getAndPopulateLeadData(id)
//        }
    }
    private fun useLeadData(lead: AllLeadMaster) {
        fillLeadDetail(lead)
        //setUpRecyclerView()
        setClickListeners(lead)
        fillColor(lead)
    }
    private fun fillLeadDetail(lead: AllLeadMaster) {
        val leadName = lead.applicantFirstName + " " + lead.applicantLastName
        binding.header.tvLeadNumber.text = lead.leadNumber
        binding.tvLeadName.text = leadName
        binding.tvEmail.text = lead.applicantEmail
        binding.tvLocation.text = lead.applicantAddress
        binding.tvPhone.text = lead.applicantContactNumber
        binding.tvTypeOfLoan.text = lead.loanProductName
        binding.tvLeadStatus.text = lead.status
        leadContact = if (lead.applicantAlternativeContactNumber.isNullOrBlank().not()) lead.applicantContactNumber?.toLong() else null
        setLeadNum(lead.leadNumber)
        if(lead.status == "Submitted"){
            binding.btnUpdateApplication.setText("VIEW APPLICATION")
        }
    }
    private fun fillColor(lead: AllLeadMaster) {
        when (lead.status) {
            AppEnums.LEAD_TYPE.PENDING.type -> binding.tvLeadStatus.setTextColor(resources.getColor(R.color.lead_status_pending))
            AppEnums.LEAD_TYPE.SUBMITTED.type -> binding.tvLeadStatus.setTextColor(resources.getColor(R.color.lead_status_submitted))
            AppEnums.LEAD_TYPE.REJECTED.type -> binding.tvLeadStatus.setTextColor(resources.getColor(R.color.lead_status_rejected))
            else -> binding.tvLeadStatus.setTextColor(resources.getColor(R.color.lead_status_new))
        }
    }
    private fun showAlert() {
                AlertDialog.Builder(this)
                        .setTitle(getString(R.string.alert_warning))
                        .setMessage(getString(R.string.alert_message))
                        .setCancelable(false)
                        .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                        .setPositiveButton("ok") { _, _ ->
                            val intent = Intent(this, CreateLeadActivity::class.java)
                            this.startActivity(intent)
                        }.show()
    }
    private fun setClickListeners(lead: AllLeadMaster) {
        binding.header.lytBack.setOnClickListener { onBackPressed() }
        binding.btnUpdateApplication.setOnClickListener {
           if (LeadMetaData.getLeadData()?.status == "Rejected") {
                showAlert()
               //checkAndStartLoanApplicationActivity(lead)
            }
            else{
            checkAndStartLoanApplicationActivity(lead)
            }
            //checkAndStartLoanApplicationActivity(lead)
        }
        binding.ivPhone.setOnClickListener {
            Assent.requestPermissions(AssentCallback { result ->
                //start location tracker service
                if (result.allPermissionsGranted()) {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel: +91${lead.applicantContactNumber}")
                    startActivity(callIntent)
                }
            } , 1 , Assent.CALL_PHONE)
        }
        binding.btnUpdateCall.setOnClickListener {
            if(LeadMetaData.getLeadData()?.status == "Rejected"){
                showAlert()
            }else{
                UpdateCallActivity.startActivityForResult(this, lead, UPDATE_CALL_REQUEST)
            }
        }
        binding.btnAddTask.setOnClickListener {
            AddTaskActivity.start(this)
        }
        binding.ivEdit.setOnClickListener(){
            if (LeadMetaData.getLeadData()?.status == "Submitted") {
                showToast("Submitted Lead can't be edit")
            }else if(LeadMetaData.getLeadData()?.status == "Rejected")
            {
                showAlert()
            }
            else {
                val intent = Intent(this, CreateLeadActivity::class.java)
                intent.putExtra("key_id", LeadMetaData.getLeadId())
                this.startActivity(intent)
            }
        }
    }
    private fun checkAndStartLoanApplicationActivity(lead: AllLeadMaster) {
        val isLeadInfoAlreadySync = leadDataViewModel.isAllApiCallCompleted.value ?: false
        val isLeadOfflineDataSync = lead.isDetailAlreadySync
        when (isSelectedLeadSynced && (isLeadInfoAlreadySync || isLeadOfflineDataSync)) {
            true -> checkAndGoToNextScreen(lead)
            false -> showToast("We are trying to sync...")
        }
    }
    private fun checkAndGoToNextScreen(lead: AllLeadMaster) {
        lead.leadID?.let {
            LoanApplicationActivity.start(this)
        }
    }
    private fun getFollowUpData() {
        lead?.leadID?.let { presenter.callNetwork(ConstantsApi.CALL_FOLLOWUP, CallFollowUP(it)) }
    }
    private fun setUpRecyclerView(list: ArrayList<FollowUpResponse>) {
        binding.rcActivities.layoutManager = LinearLayoutManager(this)
        binding.rcActivities.adapter = LeadDetailActivityAdapter(this, list, allMasterDropDown)
    }
    private fun fetchSpinnerDataFromDB() {
        binding.progressBar.visibility = View.VISIBLE
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(this, Observer { masterDrownDownValues ->
            masterDrownDownValues?.let {
                allMasterDropDown = it
                getFollowUpData()
            }
        })
    }
    inner class CallFollowUP(val leadId: Int) : ViewGeneric<Requests.RequestFollowUp, Response.ResponseFollowUp>(context = this) {
        override val apiRequest: Requests.RequestFollowUp?
            get() = Requests.RequestFollowUp(leadId)
        override fun getApiSuccess(value: Response.ResponseFollowUp) {
            followupResponse?.clear()
            value.responseObj?.let { list ->
                binding.progressBar.visibility = View.GONE
                followupResponse = list
                setUpRecyclerView(list)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UPDATE_CALL_REQUEST && resultCode == Activity.RESULT_OK) {
            getFollowUpData()
        }
    }
}
