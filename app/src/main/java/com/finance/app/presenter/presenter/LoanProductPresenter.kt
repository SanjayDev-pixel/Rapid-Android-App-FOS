package com.finance.app.presenter.presenter
import com.finance.app.presenter.connector.LoanProductConnector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

class LoanProductPresenter(private val loanInfo: LoanProductConnector.ViewOpt) :
        LoanProductConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        if (type == ConstantsApi.CALL_LOAN_PRODUCT) {
            callUpdateReferenceDetailApi()
        }
    }

    private fun callUpdateReferenceDetailApi() {
        val requestApi = apiProject.api.getLoanProduct()

        requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { loanInfo.showProgressDialog() }
                .doFinally { loanInfo.hideProgressDialog() }
                .subscribe({ response -> onLoanProductPurpose(response) },
                        { e -> loanInfo.getLoanProductFailure(e?.message ?: "") })
    }

    private fun onLoanProductPurpose(response: Response.ResponseLoanProduct) {
        if (response.responseCode == "200") {
            loanInfo.getLoanProductSuccess(response)
        } else {
            loanInfo.getLoanProductFailure(response.responseMsg)
        }
    }
}
