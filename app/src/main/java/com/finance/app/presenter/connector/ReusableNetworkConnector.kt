package com.finance.app.presenter.connector

import motobeans.architecture.constants.ConstantsApi

/**
 * Created by munishkumarthakur on 04/11/17.
 */
interface ReusableNetworkConnector {
    fun callNetwork(type: ConstantsApi)
}