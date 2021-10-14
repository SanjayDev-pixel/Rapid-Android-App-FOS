package com.finance.app.presenter.connector

interface ValidationHandler {
    fun isValid(): Boolean
    fun resetValidation()
    fun showError(isShow: Boolean)
//    fun isMandatory(isMandatory: Boolean)
//    fun getErrorMessage(): String
}