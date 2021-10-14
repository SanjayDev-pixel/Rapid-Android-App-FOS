package com.finance.app.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finance.app.view.customViews.interfaces.IspinnerModel
import java.io.Serializable
import java.util.*

@Entity
class LoanProductMaster : Serializable, IspinnerModel {

    @PrimaryKey
    var productID: Int = 0
    var fromDate: String? = null
    var loanPurposeList: ArrayList<LoanPurpose>? = null
    var maxAmount: Int = 0
    var maxTenure: Int = 0
    var minAmount: Int = 0
    var minTenure: Int = 0
    var productName: String? = null
    var toDate: String? = null

    override fun getCompareValue(): String {
        return "$productID"
    }

    override fun toString(): String {
        return "$productName"
    }
}