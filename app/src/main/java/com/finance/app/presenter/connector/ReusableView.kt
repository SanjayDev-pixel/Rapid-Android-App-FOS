package com.finance.app.presenter.connector

/**
 * Created by munishkumarthakur on 04/11/17.
 */
interface ReusableView {
    fun showToast(msg: String)
    fun showProgressDialog()
    fun hideProgressDialog()
    fun getApiFailure(msg: String)
}