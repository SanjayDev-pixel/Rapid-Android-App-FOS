package com.finance.app.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.finance.app.R
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.persistence.model.ApplicantionSubmitModel
import com.finance.app.persistence.model.FinalSubmitLoanResponseNew
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.utility.LeadMetaData
import com.finance.app.viewModel.LeadDataViewModel

import kotlinx.android.synthetic.main.activity_final_submit.*
import kotlinx.android.synthetic.main.content_final_submit.*
import motobeans.architecture.appDelegates.ViewModelType
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.AppUtils.showToast
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import java.io.Serializable

/* temporary activity*/

class FinalSubmitActivity : AppCompatActivity() {
    private val presenter = Presenter()
    private var progressBar: ProgressBar? = null// temp
    private val leadDataViewModel: LeadDataViewModel by motobeans.architecture.appDelegates.viewModelProvider(this, ViewModelType.WITH_DAO)
 private var leadData :AllLeadMaster?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_final_submit)

        setSupportActionBar(toolbar)
        progressBar = findViewById<ProgressBar>(R.id.progress_Bar) as ProgressBar


        initView()
        setOnclickListener()


    }


    private fun initView() {

         leadData = LeadMetaData.getLeadData()
        leadData?.let {

        }

    }
    private fun setOnclickListener() {
        button_submitcall.setOnClickListener { view ->
              progressBar!!.visibility = View.VISIBLE
            // presenter.callNetwork(ConstantsApi.CALL_FINAL_SUBMIT, CallFinalSubmit())
            val lead: AllLeadMaster? = LeadMetaData.getLeadData()
                checkAndStartLoanApplicationActivity(lead)


        }
    }




    private fun checkAndStartLoanApplicationActivity(lead: AllLeadMaster?) {
        val isLeadInfoAlreadySync = leadDataViewModel.isAllApiCallCompleted.value ?: false
        val isLeadOfflineDataSync = lead!!.isDetailAlreadySync

        when ( isLeadOfflineDataSync ) {
            true ->  presenter.callNetwork(ConstantsApi.CALL_FINAL_SUBMIT, CallFinalSubmit())
            false -> Toast.makeText(this,"Please wait lead is syncronise with server",Toast.LENGTH_SHORT).show()
        }
    }


    inner class CallFinalSubmit : ViewGeneric<Requests.RequestFinalSubmit, Response.ResponseFinalSubmit>(context = this) {
        override val apiRequest: Requests.RequestFinalSubmit?
            get() = getCallUpdateRequest()


        override fun getApiSuccess(value: Response.ResponseFinalSubmit) {
            if (value.responseCode == Constants.SUCCESS) {
                val submitLoanResponse: ApplicantionSubmitModel?=value.responseObj
                if(value.responseObj !=null) {
                    Toast.makeText(context,"Submitted Successfully.",Toast.LENGTH_SHORT).show()
                    progressBar!!.visibility = View.GONE
                    val intent = Intent(this@FinalSubmitActivity, LoanSubmitStatusActivity::class.java)
                    intent.putExtra("SubmitResponse", submitLoanResponse)
                    startActivity(intent)
                }else{
                    showToast(value.responseMsg)
                }
                finish()


            } else {
                showToast(value.responseMsg)
                progressBar!!.visibility = View.GONE
            }

        }

        override fun getApiFailure(msg:String) {

            if(msg.exIsNotEmptyOrNullOrBlank()){
            super.getApiFailure(msg)
                progressBar!!.visibility = View.GONE
            }else{ super.getApiFailure("Time out Error")
                progressBar!!.visibility = View.GONE}

        }
    }

    private fun getCallUpdateRequest(): Requests.RequestFinalSubmit? {
        val leadId = LeadMetaData.getLeadId()
        return Requests.RequestFinalSubmit(leadID = leadId!!)

    }
}

