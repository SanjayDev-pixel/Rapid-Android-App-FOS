package com.finance.app.presenter.presenter

import com.finance.app.presenter.connector.OTPConnector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

class VerifyOTPPresenter(private val verifyOTP: OTPConnector.VerifyOTP) : OTPConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        if (type == ConstantsApi.CALL_VERIFY_OTP) {
            callApi()
        }
    }

    private fun callApi() {
        val requestApi = apiProject.api.verifyOTP(verifyOTP.verifyOTPRequest)

        requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { verifyOTP.showProgressDialog() }
                .doFinally { verifyOTP.hideProgressDialog() }
                .subscribe({ response -> onVerifyOTP(response) },
                        { e -> verifyOTP.getVerifyOTPFailure(e?.message ?: "") })
    }

    private fun onVerifyOTP(response: Response.ResponseVerifyOTP) {
        if (response.responseCode == "200") {
            verifyOTP.getVerifyOTPSuccess(response)
        } else {
            verifyOTP.getVerifyOTPFailure(response.responseMsg)
        }
    }
}