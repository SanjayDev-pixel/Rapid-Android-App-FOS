package com.optcrm.optreporting.app.workers

import com.example.munishkumarthakur.workmanager_testing.workers.WorkerInterface.WorkerTask
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.presenter.connector.LeadSyncConnector
import com.finance.app.presenter.presenter.LeadSyncPresenter
import motobeans.architecture.constants.ConstantsApi

class UtilWorkersTaskLeadSync(
    callback: WorkerTask.callback? = null) : UtilWorkersTask(), WorkerTask.worker,
    LeadSyncConnector.ViewOptLocalToServer, LeadSyncConnector.ViewOptServerToLocal {

    lateinit var presenter: LeadSyncPresenter

    override fun execute() {
        println("Worker Running - initWorker DCR")

        presenter = LeadSyncPresenter(viewOptLocalToServer = this, viewOptServerToLocal = this)

        handleLocalToServerSync()
        handleServerToLocalSync()
    }

    override fun handleLocalToServerSync() {
        presenter.callNetwork(ConstantsApi.CALL_SYNC_LEAD_LOCAL_TO_SERVER)
    }

    override fun handleServerToLocalSync() {

    }

    override fun getLocalLeadSyncLocalToServerSuccess(value: AllLeadMaster) {
    }

    override fun getLocalLeadSyncLocalToServerFailure(msg: String) {
    }

    override fun getLocalLeadSyncServerToLocalSuccess(value: List<AllLeadMaster>) {
    }

    override fun getLocalLeadSyncServerToLocalFailure(msg: String) {
    }
}