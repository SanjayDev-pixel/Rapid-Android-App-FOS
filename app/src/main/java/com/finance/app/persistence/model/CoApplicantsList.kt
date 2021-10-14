package com.finance.app.persistence.model

import java.io.Serializable

class CoApplicantsList:Serializable {

    var applicantID: Int? = null
    var entityID: Int? = null
    var firstName: String? = ""
    var incomeConsidered: Boolean? = false
    var isMainApplicant: Boolean? = false
    var leadApplicantNumber: String? = ""
    var middleName: String? = ""
    var applicantType: String?=""
    var mobile: String?=""
}