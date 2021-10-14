package com.finance.app.presenter.connector
import motobeans.architecture.retrofit.response.Response

interface LoanProductConnector {
    interface ViewOpt : ReusableView {
        fun getLoanProductSuccess(value: Response.ResponseLoanProduct)
        fun getLoanProductFailure(msg: String)
    }
    interface PresenterOpt : ReusableNetworkConnector
}