package com.finance.app.persistence.model

import java.io.Serializable

class KycListModel: Serializable {
    var leadID:Int?=null
    var kycApplicantDetailsList:ArrayList<KycApplicantDetailsList> = ArrayList()

}