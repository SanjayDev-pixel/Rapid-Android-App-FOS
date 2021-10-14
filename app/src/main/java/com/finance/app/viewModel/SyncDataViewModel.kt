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
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

class SyncDataViewModel(private val activity: FragmentActivity) : BaseViewModel(activity) {

    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var api: ApiProject
    private val viewState: MutableLiveData<ProductViewState> = MutableLiveData()
    private val presenter = Presenter()

    init {
        ArchitectureApp.instance.component.inject(this)
        viewState.value = ProductViewState()
    }

    data class ProductViewState(var isNoDataFound: Boolean = false,
                                val isLoading: Boolean = false,
                                val isError: Boolean = false,
                                val isEmptyData: Boolean = false,
                                val errorMessage: String? = null)

    fun getUpdatedDataFromServer() {
        presenter.callNetwork(ConstantsApi.CALL_ALL_MASTER_VALUE, dmiConnector = AllMasterDropdownData())
        presenter.callNetwork(ConstantsApi.CALL_LOAN_PRODUCT, dmiConnector = LoanProductsDropdown())
        presenter.callNetwork(ConstantsApi.CALL_ALL_STATES, dmiConnector = AllStatesList())
        presenter.callNetwork(ConstantsApi.CALL_DOCUMENT_CHECKLIST,dmiConnector = AllDocumentCheckList())
        //Yahe pe fq dowload karwana hai
        presenter.callNetwork(ConstantsApi.CALL_FAQ, CallFAQ())
        getAllLeads()
    }

    internal fun getAllLeads() {
        presenter.callNetwork(ConstantsApi.CALL_GET_ALL_LEADS, dmiConnector = AllLeadsList())
    }
    inner class CallFAQ : ViewGeneric<String?, Response.ResponseFAQ>(context = activity) {
        override val apiRequest: String?
            get() = null

        override fun getApiSuccess(value: Response.ResponseFAQ) {
            if (value.responseCode == Constants.SUCCESS) {
                System.out.println("Response>>>>" + value.responseObj.size)
                saveFAQIntoDatabse(value)


            } else {
                showToast("Not called")
            }
        }

    }
    private fun saveFAQIntoDatabse(value: Response.ResponseFAQ) {
        GlobalScope.launch {
            dataBase.provideDataBaseSource().faqDao().deleteFAQ()
            dataBase.provideDataBaseSource().faqDao().insertFAQ(value.responseObj)
        }
    }

    inner class AllMasterDropdownData : ViewGeneric<String?, Response.ResponseAllMasterDropdown>(context = activity) {
        override val apiRequest: String?
            get() = null

        override fun getApiSuccess(value: Response.ResponseAllMasterDropdown) {
            if (value.responseCode == Constants.SUCCESS) {

                GlobalScope.launch {
                    dataBase.provideDataBaseSource().allMasterDropDownDao().insertAllMasterDropDownValue(value.responseObj)
                }
            } else {
                showToast(value.responseMsg)
            }
        }

    }

    inner class LoanProductsDropdown : ViewGeneric<String?, Response.ResponseLoanProduct>(context = activity) {
        override val apiRequest: String?
            get() = null

        override fun getApiSuccess(value: Response.ResponseLoanProduct) {
            if (value.responseCode == Constants.SUCCESS) {
                GlobalScope.launch {
                    dataBase.provideDataBaseSource().loanProductDao().insertLoanProductList(value.responseObj)
                }
            } else {
                showToast(value.responseMsg)
            }
        }
    }

    inner class AllStatesList : ViewGeneric<String?, Response.ResponseStatesDropdown>(context = activity) {
        override val apiRequest: String?
            get() = null

        override fun getApiSuccess(value: Response.ResponseStatesDropdown) {
            if (value.responseCode == Constants.SUCCESS) {
                GlobalScope.launch {
                    dataBase.provideDataBaseSource().statesDao().insertStates(value.responseObj)
                }
            } else {
                showToast(value.responseMsg)
            }
        }
    }
    //Inner class for Download DocumentCheckList
    inner class AllDocumentCheckList : ViewGeneric<String?,Response.ResponseDocumentCheckLists>(context = activity)
    {
        override val apiRequest: String?
            get() = null

        override fun getApiSuccess(value: Response.ResponseDocumentCheckLists) {
            System.out.println("Response Document>>>>"+value.responseCode+"other>>> "+value)
             if(value.responseCode == Constants.SUCCESS)
             {
                 System.out.println("Response Document>>>>"+value.responseObj)
                 GlobalScope.launch {
                     dataBase.provideDataBaseSource().allDocumentDao().insertDocumentList(value.responseObj)
                 }
             }
            else
             {
                 System.out.println("responseMsg>>>>"+value.responseMsg)
                 showToast(value.responseMsg)
             }
        }
    }

    inner class AllLeadsList : ViewGeneric<String?, Response.ResponseGetAllLeads>(context = activity) {
        override val apiRequest: String?
            get() = null

        override fun getApiSuccess(value: Response.ResponseGetAllLeads) {
            if (value.responseCode == Constants.SUCCESS) {

                saveDataToDB(value.responseObj)
                /*
                GlobalScope.launch {
                    dataBase.provideDataBaseSource().allLeadsDao().deleteAllLeadMaster()
                    dataBase.provideDataBaseSource().allLeadsDao().insertLeadsList(value.responseObj)
                }
                */
                /*
                Handler().postDelayed({
                    saveDataToDB(value.responseObj)
                }, 2000)
                */
            }
        }

        private fun saveDataToDB(leadsFromServer: ArrayList<AllLeadMaster>) {

            val allFilteredList = ArrayList<AllLeadMaster>()

            GlobalScope.launch {
                val leadsDao = dataBase.provideDataBaseSource().allLeadsDao()

                val listOfAllLeads = GlobalScope.async { leadsDao.getAllLeadsSynchron() }.await()

                val mapOfAllLeadsSavedInDB = listOfAllLeads?.map { it.leadID to it }?.toMap()
                leadsFromServer.forEach { serverLead ->
                    when (mapOfAllLeadsSavedInDB?.contains(serverLead.leadID)) {
                        true -> {
                            val leadData = mapOfAllLeadsSavedInDB[serverLead.leadID]

                            leadData?.let {
                                when(leadData.isSyncWithServer) {
                                    true -> allFilteredList.add(serverLead)
                                    else -> allFilteredList.add(leadData)
                                }

                            }
                        }
                        else -> allFilteredList.add(serverLead)
                    }
                }

                leadsDao.deleteAllLeadMaster()
                leadsDao.insertLeadsList(allFilteredList)
            }
        }
    }
}