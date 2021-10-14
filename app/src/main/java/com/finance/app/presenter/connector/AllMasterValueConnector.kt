package com.finance.app.presenter.connector
import motobeans.architecture.retrofit.response.Response

interface AllMasterValueConnector {
    interface MasterDropdown : ReusableView {
        fun getAllMasterDropdownSuccess(dropdown: Response.ResponseAllMasterDropdown)
        fun getAllMasterDropdownFailure(msg: String)
    }

    interface StateDropdown : ReusableView {
        fun getStatesDropdownSuccess(value: Response.ResponseStatesDropdown)
        fun getStatesDropdownFailure(msg: String)

    }

    interface PresenterOpt : ReusableNetworkConnector
}