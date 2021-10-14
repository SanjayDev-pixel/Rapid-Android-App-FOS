package com.finance.app.persistence.model

import java.io.Serializable

class PersonalApplicantsModel : Serializable {
    var addressDetailList: ArrayList<AddressDetail>? = ArrayList()
    var age: Int? = null
    var alternateContact: String? = null
    var applicantID: Int? = null
    var casteTypeDetailID: Int? = null
    var contactDetail: ContactDetail? = ContactDetail()
    var applicantKycList: ArrayList<KYCDetail>? = ArrayList()
    var dateOfBirth: String? = null
    var leadApplicantNumber: String? = null
    var detailQualificationTypeDetailID: Int? = null
    var dobProofTypeDetailID: Int? = null
    var fatherFirstName: String? = null
    var fatherLastName: String? = null
    var fatherMiddleName: String? = null
    var firstName: String? = null
    var genderTypeDetailID: Int? = null
    var isMainApplicant: Boolean = false
    var lastName: String? = null
    var loanApplicationID: Int? = null
    var maritialStatusTypeDetailID: Int? = null
    var middleName: String? = null
    var nationalityTypeDetailID: Int? = null
    var numberOfDependents: Int? = null
    var numberOfEarningMembers: Int? = null
    var presentAccommodationTypeDetailID: Int? = null
    var qualificationTypeDetailID: Int? = null
    var religionTypeDetailID: Int? = null
    var relationshipTypeDetailId: Int? = null
    var spouseFirstName: String? = null
    var spouseLastName: String? = null
    var spouseMiddleName: String? = null
    var incomeConsidered: Boolean? = null
    var livingStandardTypeDetailId: Int? = null
    var numberOfFamilyMembersOthers :Int? = null
    var isActive: Boolean = true
}
