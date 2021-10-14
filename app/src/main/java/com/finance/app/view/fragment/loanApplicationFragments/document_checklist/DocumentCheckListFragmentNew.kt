package com.finance.app.view.fragment.loanApplicationFragments.document_checklist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.finance.app.R
import com.finance.app.databinding.FragmentAssetliabilityNewBinding
import com.finance.app.databinding.FragmentDocumentChecklistBinding
import com.finance.app.eventBusModel.AppEvents
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.persistence.model.ApplicantionSubmitModel
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.activity.*
import com.finance.app.view.adapters.recycler.adapter.DocumentCheckLIstPagerAdapter
import com.finance.app.view.adapters.recycler.adapter.NavMenuAdapter
import com.finance.app.view.fragment.loanApplicationFragments.document_upload_kyc.DocumentUploadFragmentNew
import kotlinx.android.synthetic.main.activity_update_call.*
import kotlinx.android.synthetic.main.layout_fixed_meeting.view.*
import kotlinx.android.synthetic.main.layout_follow_up.view.*
import kotlinx.android.synthetic.main.layout_not_interested.view.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseFragment
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.DateUtil
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import java.util.ArrayList
import javax.inject.Inject


/**
 * Created by motobeans on 2/16/2018.
 */
class DocumentCheckListFragmentNew : BaseFragment(){
    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var formValidation: FormValidation
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private lateinit var binding: FragmentDocumentChecklistBinding
    private var pagerAdapterDocumentCheckList: DocumentCheckLIstPagerAdapter? = null
    private var applicantList: ArrayList<PersonalApplicantsModel>? = null
    private lateinit var mContext: Context
    private val presenter = Presenter()
    companion object {
        fun newInstance(): DocumentCheckListFragmentNew {
            return DocumentCheckListFragmentNew()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ArchitectureApp.instance.component.inject(this)
        mContext=context!!
    }
    override fun init() {}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = initBinding(inflater, container, R.layout.fragment_document_checklist)
        binding.lifecycleOwner = this

        initViews()
        setOnClickListener()
        return view
    }

    private fun initViews() {
    }

    private fun setOnClickListener() {
        binding.btnPrevious.setOnClickListener { AppEvents.fireEventLoanAppChangeNavFragmentPrevious() }
        binding.btnNext.setOnClickListener {
            if (LeadMetaData.getLeadData()?.status == "Submitted") {
               getSubmittedStateResponse()
            } else {
                pagerAdapterDocumentCheckList?.let { adapter->
                    if(adapter.isDocumentDetailsValid()){
                        LeadMetaData().saveDocumentData(adapter.getAllChecklistDetail())
                        PreviewActivity.start(mContext)
                       /* var fragment: Fragment? = DocumentUploadFragmentNew.newInstance()
                        val ft = (mContext as AppCompatActivity).supportFragmentManager.beginTransaction().apply {
                            replace(R.id.secondaryFragmentContainer, fragment!!)
                            addToBackStack(null)
                        }
                        ft.commit()*/
                    }
                    else
                    {
                        Toast.makeText(mContext,"Kindly fill all details", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }

    private fun getSubmittedStateResponse() {
        //Call Network
        presenter.callNetwork(ConstantsApi.Call_FINAL_RESPONSE, CallFinalSubmitResponse())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchLeadDetails()
    }
    private fun fetchLeadDetails() {
        LeadMetaData.getLeadObservable().observe(this, androidx.lifecycle.Observer { leadDetail ->
            leadDetail?.let {
                applicantList = it.personalData?.applicantDetails
                applicantList?.let {
                    //Set Tab Adapter...
                    setApplicantTabAdapter(it)
                }
            }
        })
    }

    private fun setApplicantTabAdapter(applicantList: ArrayList<PersonalApplicantsModel>) {
        pagerAdapterDocumentCheckList = DocumentCheckLIstPagerAdapter(childFragmentManager!!, applicantList)
        binding.vpDocumentDetailForm.adapter = pagerAdapterDocumentCheckList
        binding.tabLead.setupWithViewPager(binding.vpDocumentDetailForm)
    }
    inner class CallFinalSubmitResponse:ViewGeneric<Requests.RequestSubmittedLead, Response.ResponseFinalSubmitted>(context = mContext) {
        override val apiRequest: Requests.RequestSubmittedLead?
            get() = getRequestSubmittedLead()
        override fun getApiSuccess(value: Response.ResponseFinalSubmitted) {
            if (value.responseCode == Constants.SUCCESS) {
                val submitLoanResponse: ApplicantionSubmitModel? = value.responseObj
                if (value.responseObj != null) {
                    val intent = Intent(mContext, LoanSubmitStatusActivity::class.java)
                    intent.putExtra("SubmitResponse", submitLoanResponse)
                    mContext.startActivity(intent)
                } else {
                    showToast(value.responseMsg)
                }
            } else {
                showToast(value.responseMsg)
                //binding.progressBar!!.visibility = View.GONE
            }
        }
        override fun getApiFailure(msg: String) {

            if (msg.exIsNotEmptyOrNullOrBlank()) {
                    super.getApiFailure(msg)
                //   binding.progressBar!!.visibility = View.GONE
            } else {
                super.getApiFailure("Time out Error")
                // binding.progressBar!!.visibility = View.GONE
            }

        }
        private fun getRequestSubmittedLead():Requests.RequestSubmittedLead?{
            val leadId:Int?=LeadMetaData.getLeadId()
            return Requests.RequestSubmittedLead(leadID=leadId!!)
        }
    }
}

