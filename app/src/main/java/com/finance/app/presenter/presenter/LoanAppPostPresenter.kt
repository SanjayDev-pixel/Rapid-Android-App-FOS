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

class LoanAppPostPresenter(private val postLoanApp: LoanApplicationConnector.PostLoanApp) : LoanApplicationConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        if (type == ConstantsApi.CALL_POST_LOAN_APP) {
            callLoanAppPostApi()
        }
    }

    private fun callLoanAppPostApi() {
        val requestApi = apiProject.api.postLoanApp(postLoanApp.loanAppRequestPost)
        val request = requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { postLoanApp.showProgressDialog() }
                .doFinally { postLoanApp.hideProgressDialog() }
                .subscribe({ response -> onLoanAppPost(response) },
                        { e -> postLoanApp.getLoanAppPostFailure(e?.message ?: "") })
    }

    private fun onLoanAppPost(responsePost: Response.ResponseGetLoanApplication) {
        if (responsePost.responseCode == "200") {
            postLoanApp.getLoanAppPostSuccess(responsePost)
        } else {
            postLoanApp.getLoanAppPostFailure(responsePost.responseMsg)
        }
    }
}
