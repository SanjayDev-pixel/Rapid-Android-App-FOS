package com.finance.app.persistence.model

import java.io.Serializable

class BankDetailBean : Serializable {
    var applicationDocumentID: String? = null
    var accountHolderName: String? = null
    var accountNumber: String? = null
    var accountTypeDetailID: Int? = null
    var accountTypeName: String? = null
    var bankName: String? = null
    var bankNameTypeDetailID: Int? = null
    var firstName: String? = null
    var loanApplicationID: Int? = null
    var numberOfCredit: String? = null
    var salaryCreditTypeDetailID: Int? = null
}
