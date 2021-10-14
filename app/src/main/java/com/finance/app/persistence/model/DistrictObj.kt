package com.finance.app.persistence.model

import com.finance.app.view.customViews.interfaces.IspinnerModel
import java.io.Serializable

class DistrictObj : Serializable, IspinnerModel {
    var districtName: String? = ""
    var districtID: Int? = 0

    override fun getCompareValue(): String {
        return "$districtID"
    }

    override fun toString(): String {
        return "$districtName"
    }
}