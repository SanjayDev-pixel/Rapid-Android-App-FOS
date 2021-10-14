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

/**
 * Created by munishkumarthakur on 31/12/17.
 */
class AllMasterDropdownPresenter(private val masterDropdown: AllMasterValueConnector.MasterDropdown) : AllMasterValueConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        if (type == ConstantsApi.CALL_ALL_MASTER_VALUE) {
                callAllSpinnerApi()
        }
    }

    private fun callAllSpinnerApi() {
        val requestApi = apiProject.api.getAllMasterValue()

        requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _ -> masterDropdown.showProgressDialog() }
                .doFinally { masterDropdown.hideProgressDialog() }
                .subscribe({ response -> onAllSpinnerValue(response) },
                        { e -> masterDropdown.getAllMasterDropdownFailure(e?.message ?: "") })
    }

    private fun onAllSpinnerValue(response: Response.ResponseAllMasterDropdown) {
        if (response.responseCode == "200") {
            masterDropdown.getAllMasterDropdownSuccess(response)
        } else {
            masterDropdown.getAllMasterDropdownFailure(response.responseMsg)
        }
    }
}