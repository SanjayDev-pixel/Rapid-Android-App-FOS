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

class CityPresenter(private val city: DistrictCityConnector.City) : DistrictCityConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {}

    fun callCityApi(addressType: AppEnums.ADDRESS_TYPE? = null) {
        val requestApi = apiProject.api.getCities(city.districtId)

        requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { city.showProgressDialog() }
                .doFinally { city.hideProgressDialog() }
                .subscribe({ response -> onCity(response, addressType) },
                        { e -> city.getCityFailure(e?.message ?: "") })
    }

    private fun onCity(response: Response.ResponseCity, addressType: AppEnums.ADDRESS_TYPE? = null) {
        if (response.responseCode == "200") {
            city.getCitySuccess(response, addressType)
        } else {
            city.getCityFailure(response.responseMsg)
        }
    }
}