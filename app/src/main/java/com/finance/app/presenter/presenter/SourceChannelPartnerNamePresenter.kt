package com.finance.app.presenter.presenter

import com.finance.app.presenter.connector.LoanApplicationConnector
import com.finance.app.presenter.connector.SourceChannelPartnerNameConnector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

class SourceChannelPartnerNamePresenter(private val channelPartner: SourceChannelPartnerNameConnector.SourceChannelPartnerName) : LoanApplicationConnector.PresenterOpt {
    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        if (type == ConstantsApi.CALL_SOURCE_CHANNEL_PARTNER_NAME) {
            callSourceChannelPartnerNameApi()
        }
    }

    private fun callSourceChannelPartnerNameApi() {
        val requestApi = apiProject.api
                .sourceChannelPartnerName(branchId = channelPartner.branchId,
                        channelType = channelPartner.channelTypeId, employeeId = channelPartner.employeeId)

        requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { channelPartner.showProgressDialog() }
                .doFinally { channelPartner.hideProgressDialog() }
                .subscribe({ response -> onSourceChannelPartnerName(response) },
                        { e ->
                            channelPartner.getSourceChannelPartnerNameFailure(e?.message ?: "")
                        })
    }

    private fun onSourceChannelPartnerName(response: Response.ResponseSourceChannelPartnerName) {
        if (response.responseCode == "200") {
            channelPartner.getSourceChannelPartnerNameSuccess(response)
        } else {
            channelPartner.getSourceChannelPartnerNameFailure(response.responseMsg)
        }
    }
}