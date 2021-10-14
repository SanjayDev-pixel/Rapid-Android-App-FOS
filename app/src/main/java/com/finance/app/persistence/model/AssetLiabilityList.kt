package com.finance.app.persistence.model

import java.io.Serializable

class AssetLiabilityList : Serializable {
    var loanApplicationObj: ArrayList<AssetLiabilityModel> = ArrayList()
}