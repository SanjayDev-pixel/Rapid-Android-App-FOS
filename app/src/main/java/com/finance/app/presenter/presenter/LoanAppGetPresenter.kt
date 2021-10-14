package com.finance.app.presenter.presenter

import com.finance.app.presenter.connector.LoanApplicationConnector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

class LoanAppGetPresenter(private val GetLoanApp: LoanApplicationConnector.GetLoanApp) : LoanApplicationConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        if (type == ConstantsApi.CALL_GET_LOAN_APP) {
            callLoanAppGetApi()
        }
    }

    private fun callLoanAppGetApi() {
        val requestApi = apiProject.api.getLoanApp(GetLoanApp.leadId, GetLoanApp.storageType)
        requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { GetLoanApp.showProgressDialog() }
                .doFinally { GetLoanApp.hideProgressDialog() }
                .subscribe({ response -> onLoanAppGet(response) },
                        { e -> GetLoanApp.getLoanAppGetFailure(e?.message ?: "") })
    }

    private fun onLoanAppGet(responsePost: Response.ResponseGetLoanApplication) {
        if (responsePost.responseCode == "200") {
            GetLoanApp.getLoanAppGetSuccess(responsePost)
        } else {
            GetLoanApp.getLoanAppGetFailure(responsePost.responseMsg)
        }
    }
}
