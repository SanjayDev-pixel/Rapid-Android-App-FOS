package com.finance.app.persistence.model

import java.io.Serializable

class KYCDetail : Serializable {

    var applicationDocumentID: String? = null
    var identificationNumber: String = ""
    var issueDate: String = ""
    var loanApplicationID: Int? = 0
    var applicantKycID: Int? = 0
    var expireDate: String? = ""
    var applicantID: Int? = 0
    var lastModifiedOn: String? = ""
    var identificationTypeDetailID: Int? = 0
    var identificationTypeDetail: String? = ""
    var verifiedStatusTypeDetailID: Int? = 0
    var verifiedStatusTypeDetail: String? = ""
    var createdBy: Int? = 0
    var createdOn: String? = ""
    var lastModifiedBy: Int? = 0
    var active: Boolean? = false
}