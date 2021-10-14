package com.finance.app.others

import com.finance.app.R

/**
 * Created by munishkumarthakur on 02/01/18.
 */
class AppEnums {

    enum class Temp(val id: Int, val dataName: String) {
        Val1(1001, "Value1001");
    }

    enum class ScreenLoanApp(val screenName: String, val icon: Int) {
        LOAN_INFORMATION("Loan Information", R.drawable.loan_info_white),
        PERSONAL("Personal", R.drawable.personal_info_white),
        EMPLOYMENT("Employment", R.drawable.employment_icon_white),
        BANK_DETAIL("Bank Details", R.drawable.bank_icon_white),
        LIABILITY_AND_ASSET("Liability & Asset", R.drawable.assest_details_white),
        REFERENCE("Reference", R.drawable.reffrence_white),
        PROPERTY("Property", R.drawable.property_icon_white),
        DOCUMENT_CHECKLIST("Document Checklist", R.drawable.checklist),

        DEFAULT("Default", R.drawable.app_logo);

        companion object {
            fun getData(screenName: String?): ScreenLoanApp {
                return when (screenName) {
                    LOAN_INFORMATION.screenName -> LOAN_INFORMATION
                    PERSONAL.screenName -> PERSONAL
                    EMPLOYMENT.screenName -> EMPLOYMENT
                    BANK_DETAIL.screenName -> BANK_DETAIL
                    LIABILITY_AND_ASSET.screenName -> LIABILITY_AND_ASSET
                    REFERENCE.screenName -> REFERENCE
                    PROPERTY.screenName -> PROPERTY
                    //
                    DOCUMENT_CHECKLIST.screenName -> DOCUMENT_CHECKLIST

                    else -> DEFAULT
                }
            }
        }
    }

    enum class ADDRESS_TYPE (val type: String) {
        SENP("SENP"), SALARY("SALARY")
    }

    enum class INCOME_TYPE (val type: String) {
        GROSS_INCOME("GROSS_INCOME"), DEDUCTION("DEDUCTION"),
        LAST_YEAR_INCOME("LAST_YEAR_INCOME"), CURRENT_YEAR_INCOME("CURRENT_YEAR_INCOME")
    }

    enum class LEAD_TYPE(val type: String) {
        PENDING ("Pending"), SUBMITTED("Submitted"), REJECTED("Rejected"), NEW ("New"), ALL ("All")
    }

    enum class RESPONSEAPI(val type: String) {
        SUCCESS("200")
    }

    enum class FormType(val type: String) {
        LOANINFO("LOAN_INFORMATION"), PERSONALINFO("APPLICANT_PERSONAL"), EMPLOYMENT("APPLICATION_EMPLOYMENT"),
        BANKDETAIL("BANK_DETAIL"), LIABILITYASSET("LIABILITY_AND_ASSET"), PROPERTY("APPLICATION_PROPERTY"),
        REFERENCE("APPLICATION_REFERENCE"),DOCUMENT("APPLICATION_DOCUMENT_CHECKLIST")
    }

    enum class EnumEventChangeLoanApplicationFragmentNavigation {
        NEXT, PREVIOUS
    }
    enum class PreviewType {
        ASSETS, BANK, CARD, OBLIGATION, REFERNCE, DOCUMENT
    }
    enum class DropdownMasterType {
        Gender, Caste, Branch, ChannelPartnerName, ChannelType, DOBProof, DetailQualification,
        DocumentProof, EntityType, IdentificationType, LoanInformationInterestType, LoanOwnership,
        LoanScheme, LoanType, MaritalStatus, Nationality, Obligate, OfficeType, Ownership, Qualification,
        ReferedBy, Relationship, Religion, RepaymentBank, SourcingChannelPartner, TypeOfOrganisation,
        VerifiedStatus, OccupationType, ProfileSegment, SubProfileSegment, EmploymentType, BusinessSetupType,
        Industry, Sector, Constitution, AccountType, AddressProof, ResidenceType, LivingStandardIndicators,
        PropertyUnitType, AlreadyOwnedProperty, PropertyOwnership, NatureOfPropertyTransaction,
        PropertyOccupiedBy, ReferenceRelationship, TenantNocAvailable, SalaryCredit, BounceEmiPaidInSameMonth,
        AssetSubType, CreditCardObligation, AssetOwnership, ReviewerResponseType, CustomerFollowUpStatus,
        LeadType, LeadNotificationType, LeadRejectionReason, AssetDetail,BankName,PropertyType,TransactionType
    }

}
