package com.finance.app.presenter.presenter

import com.finance.app.presenter.connector.AllLeadsConnector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

class GetAllLeadsPresenter(private val allLeads: AllLeadsConnector.AllLeads) : AllLeadsConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        if (type == ConstantsApi.CALL_GET_ALL_LEADS) {
            callGetAllLeadsApi()
        }
    }

    private fun callGetAllLeadsApi() {
        val requestApi = apiProject.api.getAllLeads()
        requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { allLeads.showProgressDialog() }
                .doFinally { allLeads.hideProgressDialog() }
                .subscribe({ response -> onGetAllLead(response) },
                        { e -> allLeads.getAllLeadsFailure(e?.message ?: "") })
    }

    private fun onGetAllLead(response: Response.ResponseGetAllLeads) {
        if (response.responseCode == "200") {
            allLeads.getAllLeadsSuccess(response)
        } else {
            allLeads.getAllLeadsFailure(response.responseMsg)
        }
    }
}