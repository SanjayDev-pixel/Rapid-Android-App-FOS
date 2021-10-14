package com.finance.app.presenter.connector

import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response

/**
 * Created by munishkumarthakur on 31/12/17.
 */
interface LoginConnector {

    interface ViewOpt : ReusableView {
        val loginRequest: Requests.RequestLogin

        fun getLoginSuccess(value: Response.ResponseLogin)
        fun getLoginFailure(msg: String)
    }
    interface PresenterOpt : ReusableNetworkConnector
}