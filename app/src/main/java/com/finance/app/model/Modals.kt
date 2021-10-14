package com.finance.app.model

import android.graphics.Bitmap
import com.finance.app.persistence.model.PersonalApplicantsModel

class Modals {
    data class AddKyc(var idType: String, var idNum: String, var kycImage: Bitmap?, var issueDate: String,
                      var expiryDate: String, var verifiedStatus: String)
    data class ApplicantPersonal(var personalApplicants: ArrayList<PersonalApplicantsModel>)
    data class ApplicantTab(var name: String?, var leadApplicantNumber: Boolean)
    data class BankApplicants(var companyName: String)

    data class EmploymentApplicants(var companyName: String)

    data class AssetLiabilityApplicants(var companyName: String)
}

