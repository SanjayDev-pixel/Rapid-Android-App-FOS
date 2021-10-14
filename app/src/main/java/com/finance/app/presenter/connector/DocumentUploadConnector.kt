package com.finance.app.presenter.connector

import motobeans.architecture.retrofit.response.Response
import retrofit2.http.Multipart

/**
 * Created by munishkumarthakur on 31/12/17.
 */
interface DocumentUploadConnector {

    interface ViewOpt : ReusableView {
        val leadId: Int?
        val applicantId: Int?
        val documentName: String?
        val documentTypeDetailId: Int
        val actualDocument: Multipart
        fun getDocumentUploadSuccess(value: Response.ResponseDocumentUpload)
        fun getDocumentUploadFailure(msg: String)

    }

    interface PresenterOpt : ReusableNetworkConnector
}