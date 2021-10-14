package com.finance.app.utility

import com.finance.app.others.AppEnums
import com.finance.app.others.AppEnums.FormType.*
import com.finance.app.persistence.model.*
import com.google.gson.Gson
import motobeans.architecture.retrofit.response.Response
import java.io.Serializable

class LeadRequestResponseConversion {

    private val gson = Gson()

    fun getResponseObject(form: AppEnums.FormType, response: Response.LoanApplicationGetObj?): Serializable? {
        response?.draftData?.let {
            return when (form) {
                LOANINFO -> gson.fromJson(response.draftData, LoanInfoModel::class.java)
                PERSONALINFO -> gson.fromJson(response.draftData, PersonalApplicantList::class.java)
                EMPLOYMENT -> gson.fromJson(response.draftData, EmploymentApplicantList::class.java)
                BANKDETAIL -> gson.fromJson(response.draftData, BankDetailList::class.java)
                LIABILITYASSET -> gson.fromJson(response.draftData, AssetLiabilityList::class.java)
                PROPERTY -> gson.fromJson(response.draftData, PropertyModel::class.java)
                REFERENCE -> gson.fromJson(response.draftData, ReferencesList::class.java)
                DOCUMENT ->gson.fromJson(response.draftData,DocumentDetailList::class.java)

            }
        }
        return null
    }

    fun getRequest(form: AppEnums.FormType, response: AllLeadMaster?): LoanApplicationRequest? {

        val objectGeneric = LoanApplicationRequest()

        response?.leadID?.let {
            objectGeneric.leadID = response.leadID!!
            objectGeneric.storageType = form.type

            response.let {
                objectGeneric.draftData = when (form) {
                    LOANINFO -> gson.toJson(response.loanData)
                    PERSONALINFO -> gson.toJson(response.personalData)
                    EMPLOYMENT -> gson.toJson(response.employmentData)
                    BANKDETAIL -> gson.toJson(response.bankData)
                    LIABILITYASSET -> gson.toJson(response.assetLiabilityData)
                    PROPERTY -> gson.toJson(response.propertyData)
                    REFERENCE -> gson.toJson(response.referenceData)
                    DOCUMENT ->gson.toJson(response.documentData)

                }
            }
        }

        return objectGeneric
    }
}