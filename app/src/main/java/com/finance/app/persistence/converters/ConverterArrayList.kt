package com.finance.app.persistence.converters

import androidx.room.TypeConverter
import com.finance.app.persistence.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank

class ConverterArrayList {

    @TypeConverter
    fun fromStringToStringArrayList(value: String): List<String>? {
        val arrayListType = object : TypeToken<List<String>>() {

        }.type
        return Gson().fromJson<List<String>>(value, arrayListType)
    }

    @TypeConverter
    fun fromStringArrayListToString(arrayList: List<String>): String {
        val gson = Gson()
        return gson.toJson(arrayList)
    }

    @TypeConverter
    fun fromStringToArrayListAllSpinnerMaster(value: String?): ArrayList<AllMasterDropDown>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<AllMasterDropDown>>() {

        }.type
        return Gson().fromJson<ArrayList<AllMasterDropDown>>(value, arrayListType)
    }

    @TypeConverter
    fun fromAllSpinnerMasterArrayListToString(arrayList: ArrayList<AllMasterDropDown>?): String? {
        arrayList?.let {
            val gson = Gson()
            return gson.toJson(arrayList)
        }
        return null
    }

    @TypeConverter
    fun fromStringToArrayListDropdownMaster(value: String?): ArrayList<DropdownMaster>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<DropdownMaster>>() {

        }.type
        return Gson().fromJson<ArrayList<DropdownMaster>>(value, arrayListType)
    }

    @TypeConverter
    fun fromDropdownMasterArrayListToString(ArrayList: ArrayList<DropdownMaster>?): String? {
        ArrayList?.let {
            val gson = Gson()
            return gson.toJson(ArrayList)
        }
        return null
    }

    @TypeConverter
    fun fromStringToArrayListAllLeadMaster(value: String?): ArrayList<AllLeadMaster>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<AllLeadMaster>>() {

        }.type
        return Gson().fromJson<ArrayList<AllLeadMaster>>(value, arrayListType)
    }

    @TypeConverter
    fun fromAllLeadArrayListToString(ArrayList: ArrayList<AllLeadMaster>?): String? {
        ArrayList?.let {
            val gson = Gson()
            return gson.toJson(ArrayList)
        }
        return null
    }

    @TypeConverter
    fun fromStringToArrayListCoApplicantMaster(value: String?): ArrayList<CoApplicantsMaster>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<CoApplicantsMaster>>() {

        }.type
        return Gson().fromJson<ArrayList<CoApplicantsMaster>>(value, arrayListType)
    }

    @TypeConverter
    fun fromStringToArrayListAllDocumentCheckListMaster(value: String?): ArrayList<AllDocumentCheckListMaster>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayLisyType = object : TypeToken<ArrayList<AllDocumentCheckListMaster>>() {

        }.type
        return Gson().fromJson<ArrayList<AllDocumentCheckListMaster>>(value, arrayLisyType)

    }

    @TypeConverter
    fun fromCoApplicantsMasterArrayListToString(ArrayList: ArrayList<CoApplicantsMaster>?): String? {
        ArrayList?.let {
            val gson = Gson()
            return gson.toJson(ArrayList)
        }
        return null
    }

    @TypeConverter
    fun fromDocumentListMasterArrayListToString(ArrayList: ArrayList<AllDocumentCheckListMaster>?): String? {
        ArrayList?.let {
            val gson = Gson()
            return gson.toJson(ArrayList)
        }
        return null
    }


    @TypeConverter
    fun fromStringToArrayListAssetDetail(value: String?): ArrayList<AssetLiability>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<AssetLiability>>() {

        }.type
        return Gson().fromJson<ArrayList<AssetLiability>>(value, arrayListType)
    }

    @TypeConverter
    fun fromAssetDetailArrayListToString(array: ArrayList<AssetLiability>?): String? {
        array?.let {
            val gson = Gson()
            return gson.toJson(array)
        }
        return null
    }

    @TypeConverter
    fun fromStringToArrayListCardDetail(value: String?): ArrayList<CardDetail>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<CardDetail>>() {

        }.type
        return Gson().fromJson<ArrayList<CardDetail>>(value, arrayListType)
    }

    @TypeConverter
    fun fromCardDetailArrayListToString(ArrayList: ArrayList<CardDetail>?): String? {
        ArrayList?.let {
            val gson = Gson()
            return gson.toJson(ArrayList)
        }
        return null
    }

    @TypeConverter
    fun fromStringToArrayListObligationDetail(value: String?): ArrayList<ObligationDetail>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<ObligationDetail>>() {

        }.type
        return Gson().fromJson<ArrayList<ObligationDetail>>(value, arrayListType)
    }

    @TypeConverter
    fun fromObligationDetailArrayListToString(ArrayList: ArrayList<ObligationDetail>?): String? {
        ArrayList?.let {
            val gson = Gson()
            return gson.toJson(ArrayList)
        }
        return null
    }

    @TypeConverter
    fun fromStringToArrayListPersonalApplicantModel(value: String?): ArrayList<PersonalApplicantsModel>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<PersonalApplicantsModel>>() {

        }.type
        return Gson().fromJson<ArrayList<PersonalApplicantsModel>>(value, arrayListType)
    }

    @TypeConverter
    fun fromPersonalApplicantModelArrayListToString(ArrayList: ArrayList<PersonalApplicantsModel>?): String? {
        ArrayList?.let {
            val gson = Gson()
            return gson.toJson(ArrayList)
        }
        return null
    }

    @TypeConverter
    fun fromStringToArrayListEmploymentApplicantModel(value: String?): ArrayList<EmploymentApplicantsModel>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<EmploymentApplicantsModel>>() {

        }.type
        return Gson().fromJson<ArrayList<EmploymentApplicantsModel>>(value, arrayListType)
    }

    @TypeConverter
    fun fromEmploymentApplicantModelArrayListToString(ArrayList: ArrayList<EmploymentApplicantsModel>?): String? {
        ArrayList?.let {
            val gson = Gson()
            return gson.toJson(ArrayList)
        }
        return null
    }

    @TypeConverter
    fun fromStringToArrayListLoanPurpose(value: String?): ArrayList<LoanPurpose>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<LoanPurpose>>() {

        }.type
        return Gson().fromJson<ArrayList<LoanPurpose>>(value, arrayListType)
    }

    @TypeConverter
    fun fromLoanPurposeArrayListToString(ArrayList: ArrayList<LoanPurpose>?): String? {
        ArrayList?.let {
            val gson = Gson()
            return gson.toJson(ArrayList)
        }
        return null
    }

    @TypeConverter
    fun fromStringToArrayListStateMaster(value: String?): ArrayList<StatesMaster>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<StatesMaster>>() {

        }.type
        return Gson().fromJson<ArrayList<StatesMaster>>(value, arrayListType)
    }

    @TypeConverter
    fun fromStateMasterArrayListToString(ArrayList: ArrayList<StatesMaster>?): String? {
        ArrayList?.let {
            val gson = Gson()
            return gson.toJson(ArrayList)
        }
        return null
    }

    @TypeConverter
    fun fromStringToArrayListBankDetailModel(value: String?): ArrayList<BankDetailModel>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<BankDetailModel>>() {

        }.type
        return Gson().fromJson<ArrayList<BankDetailModel>>(value, arrayListType)
    }

    @TypeConverter
    fun fromBankDetailModelArrayListToString(ArrayList: ArrayList<BankDetailModel>?): String? {
        ArrayList?.let {
            val gson = Gson()
            return gson.toJson(ArrayList)
        }
        return null
    }

    @TypeConverter
    fun fromAssetLiabilityModelArrayListToString(arrayModel: ArrayList<AssetLiabilityModel>?): String? {
        arrayModel?.let {
            val gson = Gson()
            return gson.toJson(arrayModel)
        }
        return null
    }

    @TypeConverter
    fun fromStringToArrayListAssetLiabilityModel(value: String?): ArrayList<AssetLiabilityModel>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<AssetLiabilityModel>>() {

        }.type
        return Gson().fromJson<ArrayList<AssetLiabilityModel>>(value, arrayListType)
    }

    @TypeConverter
    fun fromCoApplicantListArrayListToString(arrayModel: ArrayList<CoApplicantsList>?): String? {
        arrayModel?.let {
            val gson = Gson()
            return gson.toJson(arrayModel)
        }
        return null
    }

    @TypeConverter
    fun fromStringToArrayListCoApplicantsList(value: String?): ArrayList<CoApplicantsList>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<CoApplicantsList>>() {

        }.type
        return Gson().fromJson<ArrayList<CoApplicantsList>>(value, arrayListType)
    }

    @TypeConverter
    fun fromStringToArrayListReferenceModel(value: String?): ArrayList<ReferenceModel>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<ReferenceModel>>() {

        }.type
        return Gson().fromJson<ArrayList<ReferenceModel>>(value, arrayListType)
    }

    @TypeConverter
    fun fromReferenceModelArrayListToString(ArrayList: ArrayList<ReferenceModel>?): String? {
        ArrayList?.let {
            val gson = Gson()
            return gson.toJson(ArrayList)
        }
        return null
    }
    //Add Document Checklist Details
    @TypeConverter
    fun fromDocumentCheckListToString(arrayModel: ArrayList<DocumentCheckListDetailModel>?): String? {
        arrayModel?.let {
            val gson = Gson()
            return gson.toJson(arrayModel)
        }
        return null
    }

    @TypeConverter
    fun fromStringToArrayListDocumentCheckList(value: String?): ArrayList<DocumentCheckListDetailModel>? {
        if (!value.exIsNotEmptyOrNullOrBlank()) {
            return null
        }
        val arrayListType = object : TypeToken<ArrayList<DocumentCheckListDetailModel>>() {

        }.type
        return Gson().fromJson<ArrayList<DocumentCheckListDetailModel>>(value, arrayListType)
    }

}
