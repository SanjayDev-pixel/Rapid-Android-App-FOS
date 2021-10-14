package com.finance.app.persistence.model

import java.io.Serializable

class AddressDetail : Serializable, Cloneable {
    var sameAsCurrentAddress: Boolean = false
    var address1: String? = null
    var addressProof: Int? = null
    var addressTypeDetailID: Int? = null
    var addressTypeDetail: String? = null
    var cityID: Int? = null
    var cityName: String? = null
    var districtID: Int? = null
    var districtName: String? = null
    var entityID: Int? = null
    var landmark: String? = null
    var rentAmount: String? = null
    var residenceTypeTypeDetailID: Int? = null
    var zip: String? = null
    var stateID: Int? = null
    var stateName: String? = null
    var stayingInYears: Float? = null
    var contactNum: String? = null

    public override fun clone(): Any {
        return super.clone()
    }

}