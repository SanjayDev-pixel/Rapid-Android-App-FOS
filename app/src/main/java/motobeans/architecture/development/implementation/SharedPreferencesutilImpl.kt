package motobeans.architecture.development.implementation

import android.Manifest
import android.Manifest.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.persistence.model.LoanInfoModel
import com.finance.app.persistence.model.UserBranches
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.sharedPreferences.SharedPreferencesBean
import motobeans.architecture.sharedPreferences.SharedPreferencesCustom
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import java.util.*
import kotlin.collections.ArrayList

class SharedPreferencesUtilImpl(private var context: Context) : SharedPreferencesUtil {

    override fun getUUID(): String {
        val uuidSharedPref = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_UUID)
        var uuid = uuidSharedPref.getString(SharedPreferencesBean.KEY_UUID)
        if (uuid.isNullOrEmpty()) {
            uuid = UUID.randomUUID().toString()
            setUUID(uuid)
        }

        return uuid
    }

    override fun getDeviceUniqueId(): String? {

            var imei = ""
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (context.checkSelfPermission(permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    if (telephonyManager != null) {
                        imei = try {
                            Settings.Secure.getString(context.contentResolver , Settings.Secure.ANDROID_ID)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Settings.Secure.getString(context.contentResolver , Settings.Secure.ANDROID_ID)
                        }
                    }
                } else {
                    ActivityCompat.requestPermissions(context as Activity , arrayOf(permission.READ_PHONE_STATE) , 1010)
                }
            } else {
                if (ActivityCompat.checkSelfPermission(context , permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    if (telephonyManager != null) {
                        imei = telephonyManager.deviceId
                    }
                } else {
                    ActivityCompat.requestPermissions(context as Activity , arrayOf(permission.READ_PHONE_STATE) , 1010)
                }
            }
            return imei

    }



    override fun getApplicationVersion(): String? {
        var version : String ? = ""
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName , 0)
             version = pInfo.versionName

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return version
    }

    override fun setUUID(uuid: String) {
        val uuidSharedPref = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_UUID)
        uuidSharedPref.putString(SharedPreferencesBean.KEY_UUID, uuid)
    }


    override fun saveCoApplicantsList(coApplicants: ArrayList<Response.CoApplicantsObj>) {
        val objCoApplicants = Gson().toJson(coApplicants)
        val objSPCoApplicants =
                SharedPreferencesCustom(context, SharedPreferencesBean.KEY_CO_APPLICANT_LIST)
        objSPCoApplicants.putString(SharedPreferencesBean.KEY_CO_APPLICANT_LIST, objCoApplicants)
    }

    override fun getCoApplicantsList(): ArrayList<Response.CoApplicantsObj>? {
        val objSpCoApplicants =
                SharedPreferencesCustom(context, SharedPreferencesBean.KEY_CO_APPLICANT_LIST)
        val coApplicantString =
                objSpCoApplicants.getString(SharedPreferencesBean.KEY_CO_APPLICANT_LIST)
        val type = object : TypeToken<ArrayList<Response.CoApplicantsObj>>() {}.type
        return Gson().fromJson(coApplicantString, type)
    }

    override fun saveLoginData(response: Response.ResponseLogin?): Boolean {
        val objLogin = Gson().toJson(response)
        val objSPLogin = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objSPLogin.putString(SharedPreferencesBean.KEY_LOGIN_DETAILS, objLogin)
        return true
    }

    override fun getLoginData(): Response.ResponseLogin? {
        val objSpLogin = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        val loginJson = objSpLogin.getString(SharedPreferencesBean.KEY_LOGIN_DETAILS)
        return Gson().fromJson(loginJson, Response.ResponseLogin::class.java)
    }

    override fun getUserToken(): String? {
        return getLoginData()?.responseObj?.token
    }

    override fun getUserName(): String? {
        return getLoginData()?.responseObj?.userDetails?.userBasicDetails?.userName
    }

    override fun getUserId(): String? {
        return getLoginData()?.responseObj?.userDetails?.userBasicDetails?.userId.toString()
    }

    override fun getEmpId(): String? {
        return getLoginData()?.responseObj?.userDetails?.userBasicDetails?.tablePrimaryID.toString()
    }

    override fun getUserBranches(): ArrayList<UserBranches>? {
        val userBranches = ArrayList<UserBranches>()
        getLoginData()?.responseObj?.userDetails?.userBranches?.let {
            userBranches.addAll(getLoginData()!!.responseObj.userDetails.userBranches)
        }
        return userBranches
    }

    override fun isLogin(): Boolean {
        return getUserToken().exIsNotEmptyOrNullOrBlank()
    }

    override fun saveLoanInfoData(request: LoanInfoModel?): Boolean {
        val objLoanInfo = Gson().toJson(request)
        val objSPLoanInfo = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOAN_INFO)
        objSPLoanInfo.putString(SharedPreferencesBean.KEY_LOAN_INFO, objLoanInfo)
        return true
    }

    override fun getLoanInfoData(): LoanInfoModel? {
        val objSpLoanInfo = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOAN_INFO)
        val loanInfo = objSpLoanInfo.getString(SharedPreferencesBean.KEY_LOAN_INFO)
        return Gson().fromJson(loanInfo, LoanInfoModel::class.java)
    }

    override fun setPropertySelection(value: String) {
        val propertySelection =
                SharedPreferencesCustom(context, SharedPreferencesBean.KEY_PROPERTY_SELECTION)
        propertySelection.putString(SharedPreferencesBean.KEY_PROPERTY_SELECTION, value)
    }

    override fun getPropertySelection(): Boolean {
        val spPropertySelection =
                SharedPreferencesCustom(context, SharedPreferencesBean.KEY_PROPERTY_SELECTION)
        val propertySelection =
                spPropertySelection.getString(SharedPreferencesBean.KEY_PROPERTY_SELECTION)
        return propertySelection == "Yes"
    }

    override fun saveLeadDetail(lead: AllLeadMaster) {
        val objLead = Gson().toJson(lead)
        val objSPLead = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LEAD_DETAIL)
        objSPLead.putString(SharedPreferencesBean.KEY_LEAD_DETAIL, objLead)
    }

    override fun getLeadDetail(): AllLeadMaster? {
        val objSpLead = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LEAD_DETAIL)
        val leadJson = objSpLead.getString(SharedPreferencesBean.KEY_LEAD_DETAIL)
        return Gson().fromJson(leadJson, AllLeadMaster::class.java)

    }

    override fun getLeadId(): String? {
        val objSpLead = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LEAD_DETAIL)
        val leadJson = objSpLead.getString(SharedPreferencesBean.KEY_LEAD_DETAIL)
        val leadDetail = Gson().fromJson(leadJson, AllLeadMaster::class.java)
        return leadDetail.leadID.toString()
    }

    override fun getLoanAppID(): Int? {
        val objSpLead = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LEAD_DETAIL)
        val leadJson = objSpLead.getString(SharedPreferencesBean.KEY_LEAD_DETAIL)
        val leadDetail = Gson().fromJson(leadJson, AllLeadMaster::class.java)
        return leadDetail.loanApplicationID
    }

    override fun getRolePrivilege(): Response.RolePrivileges? {
        val privilegesList = getLoginData()?.responseObj?.userDetails?.rolePrivilegesList
        for (privilege in privilegesList!!) {
            if (privilege.moduleName == "Login") {
                return privilege
            }
        }
        return null
    }

    override fun getNavMenuItem(): List<AppEnums.ScreenLoanApp>? {
        val navItemList = ArrayList<AppEnums.ScreenLoanApp>()
        val loginSubModules = getRolePrivilege()?.subModuleList
        for (module in loginSubModules!!) {

            val screenData = AppEnums.ScreenLoanApp.getData(module.screenDisplayName)
            if (!(navItemList.contains(screenData)) && screenData != AppEnums.ScreenLoanApp.DEFAULT) {
                navItemList.add(screenData)
            }
        }
        return navItemList
    }

    override fun clearAll() {
        try {
            for (element in SharedPreferencesBean.Array_KEY_SHARED_PREFERENCES) {
                val objSp = SharedPreferencesCustom(context, element)
                objSp.clearSharedPreferences()
            }
        } catch (e: Exception) {
        }
    }

    override fun getRoleName(): String? {
        return getLoginData()?.responseObj?.userDetails!!.roleList[0].roleName.toString()
    }

    override fun getPasswordChangeRequired(): Boolean {
        return getLoginData()?.responseObj?.userDetails?.userBasicDetails!!.passwordChangeRequired
    }

    override  fun getEmployeeName(): String?{

        return getLoginData()?.responseObj?.userDetails?.userBasicDetails?.employeeName
    }
}