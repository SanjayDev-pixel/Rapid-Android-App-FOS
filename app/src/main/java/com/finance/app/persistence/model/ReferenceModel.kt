package com.finance.app.persistence.model

import java.io.Serializable

class ReferenceModel : Serializable {

    var addressBean: ReferenceAddressDetail? = ReferenceAddressDetail()
    var contactNumber: String? = null
    var name: String? = null
    var knowSince: String? = null
    var address: String? = null
    var relationTypeDetailID: Int? = null
    var occupationTypeDetailID: Int? = null
    var relationTypeName: String? = null
    var occupationTypeName: String? = null

}