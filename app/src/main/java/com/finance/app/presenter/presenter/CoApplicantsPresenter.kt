package com.finance.app.presenter.presenter

import com.finance.app.presenter.connector.CoApplicantsConnector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

class CoApplicantsPresenter(private val coApplicant: CoApplicantsConnector.CoApplicants) : CoApplicantsConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        if (type == ConstantsApi.CALL_COAPPLICANTS_LIST) {
                callCoApplicantsList()
        }
    }

    private fun callCoApplicantsList() {
        val requestApi = apiProject.api.getCoApplicantsList(coApplicant.leadIdForApplicant)

        requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { coApplicant.showProgressDialog() }
                .doFinally { coApplicant.hideProgressDialog() }
                .subscribe({ response -> onCoApplicant(response) },
                        { e -> coApplicant.getCoApplicantsListFailure(e?.message ?: "") })
    }

    private fun onCoApplicant(response: Response.ResponseCoApplicants) {
        if (response.responseCode == "200") {
            coApplicant.getCoApplicantsListSuccess(response)
            System.out.println("Response>>>>>>"+response)
//            sharedPreferencesUtil.saveCoApplicantsList(response.responseObj)
        } else {
            coApplicant.getCoApplicantsListFailure(response.responseMsg)
        }
    }
}