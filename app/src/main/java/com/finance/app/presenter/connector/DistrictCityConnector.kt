package com.finance.app.presenter.connector

import com.finance.app.others.AppEnums
import motobeans.architecture.retrofit.response.Response

interface DistrictCityConnector {

    interface District : ReusableView {
        val stateId: String

        fun getDistrictSuccess(value: Response.ResponseDistrict, addressType: AppEnums.ADDRESS_TYPE? = null)
        fun getDistrictFailure(msg: String)

    }

    interface City : ReusableView {
        val districtId: String

        fun getCitySuccess(value: Response.ResponseCity, addressType: AppEnums.ADDRESS_TYPE? = null)
        fun getCityFailure(msg: String)

    }

    interface PresenterOpt : ReusableNetworkConnector
}