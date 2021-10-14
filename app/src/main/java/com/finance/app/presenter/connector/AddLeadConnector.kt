package com.finance.app.presenter.connector

import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response

/**
 * Created by munishkumarthakur on 31/12/17.
 */
interface AddLeadConnector {

    interface AddLead : ReusableView {
        val addLeadRequest: Requests.RequestAddLead

        fun getAddLeadSuccess(value: Response.ResponseAddLead)
        fun getAddLeadFailure(msg: String)

    }
    interface PresenterOpt : ReusableNetworkConnector
}