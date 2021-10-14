package com.finance.app.persistence.model

import java.io.Serializable

class LoanApplicationRequest : Serializable {

    var draftData: String? = null
    var editable: Boolean? = null
    var leadID: Int? = null
    var storageType: String? = null
}