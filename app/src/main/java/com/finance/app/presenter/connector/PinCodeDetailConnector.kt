package com.finance.app.presenter.connector

import com.finance.app.others.AppEnums
import motobeans.architecture.retrofit.response.Response

interface PinCodeDetailConnector {

    interface PinCode : ReusableView {
        val pinCode: String

        fun getPinCodeSuccess(value: Response.ResponsePinCodeDetail, addressType: AppEnums.ADDRESS_TYPE? = null)
        fun getPinCodeFailure(msg: String)

    }
    interface PresenterOpt : ReusableNetworkConnector
}