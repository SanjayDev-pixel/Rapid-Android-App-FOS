package com.finance.app.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class AllLeadMaster : Serializable {
    @PrimaryKey
    var leadID: Int? = null
    var actualCompletionDate: String? = null
    var applicantAddress: String? = null
    var applicantAlternativeContactNumber: String? = null
    var applicantContactNumber: String? = null
    var applicantEmail: String? = null
    var applicantFirstName: String? = null
    var applicantLastName: String? = null
    var applicantMiddleName: String? = null
    var branchID: String? = null
    var createdOn: String? = null
    var currentStatus: String? = null
    var dsaID: String? = null
    var estimatedCompletionDate: String? = null
    var isConvertedToLoanApplication: Boolean = false
    var lastModifiedOn: String? = null
    var leadFailedReason: String? = null
    var leadNumber: String? = null
    var amountRequest: Long? = null
    var leadOwnershipEntityID: String? = null
    var leadReference: String? = null
    var leadSourceTypeDetailID: Int? = null
    var loanApplicationID: Int? = null
    var loanProductID: Int? = null
    var loanProductName: String? = null
    var remarks: String? = null
    var status: String? = null
    var state: String? = null
    var district: String? = null
    var city: String? = null
    var personalData: PersonalApplicantList = PersonalApplicantList()
    var loanData: LoanInfoModel = LoanInfoModel()
    var employmentData: EmploymentApplicantList = EmploymentApplicantList()
    var bankData: BankDetailList = BankDetailList()
    var assetLiabilityData: AssetLiabilityList = AssetLiabilityList()
    var propertyData: PropertyModel = PropertyModel()
    var referenceData = ReferencesList()
    var documentData: DocumentDetailList = DocumentDetailList()
    var isDetailAlreadySync: Boolean = false
    var isSyncWithServer: Boolean = true
    var channelPartnerID:Int? =null
    var sourcingChannelPartnerTypeDetailCode:String?=null
    var sourcingChannelPartnerTypeDetailID:Int=0
}