package com.finance.app.presenter.connector

import motobeans.architecture.retrofit.request.Requests.RequestSample
import motobeans.architecture.retrofit.response.Response.ResponseSample

/**
 * Created by munishkumarthakur on 31/12/17.
 */
interface TestConnector {

    interface ViewOpt: ReusableView {
        val sampleRequest: RequestSample
        fun getObjectSuccess(value: ResponseSample)
        fun getObjectFailure(msg: String)

    }

    interface PresenterOpt: ReusableNetworkConnector

}