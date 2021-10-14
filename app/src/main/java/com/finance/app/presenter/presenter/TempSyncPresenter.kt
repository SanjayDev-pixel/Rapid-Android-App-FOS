package com.finance.app.presenter.presenter

import com.finance.app.presenter.connector.TempSyncConnector
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.constants.ConstantsApi.CALL_SYNC_LOCAL_TO_SERVER
import motobeans.architecture.constants.ConstantsApi.CALL_SYNC_SERVER_TO_LOCAL
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import javax.inject.Inject

/**
 * Created by motobeans on 1/2/2018.
 */
class TempSyncPresenter(
    private val viewOptLocalToServer: TempSyncConnector.ViewOptLocalToServer? = null,
    private val viewOptServerToLocal: TempSyncConnector.ViewOptServerToLocal? = null) : TempSyncConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var database: DataBaseUtil
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }


    override fun callNetwork(type: ConstantsApi) {
        when (type) {
            CALL_SYNC_LOCAL_TO_SERVER -> {
                //callSyncDCRLocalToServerCheckOutOnly()
                callSyncLocalToServer()
            }
            CALL_SYNC_SERVER_TO_LOCAL -> callSyncServerToLocal()
            else -> {
            }
        }
    }

    private fun callSyncLocalToServer() {

    }

    private fun callSyncServerToLocal() {

    }

}