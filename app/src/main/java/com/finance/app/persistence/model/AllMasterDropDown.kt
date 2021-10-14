package com.finance.app.persistence.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finance.app.others.AppEnums
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

@Entity
class AllMasterDropDown : Serializable {

    @PrimaryKey
    var key: String = UUID.randomUUID().toString()
    var ApprovalStatus: ArrayList<DropdownMaster>? = null
    var AssetDetail: ArrayList<DropdownMaster>? = null
    var Caste: ArrayList<DropdownMaster>? = null
    var BankName: ArrayList<DropdownMaster>? = null
    var Branch: ArrayList<DropdownMaster>? = null
    var ChannelPartnerName: ArrayList<DropdownMaster>? = null
    var ChannelType: ArrayList<DropdownMaster>? = null
    var DOBProof: ArrayList<DropdownMaster>? = null
    var DetailQualification: ArrayList<DropdownMaster>? = null
    var DocumentProof: ArrayList<DropdownMaster>? = null
    var EntityType: ArrayList<DropdownMaster>? = null
    var Gender: ArrayList<DropdownMaster>? = null
    var IdentificationType: ArrayList<DropdownMaster>? = null
    var LoanInformationInterestType: ArrayList<DropdownMaster>? = null
    var LoanOwnership: ArrayList<DropdownMaster>? = null
    var LoanScheme: ArrayList<DropdownMaster>? = null
    var LoanType: ArrayList<DropdownMaster>? = null
    var MaritalStatus: ArrayList<DropdownMaster>? = null
    var Nationality: ArrayList<DropdownMaster>? = null
    var Obligate: ArrayList<DropdownMaster>? = null
    var OfficeType: ArrayList<DropdownMaster>? = null
    var Ownership: ArrayList<DropdownMaster>? = null
    var Qualification: ArrayList<DropdownMaster>? = null
    var ReferedBy: ArrayList<DropdownMaster>? = null
    var Relationship: ArrayList<DropdownMaster>? = null
    var Religion: ArrayList<DropdownMaster>? = null
    var RepaymentBank: ArrayList<DropdownMaster>? = null
    var SourcingChannelPartner: ArrayList<DropdownMaster>? = null
    var TypeOfOrganisation: ArrayList<DropdownMaster>? = null
    var VerifiedStatus: ArrayList<DropdownMaster>? = null
    var OccupationType: ArrayList<DropdownMaster>? = null
    var ProfileSegment: ArrayList<DropdownMaster>? = null
    var SubProfileSegment:ArrayList<DropdownMaster>? = null
    var EmploymentType:ArrayList<DropdownMaster>? = null
    var BusinessSetupType:ArrayList<DropdownMaster>? = null
    var Industry:ArrayList<DropdownMaster>? = null
    var Sector:ArrayList<DropdownMaster>? = null
    var Constitution:ArrayList<DropdownMaster>? = null
    var AccountType:ArrayList<DropdownMaster>? = null
    var AddressProof:ArrayList<DropdownMaster>? = null
    var ResidenceType:ArrayList<DropdownMaster>? = null
    var LivingStandardIndicators: ArrayList<DropdownMaster>? = null
    var PropertyUnitType: ArrayList<DropdownMaster>? = null
    var AlreadyOwnedProperty: ArrayList<DropdownMaster>? = null
    var PropertyOwnership: ArrayList<DropdownMaster>? = null
    var NatureOfPropertyTransaction: ArrayList<DropdownMaster>? = null
    var PropertyOccupiedBy: ArrayList<DropdownMaster>? = null
    var ReferenceRelationship: ArrayList<DropdownMaster>? = null
    var TenantNocAvailable: ArrayList<DropdownMaster>? = null
    var SalaryCredit: ArrayList<DropdownMaster>? = null
    var BounceEmiPaidInSameMonth: ArrayList<DropdownMaster>? = null
    var AssetSubType: ArrayList<DropdownMaster>? = null
    var CreditCardObligation: ArrayList<DropdownMaster>? = null
    var AssetOwnership: ArrayList<DropdownMaster>? = null
    var ReviewerResponseType: ArrayList<DropdownMaster>? = null
    var CustomerFollowUpStatus: ArrayList<DropdownMaster>? = null
    var LeadType: ArrayList<DropdownMaster>? = null
    var LeadNotificationType: ArrayList<DropdownMaster>? = null
    var LeadRejectionReason: ArrayList<DropdownMaster>? = null
    var DocumentCode: ArrayList<DropdownMaster>? = null
    var PropertyType : ArrayList<DropdownMaster>? = null
    var TransactionType : ArrayList<DropdownMaster>? = null
    fun getMasterDropDownMap(): HashMap<AppEnums.DropdownMasterType, ArrayList<DropdownMaster>?> {
        val map = HashMap<AppEnums.DropdownMasterType, ArrayList<DropdownMaster>?>()
        map[AppEnums.DropdownMasterType.Gender] = Gender
        map[AppEnums.DropdownMasterType.LeadType] = LeadType
        map[AppEnums.DropdownMasterType.AssetOwnership] = AssetOwnership
        map[AppEnums.DropdownMasterType.Nationality] = Nationality
        map[AppEnums.DropdownMasterType.LoanInformationInterestType] = LoanInformationInterestType
        map[AppEnums.DropdownMasterType.SourcingChannelPartner] = SourcingChannelPartner
        map[AppEnums.DropdownMasterType.Caste] = Caste
        map[AppEnums.DropdownMasterType.Branch] = Branch
        map[AppEnums.DropdownMasterType.ChannelPartnerName] = ChannelPartnerName
        map[AppEnums.DropdownMasterType.ChannelType] = ChannelType
        map[AppEnums.DropdownMasterType.DOBProof] = DOBProof
        map[AppEnums.DropdownMasterType.DetailQualification] = DetailQualification
        map[AppEnums.DropdownMasterType.DocumentProof] = DocumentProof
        map[AppEnums.DropdownMasterType.EntityType] = EntityType
        map[AppEnums.DropdownMasterType.IdentificationType] = IdentificationType
        map[AppEnums.DropdownMasterType.LoanOwnership] = LoanOwnership
        map[AppEnums.DropdownMasterType.LoanScheme] = LoanScheme
        map[AppEnums.DropdownMasterType.LoanType] = LoanType
        map[AppEnums.DropdownMasterType.MaritalStatus] = MaritalStatus
        map[AppEnums.DropdownMasterType.Obligate] = Obligate
        map[AppEnums.DropdownMasterType.OfficeType] = OfficeType
        map[AppEnums.DropdownMasterType.Ownership] = Ownership
        map[AppEnums.DropdownMasterType.Qualification] = Qualification
        map[AppEnums.DropdownMasterType.ReferedBy] = ReferedBy
        map[AppEnums.DropdownMasterType.Relationship] = Relationship
        map[AppEnums.DropdownMasterType.Religion] = Religion
        map[AppEnums.DropdownMasterType.RepaymentBank] = RepaymentBank
        map[AppEnums.DropdownMasterType.SourcingChannelPartner] = SourcingChannelPartner
        map[AppEnums.DropdownMasterType.TypeOfOrganisation] = TypeOfOrganisation
        map[AppEnums.DropdownMasterType.VerifiedStatus] = VerifiedStatus
        map[AppEnums.DropdownMasterType.OccupationType] = OccupationType
        map[AppEnums.DropdownMasterType.ProfileSegment] = ProfileSegment
        map[AppEnums.DropdownMasterType.SubProfileSegment] = SubProfileSegment
        map[AppEnums.DropdownMasterType.EmploymentType] = EmploymentType
        map[AppEnums.DropdownMasterType.BusinessSetupType] = BusinessSetupType
        map[AppEnums.DropdownMasterType.Industry] = Industry
        map[AppEnums.DropdownMasterType.Sector] = Sector
        map[AppEnums.DropdownMasterType.Constitution] = Constitution
        map[AppEnums.DropdownMasterType.AccountType] = AccountType
        map[AppEnums.DropdownMasterType.AddressProof] = AddressProof
        map[AppEnums.DropdownMasterType.ResidenceType] = ResidenceType
        map[AppEnums.DropdownMasterType.LivingStandardIndicators] = LivingStandardIndicators
        map[AppEnums.DropdownMasterType.PropertyUnitType] = PropertyUnitType
        map[AppEnums.DropdownMasterType.AlreadyOwnedProperty] = AlreadyOwnedProperty
        map[AppEnums.DropdownMasterType.PropertyOwnership] = PropertyOwnership
        map[AppEnums.DropdownMasterType.NatureOfPropertyTransaction] = NatureOfPropertyTransaction
        map[AppEnums.DropdownMasterType.PropertyOccupiedBy] = PropertyOccupiedBy
        map[AppEnums.DropdownMasterType.ReferenceRelationship] = ReferenceRelationship
        map[AppEnums.DropdownMasterType.TenantNocAvailable] = TenantNocAvailable
        map[AppEnums.DropdownMasterType.SalaryCredit] = SalaryCredit
        map[AppEnums.DropdownMasterType.BounceEmiPaidInSameMonth] = BounceEmiPaidInSameMonth
        map[AppEnums.DropdownMasterType.AssetSubType] = AssetSubType
        map[AppEnums.DropdownMasterType.CreditCardObligation] = CreditCardObligation
        map[AppEnums.DropdownMasterType.AssetOwnership] = AssetOwnership
        map[AppEnums.DropdownMasterType.ReviewerResponseType] = ReviewerResponseType
        map[AppEnums.DropdownMasterType.CustomerFollowUpStatus] = CustomerFollowUpStatus
        map[AppEnums.DropdownMasterType.LeadNotificationType] = LeadNotificationType
        map[AppEnums.DropdownMasterType.LeadRejectionReason] = LeadRejectionReason
        map[AppEnums.DropdownMasterType.AssetDetail] = AssetDetail
        map[AppEnums.DropdownMasterType.BankName]=BankName
        map[AppEnums.DropdownMasterType.PropertyType] = PropertyType
        map[AppEnums.DropdownMasterType.TransactionType] = TransactionType
        return map
    }

}