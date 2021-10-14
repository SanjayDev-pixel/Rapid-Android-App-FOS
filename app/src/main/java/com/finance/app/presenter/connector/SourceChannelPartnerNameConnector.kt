package com.finance.app.presenter.connector

import motobeans.architecture.retrofit.response.Response

interface SourceChannelPartnerNameConnector {

    interface SourceChannelPartnerName : ReusableView {
        val branchId: String
        val employeeId: String
        val channelTypeId: String
        fun getSourceChannelPartnerNameSuccess(value: Response.ResponseSourceChannelPartnerName)
        fun getSourceChannelPartnerNameFailure(msg: String)
    }
    interface PresenterOpt : ReusableNetworkConnector
}