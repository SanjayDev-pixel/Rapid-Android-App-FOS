package com.finance.app.presenter.presenter

import com.finance.app.presenter.connector.AddLeadConnector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

/**
 * Created by munishkumarthakur on 31/12/17.
 */
class AddLeadPresenter(private val viewOpt: AddLeadConnector.AddLead) : AddLeadConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        if (type == ConstantsApi.CALL_ADD_LEAD) {
                callAddLeadApi()
        }
    }

    private fun callAddLeadApi() {
        val requestApi = apiProject.api.addLead(viewOpt.addLeadRequest)

        requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewOpt.showProgressDialog() }
                .doFinally { viewOpt.hideProgressDialog() }
                .subscribe({ response -> onLeadAdd(response) },
                        { e -> viewOpt.getAddLeadFailure(e?.message ?: "") })
    }

    private fun onLeadAdd(response: Response.ResponseAddLead) {
        if (response.responseCode == "200") {
            viewOpt.getAddLeadSuccess(response)
        } else {
            viewOpt.getAddLeadFailure(response.responseMsg)
        }
    }
}