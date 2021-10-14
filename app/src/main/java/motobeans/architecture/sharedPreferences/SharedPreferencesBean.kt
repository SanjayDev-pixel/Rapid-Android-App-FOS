package motobeans.architecture.sharedPreferences

/**
 * Created by Sanjay on 04/11/202-.
 */

object SharedPreferencesBean {
    const val KEY_UUID = "uuid"
    const val KEY_JSON = "json"
    const val KEY_LOGIN_DETAILS = "login_credentials"
    const val KEY_PERSONAL_APPLICANTS = "personal_applicants"
    const val KEY_PROPERTY_SELECTION = "property_selection"
    const val KEY_LEAD_DETAIL = "lead_detail"
    const val KEY_CO_APPLICANT_LIST = "co_applicant_list"
    const val KEY_LOAN_INFO = "loan_info"
    const val KEY_APP_VERSION = "application_version"

    val Array_KEY_SHARED_PREFERENCES = arrayOf(
            KEY_LOGIN_DETAILS, KEY_PERSONAL_APPLICANTS,
            KEY_PROPERTY_SELECTION, KEY_LEAD_DETAIL, KEY_LOAN_INFO
    )
}