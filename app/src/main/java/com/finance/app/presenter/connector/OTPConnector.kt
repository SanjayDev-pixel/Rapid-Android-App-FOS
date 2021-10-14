package com.finance.app.presenter.connector

import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response

interface OTPConnector {

    interface SendOTP : ReusableView {
        val sendOTPRequest: Requests.RequestSendOTP
        fun getSendOTPSuccess(value: Response.ResponseOTP)
        fun getSendOTPFailure(msg: String?)
    }

    interface VerifyOTP : ReusableView {
        val verifyOTPRequest: Requests.RequestVerifyOTP
        fun getVerifyOTPSuccess(value: Response.ResponseVerifyOTP)
        fun getVerifyOTPFailure(msg: String?)
    }

    interface PresenterOpt : ReusableNetworkConnector
}