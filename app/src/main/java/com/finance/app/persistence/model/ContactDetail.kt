package com.finance.app.persistence.model

import java.io.Serializable

class ContactDetail : Serializable {
    var contactID: Int? = null
    var contactTypeDetailID: Int? = null
    var email: String? = ""
    var enitiyID: Int? = null
    var fax: String? = ""
    var mobile: String? = ""
    var isEmailVerified: Boolean? = false
    var isMobileVerified: Boolean? = false
}
