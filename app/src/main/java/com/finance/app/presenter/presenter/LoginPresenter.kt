package com.finance.app.presenter.presenter

import com.finance.app.presenter.connector.LoginConnector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

/**
 * Created by munishkumarthakur on 31/12/17.
 */
class LoginPresenter(private val viewOpt: LoginConnector.ViewOpt) : LoginConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        val requestApi = apiProject.api.loginUser(viewOpt.loginRequest)

        val x = requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewOpt.showProgressDialog() }
                .doFinally { viewOpt.hideProgressDialog() }
                .subscribe({ response -> onNextLogin(response) },
                        { e -> viewOpt.getLoginFailure(e?.message ?: "") })
    }

    private fun onNextLogin(response: Response.ResponseLogin) {
        if (response.responseCode == "200") {
            sharedPreferencesUtil.saveLoginData(response = response)
            viewOpt.getLoginSuccess(response)
        } else {
            viewOpt.getLoginFailure(response.responseMsg)
        }
    }
}