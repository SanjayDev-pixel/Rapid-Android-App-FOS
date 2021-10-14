package com.finance.app.presenter.connector

import com.finance.app.persistence.model.*
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response

interface LoanApplicationConnector {

    interface PostLoanApp : ReusableView {
        val loanAppRequestPost: LoanApplicationRequest
        fun getLoanAppPostSuccess(value: Response.ResponseGetLoanApplication)
        fun getLoanAppPostFailure(msg: String)
    }

    interface GetLoanApp : ReusableView {
        val leadId: String
        val storageType: String
        fun getLoanAppGetSuccess(value: Response.ResponseGetLoanApplication)
        fun getLoanAppGetFailure(msg: String)
    }

    interface PresenterOpt : ReusableNetworkConnector
}