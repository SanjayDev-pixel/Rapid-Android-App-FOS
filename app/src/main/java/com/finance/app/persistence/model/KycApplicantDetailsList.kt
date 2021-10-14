package com.finance.app.persistence.model

import java.io.Serializable

class KycApplicantDetailsList: Serializable {
    var leadApplicantNumber: String? =null
    var latestKycObj : String ? = null
    var kycAadharZipInlineDataList: ArrayList<KycAadharZipInlineDataList> = ArrayList()
    var kycPanQrCodeDataList: ArrayList<KycAadharZipInlineDataList> = ArrayList()
    var kycDLQrCodeDataList: ArrayList<KycAadharZipInlineDataList> = ArrayList()
    var kycPanDLDataList: ArrayList<KycAadharZipInlineDataList> = ArrayList()
    var kycVoterCardQrCodeDataList: ArrayList<KycAadharZipInlineDataList> = ArrayList()

}