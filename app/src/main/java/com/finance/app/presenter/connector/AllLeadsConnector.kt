package com.finance.app.presenter.connector

import motobeans.architecture.retrofit.response.Response

interface AllLeadsConnector {

    interface AllLeads : ReusableView {
        fun getAllLeadsSuccess(value: Response.ResponseGetAllLeads)
        fun getAllLeadsFailure(msg: String)
    }
    interface PresenterOpt : ReusableNetworkConnector
}