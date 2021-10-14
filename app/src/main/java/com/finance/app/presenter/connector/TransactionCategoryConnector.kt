package com.finance.app.presenter.connector

import motobeans.architecture.retrofit.response.Response

interface TransactionCategoryConnector {

    interface TransactionCategory : ReusableView {
        val ownershipId: String
        val transactionId: String

        fun getTransactionCategorySuccess(value: Response.ResponsePropertyNature)
        fun getTransactionCategoryFailure(msg: String)

    }

    interface PresenterOpt : ReusableNetworkConnector
}