package com.finance.app.persistence.model

import java.io.Serializable

class ReferenceAddressDetail : Serializable {

    var address1: String = ""
    var address2: String = ""
    var addressProof: Int? = null
    var cityID: Int? = 0
    var cityName: String? = ""
    var districtID: Int? = 0
    var districtName: String? = ""
    var stateName: String? = ""
    var landmark: String = ""
    var rentAmount: Int? = null
    var residenceTypeTypeDetailID: Int? = null
    var zip: String = ""
}