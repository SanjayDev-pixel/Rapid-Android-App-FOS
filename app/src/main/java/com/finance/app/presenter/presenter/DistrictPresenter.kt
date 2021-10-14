package com.finance.app.presenter.presenter

import com.finance.app.others.AppEnums
import com.finance.app.presenter.connector.DistrictCityConnector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

class DistrictPresenter(private val district: DistrictCityConnector.District) : DistrictCityConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {

    }

    fun callDistrictApi(addressType: AppEnums.ADDRESS_TYPE? = null) {
        val requestApi = apiProject.api.getDistricts(district.stateId)

        requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { district.showProgressDialog() }
                .doFinally { district.hideProgressDialog() }
                .subscribe({ response -> onDistrict(response, addressType) },
                        { e -> district.getDistrictFailure(e?.message ?: "") })
    }

    private fun onDistrict(response: Response.ResponseDistrict, addressType: AppEnums.ADDRESS_TYPE? = null) {
        if (response.responseCode == "200") {
            district.getDistrictSuccess(response, addressType)
        } else {
            district.getDistrictFailure(response.responseMsg)
        }
    }
}