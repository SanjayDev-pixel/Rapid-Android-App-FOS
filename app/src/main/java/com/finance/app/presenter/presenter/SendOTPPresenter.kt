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

class SendOTPPresenter(private val sendOTP: OTPConnector.SendOTP) : OTPConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        if (type == ConstantsApi.CALL_SEND_OTP) {
                callSendOTPApi()
        }
    }

    private fun callSendOTPApi() {
        val requestApi = apiProject.api.sendOTP(sendOTP.sendOTPRequest)

        requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { sendOTP.showProgressDialog() }
                .doFinally { sendOTP.hideProgressDialog() }
                .subscribe({ response -> onSendOTP(response) },
                        { e -> sendOTP.getSendOTPFailure(e?.message ?: "") })
    }

    private fun onSendOTP(response: Response.ResponseOTP) {
        if (response.responseCode == "200") {
            sendOTP.getSendOTPSuccess(response)
        } else {
            sendOTP.getSendOTPFailure(response.responseMsg)
        }
    }
}