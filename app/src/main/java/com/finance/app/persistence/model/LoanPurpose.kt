package com.finance.app.persistence.model

import com.finance.app.view.customViews.interfaces.IspinnerModel
import java.io.Serializable

class LoanPurpose : Serializable, IspinnerModel {

    var loanPurposeName: String? = null
    var loanPurposeID: Int = 0

    override fun getCompareValue(): String {
        return "$loanPurposeID"
    }

    override fun toString(): String {
        return "$loanPurposeName"
    }

}