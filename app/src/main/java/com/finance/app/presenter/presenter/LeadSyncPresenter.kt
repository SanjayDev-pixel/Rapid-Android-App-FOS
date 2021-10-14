package com.finance.app.presenter.presenter

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.persistence.model.LoanApplicationRequest
import com.finance.app.presenter.connector.LeadSyncConnector
import com.finance.app.utility.LeadRequestResponseConversion
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function8
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response.ResponseGetLoanApplication
import javax.inject.Inject

class LeadSyncPresenter(
        private val viewOptLocalToServer: LeadSyncConnector.ViewOptLocalToServer? = null ,
        private val viewOptServerToLocal: LeadSyncConnector.ViewOptServerToLocal? = null
) : LeadSyncConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var database: DataBaseUtil
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        Handler(Looper.getMainLooper()).post {
            when (type) {
                ConstantsApi.CALL_SYNC_LEAD_LOCAL_TO_SERVER -> callSyncAllNotSyncLeadsToServer()
                else -> {
                }
            }
        }
    }

    private lateinit var observableLocalToServerCheckOutOnly: Observer<List<AllLeadMaster>?>
    private lateinit var observerLocalToServerCheckOutOnly: LiveData<List<AllLeadMaster>?>

    private fun callSyncAllNotSyncLeadsToServer() {
        observableLocalToServerCheckOutOnly = Observer { items ->
            if (items?.size ?: 0 > 0) {
                sendRecordsOneByOneToServer(items)
            }

            observerLocalToServerCheckOutOnly.removeObserver(observableLocalToServerCheckOutOnly)
        }

        observerLocalToServerCheckOutOnly = database.provideDataBaseSource().allLeadsDao().getAllLeadsNotSyncWithServer()
        observerLocalToServerCheckOutOnly.observeForever(observableLocalToServerCheckOutOnly)

    }

    private fun sendRecordsOneByOneToServer(items: List<AllLeadMaster>?) {
        items?.forEach {
            hitApiLeadSyncLocalToServerSeparateApiHits(itemToSync = it)
        }
    }

    private fun hitApiLeadSyncLocalToServerSeparateApiHits(itemToSync: AllLeadMaster?) {
        itemToSync?.let {
            val leadRequest = LeadRequestResponseConversion()
            val requestLoanInfo = leadRequest.getRequest(AppEnums.FormType.LOANINFO , itemToSync)
            val requestPersonal = leadRequest.getRequest(AppEnums.FormType.PERSONALINFO , itemToSync)
            val requestEmployment = leadRequest.getRequest(AppEnums.FormType.EMPLOYMENT , itemToSync)
            val requestBank = leadRequest.getRequest(AppEnums.FormType.BANKDETAIL , itemToSync)
            val requestLiabilityAndAssets = leadRequest.getRequest(AppEnums.FormType.LIABILITYASSET , itemToSync)
            val requestProperty = leadRequest.getRequest(AppEnums.FormType.PROPERTY , itemToSync)
            val requestReference = leadRequest.getRequest(AppEnums.FormType.REFERENCE , itemToSync)
            val requestDocument = leadRequest.getRequest(AppEnums.FormType.DOCUMENT , itemToSync)

            val observableLoanInfo = getObserverCommon(requestLoanInfo)
            val observablePersonal = getObserverCommon(requestPersonal)
            val observableEmployment = getObserverCommon(requestEmployment)
            val observableBank = getObserverCommon(requestBank)
            val observableLiabilityAndAssets = getObserverCommon(requestLiabilityAndAssets)


            val observableProperty = getObserverCommon(requestProperty)
            val observableReference = getObserverCommon(requestReference)
            val observableDocument = getObserverCommon(requestDocument)

            Observable.zip(observableLoanInfo , observablePersonal , observableEmployment ,
                    observableBank , observableLiabilityAndAssets , observableProperty , observableReference ,
                    observableDocument ,
                    Function8 { responseLoanInfo: ResponseGetLoanApplication? , responsePersonal: ResponseGetLoanApplication? ,
                                responseEmployment: ResponseGetLoanApplication? , responseBank: ResponseGetLoanApplication? ,
                                responseLiabilityAndAssets: ResponseGetLoanApplication? , responseProperty: ResponseGetLoanApplication? ,
                                responseReference: ResponseGetLoanApplication? , responseDocument: ResponseGetLoanApplication? ->

                        val alResponses = listOf(
                                responseLoanInfo , responsePersonal , responseEmployment ,
                                responseBank , responseLiabilityAndAssets , responseProperty , responseReference , responseDocument
                        )
                        val isValid = isAllResponsesValid(alResponses)

                        var objectToReturn = AllLeadMaster()

                        when (isValid) {
                            true -> {
                                itemToSync.isSyncWithServer = true
                                objectToReturn = itemToSync
                            }
                        }
                        objectToReturn
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        onCreateOrderSyncSuccessfully(result)
                    } ,
                            { e -> apiFailure(e) })
        }
    }
    private fun isAllResponsesValid(alResponses: List<ResponseGetLoanApplication?>): Boolean {
        var isAllResponseValid = true
        alResponses.forEach {
            val isValid = checkAndValidateDataSyncResponseIsValid(it)
            if (!isValid) {
                isAllResponseValid = false
            }
        }

        return isAllResponseValid
    }

    private fun checkAndValidateDataSyncResponseIsValid(response: ResponseGetLoanApplication?) = response?.responseCode == Constants.SUCCESS

    private fun getObserverCommon(observerLoanInfo: LoanApplicationRequest?): Observable<ResponseGetLoanApplication>? {
        observerLoanInfo?.let {
            return apiProject.api.postLoanApp(observerLoanInfo)
        }

        return null
    }

    private fun hitApiLeadSyncLocalToServer(itemToSync: AllLeadMaster?) {
        itemToSync?.let {

            val requestApi = apiProject.api.postLoanAllLeadData(itemToSync)

            requestApi
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response -> onCreateOrderSyncSuccessfully(response.responseObj) } ,
                            { e ->
                                viewOptLocalToServer?.getLocalLeadSyncLocalToServerFailure(e?.message ?: "")
                            })
        }
    }

    private fun onCreateOrderSyncSuccessfully(response: AllLeadMaster?) {
        if (response?.leadID == null) {
            return
        } else {
            response.let {

                viewOptLocalToServer?.let {
                    viewOptLocalToServer.getLocalLeadSyncLocalToServerSuccess(response)
                }

//      GlobalScope.launch {
//        database.provideDataBaseSource().allLeadsDao().insertLead(resposne)
//      }

//      viewOptLocalToServer?.getLocalLeadSyncLocalToServerSuccess(resposne)
            }
        }
    }

    private fun apiFailure(e: Throwable?) {
        println(e)
    }

}