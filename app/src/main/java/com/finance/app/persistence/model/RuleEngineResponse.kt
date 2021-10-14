package com.finance.app.persistence.model

import java.io.Serializable

class RuleEngineResponse :Serializable {
    var hfcPolicyResponse: HfcPolicyResponse? = HfcPolicyResponse()
   var action:Boolean=false

}