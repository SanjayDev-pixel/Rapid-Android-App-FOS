package com.finance.app.presenter.presenter

import com.finance.app.presenter.connector.TestConnector.PresenterOpt
import com.finance.app.presenter.connector.TestConnector.ViewOpt
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.constants.ConstantsApi.CALL_TEMP
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response.ResponseSample
import javax.inject.Inject

/**
 * Created by munishkumarthakur on 31/12/17.
 */
class TestPresenter(private val viewOpt: ViewOpt) : PresenterOpt {
    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        when (type) {
            CALL_TEMP -> callTempApi()
        }
    }

    private fun callTempApi() {
        val requestApi = apiProject.api.postTempApi(viewOpt.sampleRequest)

        requestApi
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _ -> viewOpt.showProgressDialog() }
            .doFinally { viewOpt.hideProgressDialog() }
            .subscribe({ resposne -> onNextForgotPassword(resposne) },
                { e -> viewOpt.getObjectFailure(e?.message ?: "") })
    }


    private fun onNextForgotPassword(response: ResponseSample) {
        val msg = "Data does not exist"
        if (response.status == 200) {
//            sharedPreferencesUtil!!.saveLoginData(response = response)
            viewOpt.getObjectSuccess(response)
        } else {
            viewOpt.getObjectFailure(msg)
        }
    }
}