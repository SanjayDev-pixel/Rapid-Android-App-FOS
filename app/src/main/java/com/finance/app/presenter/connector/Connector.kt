package com.finance.app.presenter.connector

/**
 * Created by munishkumarthakur on 21/12/19.
 */
class Connector {

    interface ViewOpt<Request, Response> : ReusableView {
        val apiRequest: Request?
        fun getApiSuccess(value: Response)
        //fun getApiValidationFail(value : Response)
    }
    interface PresenterOpt
}