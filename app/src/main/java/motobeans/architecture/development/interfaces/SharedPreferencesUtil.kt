package motobeans.architecture.development.interfaces

import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.persistence.model.LoanInfoModel
import com.finance.app.persistence.model.UserBranches
import motobeans.architecture.retrofit.response.Response

interface SharedPreferencesUtil {
    fun saveLoginData(response: Response.ResponseLogin?): Boolean
    fun saveLoanInfoData(request: LoanInfoModel?): Boolean
    fun getLoginData(): Response.ResponseLogin?
    fun getLoanInfoData(): LoanInfoModel?
    fun isLogin(): Boolean
    fun getUserToken(): String?
    fun getUserName(): String?
    fun setPropertySelection(value: String)
    fun saveLeadDetail(lead: AllLeadMaster)
    fun saveCoApplicantsList(coApplicants: ArrayList<Response.CoApplicantsObj>)
    fun getCoApplicantsList(): ArrayList<Response.CoApplicantsObj>?
    fun getLeadId(): String?
    fun getLeadDetail(): AllLeadMaster?
    fun getUserId(): String?
    fun getEmpId(): String?
    fun getLoanAppID(): Int?
    fun getPropertySelection(): Boolean
    fun getUserBranches(): ArrayList<UserBranches>?
    fun getRolePrivilege(): Response.RolePrivileges?
    fun getNavMenuItem(): List<AppEnums.ScreenLoanApp>?
    fun getUUID(): String
    fun setUUID(uuid: String)
    fun clearAll()
    fun getRoleName():String?
    fun getPasswordChangeRequired():Boolean
    fun getEmployeeName():String?
    fun getApplicationVersion(): String ?
    fun getDeviceUniqueId() : String ?

}