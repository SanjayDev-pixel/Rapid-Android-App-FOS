package com.finance.app.persistence.converters

import androidx.room.TypeConverter
import com.finance.app.persistence.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import java.util.*

/**
 * Created by munishkumarthakur on 19/03/18.
 */
class Converters {

    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
//        for(dateFormate in alDateFormats){
//          try {
//            return dateFormate.parse(value)
//          } catch (e: Exception) {
//            e.printStackTrace()
//          }
//        }
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): String? {
        return (date?.time)?.toString()
    }

    @TypeConverter
    fun fromStringToAllDropDownMaster(value: String?): AllMasterDropDown? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<AllMasterDropDown>() {

        }.type
        return Gson().fromJson<AllMasterDropDown>(value , listType)
    }

    @TypeConverter
    fun fromAllDropDownMasterToString(list: AllMasterDropDown?): String? {
        list?.let {
            val gson = Gson()
            return gson.toJson(list)
        }
        return null
    }

    @TypeConverter
    fun fromStringToDropdownMaster(value: String?): DropdownMaster? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<DropdownMaster>() {

        }.type
        return Gson().fromJson<DropdownMaster>(value , listType)
    }

    @TypeConverter
    fun fromDropdownMasterToString(list: DropdownMaster?): String? {
        list?.let {
            val gson = Gson()
            return gson.toJson(list)
        }
        return null
    }

    @TypeConverter
    fun fromStringToAllLeadMaster(value: String?): AllLeadMaster? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<AllLeadMaster>() {

        }.type
        return Gson().fromJson<AllLeadMaster>(value , listType)
    }

    @TypeConverter
    fun fromAllLeadMasterToString(lead: AllLeadMaster?): String? {
        lead?.let {
            val gson = Gson()
            return gson.toJson(lead)
        }
        return null
    }

    @TypeConverter
    fun fromStringToCoApplicantMaster(value: String?): CoApplicantsMaster? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<CoApplicantsMaster>() {

        }.type
        return Gson().fromJson<CoApplicantsMaster>(value , listType)
    }

    @TypeConverter
    fun fromCoApplicantMasterToString(lead: CoApplicantsMaster?): String? {
        lead?.let {
            val gson = Gson()
            return gson.toJson(lead)
        }
        return null
    }


    @TypeConverter
    fun fromStringToLoanInfoObj(value: String?): LoanInfoModel? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<LoanInfoModel>() {

        }.type
        return Gson().fromJson<LoanInfoModel>(value , listType)
    }

    @TypeConverter
    fun fromLoanInfoObjToString(lead: LoanInfoModel?): String? {
        lead?.let {
            val gson = Gson()
            return gson.toJson(lead)
        }
        return null
    }

    @TypeConverter
    fun fromStringToBankDetailList(value: String?): BankDetailList? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<BankDetailList
                >() {

        }.type
        return Gson().fromJson<BankDetailList>(value , listType)
    }

    @TypeConverter
    fun fromBankDetailListToString(bankList: BankDetailList?): String? {
        bankList?.let {
            val gson = Gson()
            return gson.toJson(bankList)
        }
        return null
    }

    @TypeConverter
    fun fromReferenceMasterToString(address: Requests.AddressBean?): String? {
        address?.let {
            val gson = Gson()
            return gson.toJson(address)
        }
        return null
    }


    @TypeConverter
    fun fromStringToPersonalApplicantList(value: String?): PersonalApplicantList? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<PersonalApplicantList>() {

        }.type
        return Gson().fromJson<PersonalApplicantList>(value , listType)
    }

    @TypeConverter
    fun fromPersonalApplicantListToString(applicant: PersonalApplicantList?): String? {
        applicant?.let {
            val gson = Gson()
            return gson.toJson(applicant)
        }
        return null
    }

    @TypeConverter
    fun fromStringToAssetLiabilityList(value: String?): AssetLiabilityList? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<AssetLiabilityList>() {

        }.type
        return Gson().fromJson<AssetLiabilityList>(value , listType)
    }

    @TypeConverter
    fun fromAssetLiabilityListToString(applicant: AssetLiabilityList?): String? {
        applicant?.let {
            val gson = Gson()
            return gson.toJson(applicant)
        }
        return null
    }

    @TypeConverter
    fun fromStringToReferencesList(value: String?): ReferencesList? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<ReferencesList>() {

        }.type
        return Gson().fromJson<ReferencesList>(value , listType)
    }

    @TypeConverter
    fun fromReferencesListToString(applicant: ReferencesList?): String? {
        applicant?.let {
            val gson = Gson()
            return gson.toJson(applicant)
        }
        return null
    }

    @TypeConverter
    fun fromStringToEmploymentApplicantList(value: String?): EmploymentApplicantList? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<EmploymentApplicantList>() {

        }.type
        return Gson().fromJson<EmploymentApplicantList>(value , listType)
    }

    @TypeConverter
    fun fromEmploymentApplicantListToString(applicant: EmploymentApplicantList?): String? {
        applicant?.let {
            val gson = Gson()
            return gson.toJson(applicant)
        }
        return null
    }

    @TypeConverter
    fun fromStringToStateMaster(value: String?): StatesMaster? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<StatesMaster>() {

        }.type
        return Gson().fromJson<StatesMaster>(value , listType)
    }

    @TypeConverter
    fun fromStatesMasterToString(states: StatesMaster?): String? {
        states?.let {
            val gson = Gson()
            return gson.toJson(states)
        }
        return null
    }

    @TypeConverter
    fun fromStringToPropertyModel(value: String?): PropertyModel? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<PropertyModel>() {

        }.type
        return Gson().fromJson<PropertyModel>(value , listType)
    }

    @TypeConverter
    fun fromPropertyModelToString(states: PropertyModel?): String? {
        states?.let {
            val gson = Gson()
            return gson.toJson(states)
        }
        return null
    }

    @TypeConverter
    fun fromStringToLoanPurpose(value: String?): LoanPurpose? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<LoanPurpose>() {

        }.type
        return Gson().fromJson<LoanPurpose>(value , listType)
    }

    @TypeConverter
    fun fromLoanPurposeToString(states: LoanPurpose?): String? {
        states?.let {
            val gson = Gson()
            return gson.toJson(states)
        }
        return null
    }


    @TypeConverter
    fun fromStringToKycDocumentModel(value: String?): KycDocumentModel? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }

        val listType = object : TypeToken<KycDocumentModel>() {}.type
        return Gson().fromJson<KycDocumentModel>(value , listType)
    }

    @TypeConverter
    fun fromKycDocumentModelToString(kycDocumentModel: KycDocumentModel?): String? {
        kycDocumentModel?.let {
            val gson = Gson()
            return gson.toJson(kycDocumentModel)
        }
        return null
    }

    @TypeConverter
    fun fromStringToDocumentChecklistModel(value: String?): AllDocumentCheckListMaster? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<AllDocumentCheckListMaster>() {}.type
        return Gson().fromJson<AllDocumentCheckListMaster>(value , listType)
    }

    @TypeConverter
    fun fromDocumentChecklistModelToString(documentCheckListMaster: AllDocumentCheckListMaster): String? {
        documentCheckListMaster?.let {
            val gson = Gson()
            return gson.toJson(documentCheckListMaster)
        }
        return null
    }

    @TypeConverter
    fun fromStringToDocumentInfoObj(value: String?): DocumentDetailList? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val listType = object : TypeToken<DocumentDetailList>() {

        }.type
        return Gson().fromJson<DocumentDetailList>(value , listType)
    }

    @TypeConverter
    fun fromDocumentInfoObjToString(documentCheckList: DocumentDetailList?): String? {
        documentCheckList?.let {
            val gson = Gson()
            return gson.toJson(documentCheckList)
        }
        return null
    }

    @TypeConverter
    fun fromStringToLocationTrackerModel(value: String?): LocationTrackerModel? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }

        val listType = object : TypeToken<LocationTrackerModel>() {}.type
        return Gson().fromJson<LocationTrackerModel>(value , listType)
    }

    @TypeConverter
    fun fromLocationTrackerToString(locationTrackerModel: LocationTrackerModel?): String? {
        locationTrackerModel?.let {
            val gson = Gson()
            return gson.toJson(locationTrackerModel)
        }
        return null
    }
    @TypeConverter
    fun fromStringToFAQ(value: String?): FAQ? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }

        val listType = object : TypeToken<FAQ>() {}.type
        return Gson().fromJson<FAQ>(value , listType)
    }

    @TypeConverter
    fun fromFAQToString(faqModel: FAQ?): String? {
        faqModel?.let {
            val gson = Gson()
            return gson.toJson(faqModel)
        }
        return null
    }

}