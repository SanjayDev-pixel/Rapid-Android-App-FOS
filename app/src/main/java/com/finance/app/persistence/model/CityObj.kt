package com.finance.app.persistence.model

import com.finance.app.view.customViews.interfaces.IspinnerModel
import java.io.Serializable

class CityObj : Serializable, IspinnerModel {
    var cityName: String? = ""
    var cityID: Int? = 0

    override fun getCompareValue(): String {
        return "$cityID"
    }

    override fun toString(): String {
        return "$cityName"
    }
}