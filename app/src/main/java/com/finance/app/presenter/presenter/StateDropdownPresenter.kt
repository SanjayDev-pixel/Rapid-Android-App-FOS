package com.finance.app.presenter.presenter

import com.finance.app.presenter.connector.AllMasterValueConnector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

class StateDropdownPresenter(private val masterDropdown: AllMasterValueConnector.StateDropdown) : AllMasterValueConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        if (type == ConstantsApi.CALL_ALL_STATES) {
                callAllStateApi()
        }
    }

    private fun callAllStateApi() {
        val requestApi = apiProject.api.getStates()

        requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _ -> masterDropdown.showProgressDialog() }
                .doFinally { masterDropdown.hideProgressDialog() }
                .subscribe({ response -> onAllStatesValue(response) },
                        { e -> masterDropdown.getStatesDropdownFailure(e?.message ?: "") })
    }

    private fun onAllStatesValue(response: Response.ResponseStatesDropdown) {
        if (response.responseCode == "200") {
            masterDropdown.getStatesDropdownSuccess(response)
        } else {
            masterDropdown.getStatesDropdownFailure(response.responseMsg)
        }
    }
}