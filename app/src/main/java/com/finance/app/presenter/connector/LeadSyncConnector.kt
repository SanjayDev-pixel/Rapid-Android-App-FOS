package com.finance.app.presenter.connector

import com.finance.app.persistence.model.AllLeadMaster

/**
 * Created by motobeans on 1/2/2018.
 */
interface LeadSyncConnector {
    interface ViewOptLocalToServer {
        fun getLocalLeadSyncLocalToServerSuccess(value: AllLeadMaster)
        fun getLocalLeadSyncLocalToServerFailure(msg: String)
    }

    interface ViewOptServerToLocal {
        fun getLocalLeadSyncServerToLocalSuccess(value: List<AllLeadMaster>)
        fun getLocalLeadSyncServerToLocalFailure(msg: String)
    }

    interface PresenterOpt : ReusableNetworkConnector
}