package com.finance.app.persistence.model

import com.finance.app.view.customViews.interfaces.IspinnerModel
import java.io.Serializable

class ChannelPartnerName : Serializable, IspinnerModel {

    var entityID: Int? = 0
    var isActive: Boolean = true
    var salesOfficerEmpID: Int? = 0
    var companyName: String? = ""
    var branchID: Int? = 0
    var channelTypeTypeDetailID: Int? = 0
    var dsaID: Int? = 0

    override fun getCompareValue(): String {
        return "$dsaID"
    }

    override fun toString(): String {
        return "$companyName"
    }
}