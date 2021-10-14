package com.finance.app.persistence.model

import com.finance.app.view.customViews.interfaces.IspinnerModel
import java.io.Serializable

class DropdownMaster : Serializable, IspinnerModel {
    var typeDetailCode: String? = ""
    var typeMasterID: Int? = 0
    var typeDetailID: Int = 0
    var refTypeDetailID: Int? = 0
    var typeMasterName: String? = ""
    var typeMasterDisplayText: String? = ""
    var typeMasterLogicalCode: String? = ""
    var typeDetailLogicalCode: String? = ""
    var sequence: String? = ""
    var typeDetailDescription: String? = ""
    var typeDetailDisplayText: String? = ""

    override fun getCompareValue(): String {
        return "$typeDetailID"
    }

    override fun toString(): String {
//        return "$typeDetailCode"
        return "$typeDetailDisplayText"
    }
}