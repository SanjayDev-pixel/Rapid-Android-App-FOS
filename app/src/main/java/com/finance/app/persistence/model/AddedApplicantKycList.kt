package com.finance.app.persistence.model

import java.io.Serializable

class AddedApplicantKycList : Serializable {
    var applicantDetails : ArrayList<BasicAddedKycData> = ArrayList()
}