package motobeans.architecture.retrofit.response

import com.finance.app.ResponseReportData
import com.finance.app.persistence.model.*

object Response {
    data class ResponseSample(var isSuccess: Boolean, var status: Int, var message: String)
    data class ResponseAddLead(val error: String, val responseCode: String, val responseMsg: String, val responseObj: Any? = null, val timeStamp: Long)

    data class ResponseLogin(val responseCode: String, val responseMsg: String, val responseObj: LoginObj, val timeStamp: Long)
    data class LoginObj(val token: String, val userDetails: UserDetails)
    data class UserDetails(
        val roleList: ArrayList<Role>,
        val rolePrivilegesList: ArrayList<RolePrivileges>,
        val userBasicDetails: UserBasicDetails,
        val userBranches: ArrayList<UserBranches>,
        val userSpecialPermissions: ArrayList<Any>
    )

    data class Role(val isActive: Boolean, val roleId: Int, val roleName: String, val rolePrivilegesCollection: Any, val rolePrivilegesList: Any, val userRoleCollection: Any)
    data class RolePrivileges(val moduleId: Int, val moduleName: String, val subModuleList: ArrayList<SubModule>)
    data class SubModule(
        val componentPrivileges: ArrayList<Any>, val isAdd: Boolean, val isApproved: Int, val isDelete: Boolean, val isEdit: Boolean, val isExport: Boolean,
        val isPrint: Boolean, val isView: Boolean, val screenDisplayName: String, val screenId: Int, val screenName: String,
        val screenSectionId: Int, val screenSectionName: String, val sequence: Int, val subModuleId: Int, val subModuleName: String
    )

    data class UserBasicDetails(
        val tablePrimaryID: Long, val userType: String, val entityID: Int, val neverPasswordExpired: Boolean, val password: Any, val passwordChangeRequired: Boolean, val roleEdited: Boolean,
        val secuirtyAnswer1: String, val secuirtyAnswer2: String, val secuirtyQuestionId1: Int, val secuirtyQuestionId2: Int, val userId: Int, val userName: String,val employeeName:String
    )

    data class ResponseAllMasterDropdown(val responseCode: String, val responseMsg: String, val responseObj: AllMasterDropDown, val timeStamp: Long)
    data class ResponseSourceChannelPartnerName(val responseCode: String, val responseMsg: String, val responseObj: ArrayList<ChannelPartnerName>, val timeStamp: Long)
    data class ResponseLoanProduct(val responseCode: String, val responseMsg: String, val responseObj: ArrayList<LoanProductMaster>, val timeStamp: Long)
    data class LoanPurpose(val loanPurposeID: Int, val loanPurposeName: String)
    data class ResponsePinCodeDetail(val responseCode: String, val responseMsg: String, val responseObj: ArrayList<PinCodeObj>?, val timeStamp: Long)
    data class PinCodeObj(
        val cityID: Int?,
        val cityName: String?,
        val districtID: Int?,
        val districtName: String?,
        val pincode: String?,
        val pincodeID: Int?,
        val stateID: Int?,
        val stateName: String?
    )

    data class ResponseDocumentUpload(val responseCode: String, val responseMsg: String, val responseObj: DocumentUploadObj, val timeStamp: Long)
    data class DocumentUploadObj(val applicationDocumentID: Any, val documentName: String, val documentType: Any, val documentTypeDetailID: Int, val uploadedDocumentPath: String)
    data class ResponseGetAllLeads(val responseCode: String, val responseMsg: String, val responseObj: ArrayList<AllLeadMaster>, val timeStamp: Long)
    data class ResponseGetLoanApplication(val responseCode: String, val responseMsg: String, val responseObj: LoanApplicationGetObj?, val timeStamp: Long)
    data class LoanApplicationGetObj(val draftData: String?, val editable: Boolean?, val leadID: Int, val loanApplicationDraftDetailID: Int?, val storageType: String)
    data class ResponseStatesDropdown(val responseCode: String, val responseMsg: String, val responseObj: ArrayList<StatesMaster>, val timeStamp: Long)
    data class ResponseDocumentCheckLists(val responseCode: String,val responseMsg: String,val responseObj: ArrayList<AllDocumentCheckListMaster>,val timeStamp: Long)
    data class ResponseCity(val responseCode: String, val responseMsg: String, val responseObj: ArrayList<CityObj>?, val timeStamp: Long)
    data class CityObj(val cityID: Int, val cityName: String)
    data class ResponseDistrict(val responseCode: String, val responseMsg: String, val responseObj: ArrayList<DistrictObj>?, val timeStamp: Long)
    data class DistrictObj(val districtID: Int, val districtName: String)
    data class ResponsePropertyNature(val responseCode: String, val responseMsg: String, val responseObj: ArrayList<TransactionCategoryObj>, val timeStamp: Long)
    data class TransactionCategoryObj(val propertyNatureTransactionCategory: String, val propertyNatureTransactionCategoryID: Int)
    data class ResponseCoApplicants(val responseCode: String, var responseMsg: String, val responseObj: ArrayList<CoApplicantsList>?, val timeStamp: Long)
    data class CoApplicantsObj(
        var applicantID: Int? = null, var entityID: Int? = null, var firstName: String? = null,
        var incomeConsidered: Boolean? = null, var isMainApplicant: Boolean,
        var lastName: String? = null, var leadApplicantNumber: String, var middleName: String? = null
    )
    //data class ResponseVerifyOTPApplicant(val errorStack: Any?, val responseCode: String?, val responseMsg: String?, val responseObj: OtpTypeResponse?, val timeStamp: Long?)
    data class ResponseOTP(val errorStack: Any?, val responseCode: String?, val responseMsg: String?, val responseObj: OtpTypeResponse?, val timeStamp: Long?)
    data class ResponseCallUpdate(val errorStack: Any?, val responseCode: String?, val responseMsg: String?, val responseObj: Any?, val timeStamp: Long?)
    data class ResponseFollowUp(val errorStack: Any?, val responseCode: String?, val responseMsg: String?, val responseObj: ArrayList<FollowUpResponse>?, val timeStamp: Long?)
    data class ResponseLoanLeadData(val responseCode: String, val responseMsg: String, val responseObj: AllLeadMaster?, val timeStamp: Long)
    data class ResponseFinalSubmit(val responseCode: String, val responseMsg: String, val responseObj: ApplicantionSubmitModel?, val timeStamp: Long)
    data class ResponseKYC(
        val errorStack: String?, val responseCode: String, val responseMsg: String,
        val responseObj: ObjectKYC?, val timeStamp: Long
    )
    data class ObjectKYC(val kycID: String?)
    data class ResponseDocumentList(val errorStack: Any?, val responseCode: String?, val responseMsg: String?, val responseObj: DocumentTypeResponse?, val timeStamp: Long?)
    data class ResponseUploadDocument(val errorStack: Any?, val responseCode: String?, val responseMsg: String?, val responseObj: Any?, val timeStamp: Long?)
    data class ResponseUploadedDocumentList(val errorStack: Any?, val responseCode: String?, val responseMsg: String?, val responseObj: UploadedDocumentResponse?, val timeStamp: Long?)
    data class ResponseDocumentDownloadableLink(val errorStack: Any?, val responseCode: String?, val responseMsg: String?, val responseObj: DocumentPathResponse?, val timeStamp: Long?)
    data class DashboardResponse(val responseMsg: String?, val responseCode: Int?,val errorStack : String, val responseObj: ResponseObj?,val timeStamp:Double?)
    data class ResponseObj(val dashboardChildrens: ArrayList<DashboardChildrens>)
    data class DashboardChildrens(val heading: String?, val description: String?, val chartData: ArrayList<ChartData>)
    data class ChartData(val title: String, val total: Float, val data: ArrayList<Data>)
    data class Data(val label: String , val value: Float)
    data class ResponseFinalSubmitted(val responseCode: String, val responseMsg: String, val responseObj: ApplicantionSubmitModel?, val timeStamp: Long)
    data class ResponseEditLead(val error: String, val responseCode: String, val responseMsg: String, val responseObj: Any? , val timeStamp: Long)
    data class ResponseKycDetail(val responseCode: String, val responseMsg: String, val responseObj: KycListModel, val timeStamp: Long)
    data class ResponseResetPassword(val responseCode: String, val responseMsg: String, val responseObj: Any?, val timeStamp: Long)
    data class ResponseGetOTP(val responseCode: String, val responseMsg: String, val responseObj: Any?, val timeStamp: Long)
    data class ResponseVerifyOTP(val responseCode: String, val responseMsg: String, val responseObj: Any?, val timeStamp: Long)
    data class ResponseSubmitPassword(val responseCode: String, val responseMsg: String, val responseObj: Any?, val timeStamp: Long)
    data class ResponseDashboard(val responseCode: String, val responseMsg: String, val responseObj: ResponseObj?, val timeStamp: Long)
    data class ResponseUploadLocation(val errorStack: Any?, val responseCode: String?, val responseMsg: String?, val responseObj: Any?, val timeStamp: Long?)
    data class ResponseApplicantKycList(val responseCode: String, val responseMsg: String, val responseObj: PersonalApplicantList?, val timeStamp: Long)
    data class ResponseApplicationReport(val errorStack: Any? , val responseCode: String? , val responseMsg: String? , val responseObj: ArrayList<ResponseReportData> , val timeStamp: Long?)
    data class ResponseKYCAttempt(val responseCode: String, val responseMsg: String,val errorStack: Any?,val timeStamp: Long?,val responseObj:ResponseObjectKycAttempt)
    data class ResponseObjectKycAttempt(val isKycAttempt : String,val kycStatus : String,val isKycByPassAllowed : String)

    data class ResponseFAQ(val responseCode: String, val responseMsg: String, val responseObj: ArrayList<FAQ>, val timeStamp: Long)
    data class ResponseObjFAQ(val question : String, val answer : String)
    data class ResponseEmployeeByManager(val responseCode: String, val responseMsg: String, val responseObj: ArrayList<EmployeeByManager>, val timeStamp: Long)
    data class ResponseLocationHistory(val errorStack: Any?, val responseCode: String?, val responseMsg: String?, val responseObj: ArrayList<LocationHistory>, val timeStamp: Long?)
}
