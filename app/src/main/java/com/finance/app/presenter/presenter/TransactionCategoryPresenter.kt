package com.finance.app.presenter.presenter

import com.finance.app.presenter.connector.TransactionCategoryConnector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

class TransactionCategoryPresenter(private val transactionCategory: TransactionCategoryConnector.TransactionCategory) : TransactionCategoryConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        if (type == ConstantsApi.CALL_TRANSACTON_CATEGORY) {
            callApi()
        }
    }

    private fun callApi() {
        val requestApi = apiProject.api.gettransactionCategory(transactionCategory.ownershipId, transactionCategory.transactionId)

        requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { transactionCategory.showProgressDialog() }
                .doFinally { transactionCategory.hideProgressDialog() }
                .subscribe({ response -> onTransactionCategory(response) },
                        { e -> transactionCategory.getTransactionCategoryFailure(e?.message ?: "") })
    }

    private fun onTransactionCategory(response: Response.ResponsePropertyNature) {
        if (response.responseCode == "200") {
            transactionCategory.getTransactionCategorySuccess(response)
        } else {
            transactionCategory.getTransactionCategoryFailure(response.responseMsg)
        }
    }
}