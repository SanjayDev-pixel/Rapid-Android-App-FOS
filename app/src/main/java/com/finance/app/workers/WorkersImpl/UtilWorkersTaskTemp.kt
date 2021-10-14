package com.optcrm.optreporting.app.workers

import com.example.munishkumarthakur.workmanager_testing.workers.WorkerInterface.WorkerTask
import com.finance.app.presenter.connector.TempSyncConnector
import com.finance.app.presenter.presenter.TempSyncPresenter
import motobeans.architecture.constants.ConstantsApi.CALL_SYNC_LOCAL_TO_SERVER
import motobeans.architecture.constants.ConstantsApi.CALL_SYNC_SERVER_TO_LOCAL
import motobeans.architecture.retrofit.response.Response.ResponseSample


class UtilWorkersTaskTemp(
    callback: WorkerTask.callback? = null) : UtilWorkersTask(), WorkerTask.worker, TempSyncConnector.ViewOptLocalToServer, TempSyncConnector.ViewOptServerToLocal {

  lateinit var presenter: TempSyncPresenter

  override fun execute() {
    println("Worker Running - initWorker DCR")

    presenter = TempSyncPresenter(viewOptLocalToServer = this,
        viewOptServerToLocal = this)

    handleLocalToServerSync()
    handleServerToLocalSync()
  }

  override fun handleLocalToServerSync() {
    presenter.callNetwork(CALL_SYNC_LOCAL_TO_SERVER)
  }

  override fun handleServerToLocalSync() {
    presenter.callNetwork(CALL_SYNC_SERVER_TO_LOCAL)
  }

  override fun getSyncLocalToServerSuccess(value: List<ResponseSample>) {
  }

  override fun getSyncLocalToServerFailure(msg: String) {
  }

  override fun getSyncServerToLocalSuccess(value: List<ResponseSample>) {
  }

  override fun getSyncServerToLocalFailure(msg: String) {
  }
}