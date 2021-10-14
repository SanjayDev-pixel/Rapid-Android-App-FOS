package com.finance.app.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.databinding.ActivityUpdateCallBinding
import com.finance.app.others.AppEnums
import com.finance.app.others.setTextVertically
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.utility.ConvertDate
import com.finance.app.utility.SelectDate
import com.finance.app.view.adapters.recycler.spinner.MasterSpinnerAdapter
import kotlinx.android.synthetic.main.activity_update_call.*
import kotlinx.android.synthetic.main.layout_fixed_meeting.view.*
import kotlinx.android.synthetic.main.layout_follow_up.view.*
import kotlinx.android.synthetic.main.layout_not_interested.view.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.DateUtil
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import java.util.*
import javax.inject.Inject
import kotlinx.android.synthetic.main.layout_fixed_meeting.view.etMessage as etMessageFixMeeting
import kotlinx.android.synthetic.main.layout_follow_up.view.etMessage as etMessageFollowUp

class UpdateCallActivity : BaseAppCompatActivity() {
    enum class RequestLayout {
        FOLLOW_UP, FIX_MEETING, NOT_INTERESTED, NOTHING
    }

    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var formValidation: FormValidation

    private var bundle: Bundle? = null
    private var leadDetails: AllLeadMaster? = null
    private var allMasterDropDown: AllMasterDropDown? = null
    private val presenter = Presenter()
    private var selectedLayoutType: RequestLayout = RequestLayout.NOTHING
    // used to bind element of layout to activity
    private val binding: ActivityUpdateCallBinding by ActivityBindingProviderDelegate(this, R.layout.activity_update_call)


    companion object {
        private const val KEY_LEAD = "leadApplicant"
        fun start(context: Context, lead: AllLeadMaster) {
            val intent = Intent(context, UpdateCallActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(KEY_LEAD, lead)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        fun startActivityForResult(context: Context, lead: AllLeadMaster, requestCode: Int? = null) {
            val intent = Intent(context, UpdateCallActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(KEY_LEAD, lead)
            intent.putExtras(bundle)
            if (context is Activity) requestCode?.let { context.startActivityForResult(intent, it) }
            else if (context is Fragment) requestCode?.let { context.startActivityForResult(intent, it) }
        }
    }

    inner class CallUpdateRequest : ViewGeneric<Requests.RequestCallUpdate, Response.ResponseCallUpdate>(context = this) {
        override val apiRequest: Requests.RequestCallUpdate?
            get() = getCallUpdateRequest()

        override fun getApiSuccess(value: Response.ResponseCallUpdate) {
            Toast.makeText(this@UpdateCallActivity, value.responseMsg, Toast.LENGTH_LONG).show()
            if (value.responseCode == Constants.SUCCESS) {
                setResult(Activity.RESULT_OK)
                finish()
            }

        }

        override fun getApiFailure(msg: String) {
            Toast.makeText(this@UpdateCallActivity, msg, Toast.LENGTH_LONG).show()

        }
    }

    private fun getLeadDetailsFromBundle() {
        bundle = intent.extras
        bundle?.let {
            leadDetails = it.getSerializable(KEY_LEAD) as AllLeadMaster
            setLeadDetailsToViews(it.getSerializable(KEY_LEAD) as AllLeadMaster)
        }
    }

    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        hideToolbar()
        hideSecondaryToolbar()
        setClickListeners()
        //Now fetch details from where-ever need...
        fetchSpinnersDataFromDB()
        getLeadDetailsFromBundle()
    }

    private fun setClickListeners() {
        binding.layoutFollowUp.btnUpdateFollowUpLead.setOnClickListener { if (selectedLayoutType != RequestLayout.NOTHING && formValidation.validateUpdateCallForm(binding, selectedLayoutType)) postFollowUpStatus() }
        binding.layoutFixedMeeting.btnUpdateFixedMeeting.setOnClickListener { if (selectedLayoutType != RequestLayout.NOTHING && formValidation.validateUpdateCallForm(binding, selectedLayoutType)) postFollowUpStatus() }
        binding.layoutNotInterested.btnCloseLead.setOnClickListener { if (selectedLayoutType != RequestLayout.NOTHING && formValidation.validateUpdateCallForm(binding, selectedLayoutType)) postFollowUpStatus() }

        binding.header.lytBack.setOnClickListener { onBackPressed() }
        binding.layoutFollowUp.etFollowUpTiming.setOnClickListener {
            SelectDate(binding.layoutFollowUp.etFollowUpTiming, this@UpdateCallActivity, minDate = Date().time)
        }
        binding.layoutFixedMeeting.etMeetingDate.setOnClickListener {
            SelectDate(binding.layoutFixedMeeting.etMeetingDate, this@UpdateCallActivity, minDate = Date().time)
        }
    }

    private fun setCallStatusSpinnerAdapter(customerFollowUpStatus: ArrayList<DropdownMaster>) {
        binding.spinnerCallStatus.adapter = MasterSpinnerAdapter(getContext(), customerFollowUpStatus)
        binding.spinnerCallStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override
            fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                switchViewByCustomerStatus(position)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }
    }

    private fun setLeadTypeSpinnerAdapter(leadTypes: ArrayList<DropdownMaster>) {
        layoutFollowUp.spinnerLeadType.adapter = MasterSpinnerAdapter(getContext(), leadTypes)
        layoutFollowUp.spinnerLeadType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override
            fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
//                switchViewByCustomerStatus(position)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }
    }

    private fun setLeadNotificationTypeSpinnerAdapter(notificationTypes: ArrayList<DropdownMaster>) {
        layoutFixedMeeting.spinnerNotificationType.adapter = MasterSpinnerAdapter(getContext(), notificationTypes)
        layoutFixedMeeting.spinnerNotificationType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override
            fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
//                switchViewByCustomerStatus(position)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }
    }

    private fun setLeadRejectionTypeSpinnerAdapter(rejectionTypes: ArrayList<DropdownMaster>) {
        layoutNotInterested.spinnerLeadCloseReason.adapter = MasterSpinnerAdapter(getContext(), rejectionTypes)
        layoutNotInterested.spinnerLeadCloseReason.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override
            fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
//                switchViewByCustomerStatus(position)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setLeadDetailsToViews(lead: AllLeadMaster) {
        binding.header.tvLeadNumber.text = lead.leadNumber
        binding.leadDetails.tvStatusLine.setTextVertically(lead.status)
        binding.leadDetails.tvLeadName.text = lead.applicantFirstName
        binding.leadDetails.tvLeadID.text = ": ${lead.leadID.toString()}"
        binding.leadDetails.tvLoanType.text = ": ${lead.loanProductName}"
        binding.leadDetails.tvLeadnumber.text = ": ${lead.leadNumber}"
        binding.leadDetails.tvAmountValue.text = lead.amountRequest?.let { ": Rs. $it" } ?: kotlin.run { ": Rs. N/A" }
        binding.leadDetails.tvCreatedDate.text = ": ${lead.createdOn}"
        //binding.leadDetails.tvCreatedDate.text = ": ${ConvertDate().convertDate(lead.createdOn!!)}"
        binding.leadDetails.tvUpdatedDate.text = ConvertDate().convertDate(lead.lastModifiedOn!!)

        when (lead.status) {
            AppEnums.LEAD_TYPE.NEW.type -> binding.leadDetails.tvStatusLine.setBackgroundColor(resources.getColor(R.color.lead_status_new))
            AppEnums.LEAD_TYPE.SUBMITTED.type -> binding.leadDetails.tvStatusLine.setBackgroundColor(resources.getColor(R.color.lead_status_submitted))
            AppEnums.LEAD_TYPE.PENDING.type -> binding.leadDetails.tvStatusLine.setBackgroundColor(resources.getColor(R.color.lead_status_pending))
            AppEnums.LEAD_TYPE.REJECTED.type -> binding.leadDetails.tvStatusLine.setBackgroundColor(resources.getColor(R.color.lead_status_rejected))
        }
    }

    private fun fetchSpinnersDataFromDB() {
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(this@UpdateCallActivity, Observer { masterDrownDownValues ->
            masterDrownDownValues?.let {
                allMasterDropDown = it
                //Now assign the spinners....
                it.CustomerFollowUpStatus?.let { values -> setCallStatusSpinnerAdapter(values) }
                it.LeadType?.let { values -> setLeadTypeSpinnerAdapter(values) }
                it.LeadNotificationType?.let { values -> setLeadNotificationTypeSpinnerAdapter(values) }
                it.LeadRejectionReason?.let { values -> setLeadRejectionTypeSpinnerAdapter(values) }
            }
        })
    }

    private fun switchViewByCustomerStatus(position: Int) {
        when (position) {
            0 -> showFollowUpLayout()
            1 -> showFixedMeetingLayout()
            2 -> showNotInterestedLayout()
            else -> hideAllLayouts()
        }
    }

    private fun showFollowUpLayout() {
        selectedLayoutType = RequestLayout.FOLLOW_UP

        layoutFixedMeeting.visibility = View.GONE
        layoutNotInterested.visibility = View.GONE
        layoutFollowUp.visibility = View.VISIBLE
    }

    private fun showFixedMeetingLayout() {
        selectedLayoutType = RequestLayout.FIX_MEETING

        layoutFixedMeeting.visibility = View.VISIBLE
        layoutNotInterested.visibility = View.GONE
        layoutFollowUp.visibility = View.GONE
    }

    private fun showNotInterestedLayout() {
        selectedLayoutType = RequestLayout.NOT_INTERESTED

        layoutFixedMeeting.visibility = View.GONE
        layoutNotInterested.visibility = View.VISIBLE
        layoutFollowUp.visibility = View.GONE
    }

    private fun hideAllLayouts() {
        selectedLayoutType = RequestLayout.NOTHING

        layoutFixedMeeting.visibility = View.GONE
        layoutNotInterested.visibility = View.GONE
        layoutFollowUp.visibility = View.GONE
    }

    private fun getCallUpdateRequest(): Requests.RequestCallUpdate? {
        val leadId = leadDetails?.leadID
        val customerFollowUpStatusTypeDetailId = (binding.spinnerCallStatus.selectedItem as DropdownMaster).typeDetailID

        val leadTypeDetailId = if (selectedLayoutType == RequestLayout.FOLLOW_UP) (layoutFollowUp.spinnerLeadType.selectedItem as DropdownMaster).typeDetailID else null

        val leadRejectionReasonTypeDetailId = if (selectedLayoutType == RequestLayout.NOT_INTERESTED) (layoutNotInterested.spinnerLeadCloseReason.selectedItem as DropdownMaster).typeDetailID else null

        val meetingDate = when (selectedLayoutType) {
            RequestLayout.FOLLOW_UP -> DateUtil().getFormattedDate(DateUtil.dateFormattingType.TYPE_NORMAL_1, DateUtil.dateFormattingType.TYPE_API_REQUEST_2, layoutFollowUp.etFollowUpTiming.text.toString())
            RequestLayout.FIX_MEETING -> DateUtil().getFormattedDate(DateUtil.dateFormattingType.TYPE_NORMAL_1, DateUtil.dateFormattingType.TYPE_API_REQUEST_2, layoutFixedMeeting.etMeetingDate.text.toString())
            else -> ""
        }
        val notificationTypeDetailId = if (selectedLayoutType == RequestLayout.FIX_MEETING) (layoutFixedMeeting.spinnerNotificationType.selectedItem as DropdownMaster).typeDetailID else null
        val messageShared = when (selectedLayoutType) {
            RequestLayout.FOLLOW_UP -> layoutFollowUp.etMessageFollowUp.text.toString()
            RequestLayout.FIX_MEETING -> layoutFixedMeeting.etMessageFixMeeting.text.toString()
            else -> ""
        }

        return Requests.RequestCallUpdate(leadID = leadId!!,
                customerFollowUpStatusTypeDetailId = customerFollowUpStatusTypeDetailId
                , leadTypeDetailId = leadTypeDetailId
                , leadRejectionReasonTypeDetailId = leadRejectionReasonTypeDetailId
                , meetingDate = meetingDate
                , notificationTypeDetailId = notificationTypeDetailId
                , messageShared = messageShared
        )
    }

    private fun postFollowUpStatus() {
        presenter.callNetwork(ConstantsApi.CALL_UPDATE_CALL, dmiConnector = CallUpdateRequest())
    }

}
