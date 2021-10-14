package com.finance.app.presenter.presenter

import android.app.ProgressDialog
import android.content.Context
import com.fasterxml.jackson.core.JsonParser
import com.finance.app.R
import com.finance.app.persistence.model.KycDocumentModel
import com.finance.app.persistence.model.LoanApplicationRequest
import com.finance.app.presenter.connector.Connector
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.util.DialogFactory
import motobeans.architecture.util.exShowToast
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Created by munishkumarthakur on 21/12/19.
 */
@Suppress("UNCHECKED_CAST")
class Presenter {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }
    fun <RequestApi, ResponseApi> callNetwork(type: ConstantsApi, dmiConnector: Connector.ViewOpt<RequestApi, ResponseApi>) {
        val requestApi = when (type) {
            ConstantsApi.CALL_ADD_LEAD -> apiProject.api.addLead(dmiConnector.apiRequest as Requests.RequestAddLead)
            ConstantsApi.CALL_ALL_MASTER_VALUE -> apiProject.api.getAllMasterValue()
            ConstantsApi.CALL_LOAN_PRODUCT -> apiProject.api.getLoanProduct()
            ConstantsApi.CALL_GET_ALL_LEADS -> apiProject.api.getAllLeads()
            ConstantsApi.CALL_ALL_STATES -> apiProject.api.getStates()
            ConstantsApi.CALL_DOCUMENT_CHECKLIST ->apiProject.api.getDocumentList()
            ConstantsApi.CALL_LOGIN -> apiProject.api.loginUser(dmiConnector.apiRequest as Requests.RequestLogin)
            ConstantsApi.CALL_COAPPLICANTS_LIST -> apiProject.api.getCoApplicantsList(dmiConnector.apiRequest as String)
            ConstantsApi.CALL_SEND_OTP -> apiProject.api.sendOTP(dmiConnector.apiRequest as Requests.RequestSendOTP)
            ConstantsApi.CALL_VERIFY_OTP -> apiProject.api.verifyOTP(dmiConnector.apiRequest as Requests.RequestVerifyOTP)
            ConstantsApi.CALL_SOURCE_CHANNEL_PARTNER_NAME -> {
                val strings = dmiConnector.apiRequest as ArrayList<String>
                apiProject.api.sourceChannelPartnerName(strings[0], strings[1], strings[2])
            }
            ConstantsApi.CALL_GET_LOAN_APP -> {
                val strings = dmiConnector.apiRequest as ArrayList<String>
                apiProject.api.getLoanApp(strings[0], strings[1])
            }
            ConstantsApi.CALL_POST_LOAN_APP -> apiProject.api.postLoanApp(dmiConnector.apiRequest as LoanApplicationRequest)
            ConstantsApi.CALL_UPDATE_CALL -> apiProject.api.postCallUpdate((dmiConnector.apiRequest as Requests.RequestCallUpdate).leadID, dmiConnector.apiRequest as Requests.RequestCallUpdate)
            ConstantsApi.CALL_FINAL_SUBMIT -> apiProject.api.finalSubmit((dmiConnector.apiRequest as Requests.RequestFinalSubmit).leadID)
            ConstantsApi.CALL_FOLLOWUP -> apiProject.api.postCallFollowUp((dmiConnector.apiRequest as Requests.RequestFollowUp).leadID)
            ConstantsApi.CALL_KYC -> apiProject.api.postCallKYC(dmiConnector.apiRequest as Requests.RequestKYC)
            ConstantsApi.CALL_DOC_TYPE -> apiProject.api.getDocumentType((dmiConnector.apiRequest as Requests.RequestDocumentList).codeId)
            ConstantsApi.CALL_UPLOADED_DOC -> apiProject.api.getDocumentList((dmiConnector.apiRequest as Requests.RequestUploadedDocumentList).codeId, (dmiConnector.apiRequest as Requests.RequestUploadedDocumentList).leadId, (dmiConnector.apiRequest as Requests.RequestUploadedDocumentList).applicantNumber)
            ConstantsApi.CALL_DOWNLOAD_DOCUMENT -> apiProject.api.getDocumentDownloadableLink((dmiConnector.apiRequest as Requests.RequestDocumentDownloadableLink).DocumentId)
            ConstantsApi.Call_FINAL_RESPONSE ->apiProject.api.finalSubmittedResponse((dmiConnector.apiRequest as Requests.RequestSubmittedLead).leadID)
            ConstantsApi.CALL_EDIT_LEAD -> apiProject.api.editLead(dmiConnector.apiRequest as Requests.RequestEditLead)
            ConstantsApi.CALL_KYC_DETAIL -> apiProject.api.getKycDetail((dmiConnector.apiRequest as Requests.RequestKycDetail).leadID, (dmiConnector.apiRequest as Requests.RequestKycDetail).leadApplicantNumber)
            ConstantsApi.Call_RESET_PASSWORD -> apiProject.api.resetPassword(dmiConnector.apiRequest as Requests.RequestResetPassword)
            ConstantsApi.CALL_GET_OTP-> apiProject.api.getOTP((dmiConnector.apiRequest as Requests.RequestGetOTP))
            ConstantsApi.CALL_VERIFY_FORGOT_OTP-> apiProject.api.verifyOTP(dmiConnector.apiRequest as Requests.RequestVerifyOTPforForgetPassword)
            ConstantsApi.CALL_SUBMIT_PASSWORD -> apiProject.api.submitPassword(dmiConnector.apiRequest as Requests.RequestSubmitPassword)
            ConstantsApi.CALL_DASBOARD -> apiProject.api.getDasboardData()
            ConstantsApi.CALL_KYC_PREPARE -> apiProject.api.prepareCallKYC(dmiConnector.apiRequest as Requests.RequestKYCID)
            ConstantsApi.CALL_KYC_MOBILE_PREPARE -> apiProject.api.prepareCallKYCMobile(dmiConnector.apiRequest as Requests.RequestKYCOnMobileId)
            //
            ConstantsApi.CALL_KYC_APPLICANT_DETAILS -> apiProject.api.getApplicantKycList((dmiConnector.apiRequest as Requests.RequestKYCApplicantList).leadID)
            //ConstantsApi.CALL_FINAL_SUBMIT -> apiProject.api.finalSubmit((dmiConnector.apiRequest as Requests.RequestFinalSubmit).leadID)
            ConstantsApi.CALL_REPORT ->apiProject.api.getReport((dmiConnector.apiRequest as Requests.RequestReport).screenName,(dmiConnector.apiRequest as Requests.RequestReport).searchKey)
            ConstantsApi.CALL_KYC_ATTEMPT -> apiProject.api.getKycAttempt((dmiConnector.apiRequest as Requests.RequestKycAttempt).leadID, (dmiConnector.apiRequest as Requests.RequestKycAttempt).leadApplicantNumber)
            ConstantsApi.CALL_FAQ  ->apiProject.api.getFAQ()
            ConstantsApi.CALL_EMPLOYEE_BY_MANAGER ->apiProject.api.getEmployeeByManager()
            ConstantsApi.CALL_LOCATION_HISTORY->apiProject.api.getLocationHistory((dmiConnector.apiRequest as Requests.RequestLocationHistory).userID)


            else -> return
        }

        callApi(dmiConnector, requestApi = requestApi)
    }

    private fun <RequestApi, ResponseApi> callApi(viewOpt: Connector.ViewOpt<RequestApi, ResponseApi>, requestApi: Observable<out Any>) {
        val dispose = requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewOpt.showProgressDialog() }
                .doFinally { viewOpt.hideProgressDialog() }
                .subscribe({ response ->
                    response?.let { apiSuccess(viewOpt, response as ResponseApi) }
                }, { e -> if(e != null){
                    val errorMessage  = ApiError(e).message
                    apiFailure(viewOpt , errorMessage)

                }else {
                    apiFailure(viewOpt , e)
                }
                }
                )


    }

    private fun <RequestApi, ResponseApi> apiSuccess(viewOpt: Connector.ViewOpt<RequestApi, ResponseApi>, response: ResponseApi) {
        viewOpt.getApiSuccess(value = response)
    }

    private fun <RequestApi, ResponseApi> apiFailure(viewOpt: Connector.ViewOpt<RequestApi, ResponseApi>, e: String?) {
        viewOpt.getApiFailure(e.toString())
    }


}

abstract class ViewGeneric<RequestApi, ResponseApi>(val context: Context) : Connector.ViewOpt<RequestApi, ResponseApi> {

    internal var progressDialog: ProgressDialog? = null

    override fun showToast(msg: String) {
        msg.exShowToast(context)
    }

    override fun showProgressDialog() {
        when (BaseAppCompatActivity.progressDialog == null) {
            true -> BaseAppCompatActivity.progressDialog = DialogFactory.getInstance(context = context)
        }
        BaseAppCompatActivity.progressDialog?.show()
    }

    override fun hideProgressDialog() {
        BaseAppCompatActivity.progressDialog?.hide()
    }

    override fun getApiFailure(msg: String) {
       showToast(msg)
        //showToast(context.getString(R.string.error_api_failure))
    }
}
class ApiError constructor(error : Throwable){
    var message = "An error occured"
    init {
        if(error is HttpException){
            val errorJsonString = error.response().errorBody()?.string()
            System.out.println("errorJsonString"+errorJsonString)
            val responseMessage:JSONObject = JSONObject(errorJsonString)
            message = responseMessage.getString("responseMsg")
            System.out.println("Message" +message)
        }
        else{
            this.message = error.message ?: this.message
        }
    }
}

