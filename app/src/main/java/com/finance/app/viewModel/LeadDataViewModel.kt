/*
* Copyright (C) 2017 The Munish/BrighterBrain Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.finance.app.viewModel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.*
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.utility.LeadRequestResponseConversion
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import java.io.Serializable
import javax.inject.Inject

class LeadDataViewModel(private val activity: FragmentActivity) : BaseViewModel(activity) {

    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var api: ApiProject
    private val presenter = Presenter()

    val isAllApiCallCompleted = MutableLiveData<Boolean>()
    private val isLeadSyncLoanInfo = MutableLiveData<Boolean>()
    private val isLeadSyncPersonalInfo = MutableLiveData<Boolean>()
    private val isLeadSyncEmployment = MutableLiveData<Boolean>()
    private val isLeadSyncBankDetail = MutableLiveData<Boolean>()
    private val isLeadSyncLiabilityAndAssets = MutableLiveData<Boolean>()
    private val isLeadSyncProperty = MutableLiveData<Boolean>()
    private val isLeadSyncReference = MutableLiveData<Boolean>()
    private val isLeadSyncDocument = MutableLiveData<Boolean>()


    private var leadData: AllLeadMaster? = null

    private val listOfToSyncData = listOf(isLeadSyncLoanInfo,
            isLeadSyncPersonalInfo, isLeadSyncEmployment, isLeadSyncBankDetail,
            isLeadSyncLiabilityAndAssets, isLeadSyncReference,isLeadSyncProperty,isLeadSyncDocument)

    init {
        ArchitectureApp.instance.component.inject(this)
        checkIfAppConfiguredSuccessfully()
        setObservers()
    }

    private fun setObservers() {
        isAllApiCallCompleted.observeForever {
            when (it) {
                true -> {
                    leadData?.isDetailAlreadySync = true
                    saveLead(leadData)
                }
            }
        }

        listOfToSyncData.forEach {
            it.observeForever { checkIfAppConfiguredSuccessfully() }
        }
    }

    private fun checkIfAppConfiguredSuccessfully() {

        var isAllApiCallsCompletedValue = true

        permissionLoop@ for (observable in listOfToSyncData) {
            val isSyncCompleted = getBoolean(observable)

            if (!isSyncCompleted) {
                isAllApiCallsCompletedValue = false
                break@permissionLoop
            }
        }

        isAllApiCallCompleted.value = isAllApiCallsCompletedValue
    }

    private fun getBoolean(liveData: MutableLiveData<Boolean>) = liveData.value ?: false

    fun getLeadData(leadData: AllLeadMaster) {
        this.leadData = leadData
        val leadId = (leadData.leadID ?: "").toString()
        presenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP, CallGetLoan(leadData = leadData, leadId = leadId, form = AppEnums.FormType.LOANINFO))
        presenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP, CallGetLoan(leadData = leadData, leadId = leadId, form = AppEnums.FormType.PERSONALINFO))
        presenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP, CallGetLoan(leadData = leadData, leadId = leadId, form = AppEnums.FormType.EMPLOYMENT))
        presenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP, CallGetLoan(leadData = leadData, leadId = leadId, form = AppEnums.FormType.BANKDETAIL))
        presenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP, CallGetLoan(leadData = leadData, leadId = leadId, form = AppEnums.FormType.LIABILITYASSET))
        presenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP, CallGetLoan(leadData = leadData, leadId = leadId, form = AppEnums.FormType.PROPERTY))
        presenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP, CallGetLoan(leadData = leadData, leadId = leadId, form = AppEnums.FormType.REFERENCE))
        presenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP,CallGetLoan(leadData = leadData, leadId = leadId,form = AppEnums.FormType.DOCUMENT))

    }

    private fun saveLead(leadData: AllLeadMaster?) {
        leadData?.let {
            GlobalScope.launch {
                dataBase.provideDataBaseSource().allLeadsDao().insertLead(leadData)
            }
        }
    }

    inner class CallGetLoan(private val leadData: AllLeadMaster, private val leadId: String,
                            private val form: AppEnums.FormType) : ViewGeneric<ArrayList<String>?,
            Response.ResponseGetLoanApplication>(context = activity) {
        override val apiRequest: ArrayList<String>?
            get() = arrayListOf(leadId, form.type)

        override fun getApiSuccess(value: Response.ResponseGetLoanApplication) {
            if (value.responseCode == Constants.SUCCESS) {
                handleResponse(value.responseObj)
            }
        }



        private fun handleResponse(responseObj: Response.LoanApplicationGetObj?) {
            val apiResponseObject = LeadRequestResponseConversion().getResponseObject(form = form,
                    response = responseObj)
            when (form) {
                AppEnums.FormType.LOANINFO -> handleLoanInfoResponse(apiResponseObject)
                AppEnums.FormType.PERSONALINFO -> handlePersonalResponse(apiResponseObject)
                AppEnums.FormType.EMPLOYMENT -> handleEmploymentResponse(apiResponseObject)
                AppEnums.FormType.BANKDETAIL -> handleBankDetailResponse(apiResponseObject)
                AppEnums.FormType.LIABILITYASSET -> handleAssetsAndLiabilityResponse(apiResponseObject)
                AppEnums.FormType.PROPERTY -> handlePropertyResponse(apiResponseObject)
                AppEnums.FormType.REFERENCE -> handleReferenceResponse(apiResponseObject)
                AppEnums.FormType.DOCUMENT -> handleDocumentResponse(apiResponseObject)

            }
        }

        private fun handleLoanInfoResponse(apiResponseObject: Serializable?) {
            apiResponseObject?.let { leadData.loanData = apiResponseObject as LoanInfoModel }
            setObservableValueTrue(isLeadSyncLoanInfo)
        }

        private fun handlePersonalResponse(apiResponseObject: Serializable?) {
            apiResponseObject?.let { leadData.personalData = apiResponseObject as PersonalApplicantList }
            setObservableValueTrue(isLeadSyncPersonalInfo)
        }

        private fun handleEmploymentResponse(apiResponseObject: Serializable?) {
            apiResponseObject?.let { leadData.employmentData = apiResponseObject as EmploymentApplicantList }
            setObservableValueTrue(isLeadSyncEmployment)
        }

        private fun handleBankDetailResponse(apiResponseObject: Serializable?) {
            apiResponseObject?.let { leadData.bankData = apiResponseObject as BankDetailList }
            setObservableValueTrue(isLeadSyncBankDetail)
        }

        private fun handleAssetsAndLiabilityResponse(apiResponseObject: Serializable?) {
            apiResponseObject?.let { leadData.assetLiabilityData = apiResponseObject as AssetLiabilityList }
            setObservableValueTrue(isLeadSyncLiabilityAndAssets)
        }

        private fun handlePropertyResponse(apiResponseObject: Serializable?) {
            apiResponseObject?.let { leadData.propertyData = apiResponseObject as PropertyModel }
            setObservableValueTrue(isLeadSyncProperty)
        }

        private fun handleReferenceResponse(apiResponseObject: Serializable?) {
            apiResponseObject?.let { leadData.referenceData = apiResponseObject as ReferencesList }
            setObservableValueTrue(isLeadSyncReference)
        }
        private fun handleDocumentResponse(apiResponseObject: Serializable?){
            apiResponseObject?.let { leadData.documentData = apiResponseObject as DocumentDetailList }
            setObservableValueTrue(isLeadSyncDocument)
        }
        private fun handleDocumentResponseDummy(apiResponseObject: Serializable?){
            apiResponseObject?.let { leadData.documentData = apiResponseObject as DocumentDetailList }
            setObservableValueTrue(isLeadSyncDocument)
        }


        private fun setObservableValueTrue(observableSync: MutableLiveData<Boolean>) {
            observableSync.value = true
        }
    }
}