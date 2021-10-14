package com.finance.app.presenter.connector

import motobeans.architecture.retrofit.response.Response.ResponseSample

/**
 * Created by motobeans on 1/2/2018.
 */
interface TempSyncConnector {
    interface ViewOptLocalToServer {
        fun getSyncLocalToServerSuccess(value: List<ResponseSample>)
        fun getSyncLocalToServerFailure(msg: String)
    }

    interface ViewOptServerToLocal {
        fun getSyncServerToLocalSuccess(value: List<ResponseSample>)
        fun getSyncServerToLocalFailure(msg: String)
    }

    interface PresenterOpt : ReusableNetworkConnector
}