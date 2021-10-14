package com.finance.app.persistence.model

import java.io.Serializable

class LoanInfoModel:Serializable {

    var affordableEMI: Double? = null
    var applicationNumber: String? = null
    var channelPartnerDsaID: Int? = null
    var interestTypeTypeDetailID: Int? = null
    var leadID: Int? = null
    var loanAmountRequest: Int? = null
    var loanApplicationID: Int? = null
    var loanPurposeID: Int? = null
    var loanSchemeTypeDetailID: Int? = null
    var logginUserEntityID: Int? = null
    var productID: Int? = null
    var salesOfficerEmpID: Int? = null
    var sourcingChannelPartnerTypeDetailID: Int? = null
    var tenure: Int? = null
    var isPropertySelected: Boolean? = null
    var channelPartnerName: String?= null
}
