package com.finance.app.presenter.presenter

import com.finance.app.presenter.connector.DocumentUploadConnector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

/**
 * Created by munishkumarthakur on 31/12/17.
 */
class DocumentUploadPresenter(private val documentUpload: DocumentUploadConnector.ViewOpt) : DocumentUploadConnector.PresenterOpt {

    @Inject
    lateinit var apiProject: ApiProject
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun callNetwork(type: ConstantsApi) {
        if (type == ConstantsApi.CALL_DOCUMENT_UPLOAD) {
            callDocumentUploadApi()
        }
    }

    private fun callDocumentUploadApi() {
        val requestApi = apiProject.api.uploadDocument(documentUpload.documentName!!)

        requestApi
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _ -> documentUpload.showProgressDialog() }
                .doFinally { documentUpload.hideProgressDialog() }
                .subscribe({ resposne -> onDocumentUpload(resposne) },
                        { e -> documentUpload.getDocumentUploadFailure(e?.message ?: "") })
    }

    private fun onDocumentUpload(response: Response.ResponseDocumentUpload) {
        if (response.responseCode == "200") {
            documentUpload.getDocumentUploadSuccess(response)
        } else {
            documentUpload.getDocumentUploadFailure(response.responseMsg)
        }
    }
}