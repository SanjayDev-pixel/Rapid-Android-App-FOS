package com.finance.app.persistence.model

import java.io.Serializable

class ObligationDetail : Serializable {
    var loanAmount: Int? = null
    var tenure: Int? = null
    var balanceTenure: Int? = null
    var emiAmount: Int? = null
    var numberOfBouncesInLastSixMonth: Int? = null
    var numberOfBouncesInLastNineMonth: Int? = null
    var financerName: String? = null
    var loanAccountNumber: String? = null
    var borrowerNameTypeDetailID: Int? = null
    var bounseEmiPaidInSameMonth: Int? = null
    var loanOwnershipTypeDetailID: Int? = null
    var obligateTypeDetailID: Int? = null
    var loanTypeTypeDetailID: Int? = null
    var repaymentBankTypeDetailID: Int? = null
    var loanDisbursementDate: String? = null
}