package com.finance.app.persistence.model

import java.io.Serializable

class CardDetail : Serializable {
    var bankNameTypeDetailID: Int? = null
    var cardLimit: Int? = null
    var currentUtilization: Int? = null
    var lastPaymentDate: String? = null
    var obligateTypeDetail: Int? = null
}