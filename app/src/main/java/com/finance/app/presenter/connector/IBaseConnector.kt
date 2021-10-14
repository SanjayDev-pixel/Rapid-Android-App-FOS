package com.finance.app.presenter.connector

/**
 * Created by VishalRathi on 19/12/19.
 */

interface IBaseConnector : ReusableView {
    fun <Response> getApiSuccess(value: Response)
}