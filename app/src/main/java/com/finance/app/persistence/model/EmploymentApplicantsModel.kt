package com.finance.app.persistence.model

import java.io.Serializable

class EmploymentApplicantsModel : Serializable {
    var addressBean: AddressDetail? = null
    var allEarningMembers: Boolean? = null
    var businessSetupTypeDetailID: Int? = null


    var isMainApplicant: Boolean? = false
    var constitutionTypeDetailID: Int? = null
    var dateOfIncorporation: String? = null
    var dateOfJoining: String? = null
    var employeeID: String? = null

    var gstRegistration: String? = null
    var industryTypeDetailID: Int? = null
    var loanApplicationID: Int? = null
    var officialMailID: String? = null

    var isPensioner: Boolean? = null
    var profileSegmentTypeDetailID: Int? = null

    var sectorTypeDetailID: Int? = null
    var subProfileTypeDetailID: Int? = null

    var designation: String? = null
    var employerContactNumber: String? = null
    var incomeDetail: IncomeDetail? = null

    var companyName: String? = null
    var totalExperience: String? = null
    var retirementAge: Int? = null
    var businessVinatgeInYear: Int? = null
    var leadApplicantNumber: String? = null
    var incomeConsidered: Boolean? = null
    var employmentTypeDetailID: Int? = null

}
