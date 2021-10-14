package com.finance.app.persistence.model

import java.io.Serializable

class EmploymentApplicantList : Serializable {
    var applicantDetails: ArrayList<EmploymentApplicantsModel> = ArrayList()
}