package com.finance.app.persistence.model
import java.io.Serializable

class IncomeDetail : Serializable {

    var salariedGrossIncome: Float? = 0.0f
    var salariedDeduction: Float? = 0.0f
    var salariedNetIncome: Float? = 0.0f
    var itrCurrentYearIncome: Float? = 0.0f
    var itrLastYearIncome: Float? = 0.0f
    var itrAverageMonthlyIncome: Float? = 0.0f
    var assessedMonthlyIncome: Float? = 0.0f
}