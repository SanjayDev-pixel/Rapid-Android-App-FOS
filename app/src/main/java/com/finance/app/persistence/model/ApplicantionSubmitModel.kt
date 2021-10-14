package com.finance.app.persistence.model

import java.io.Serializable
import java.math.BigDecimal

class ApplicantionSubmitModel:Serializable {

    var ruleEngineResponseID : String? = null
    var acticoStatusTypeDetailID : Int?=null
    var deviceType : String?=""
    var eligibleLoanAmount : Double?=null
    var eligibleLoanAmountWithoutINSR : Double?=null
    var finalFOIR : Double?=null
    var finalLTV : Double?=null
    var foirNorm : Double?=null
    var foirNormValue : Double?=null
    var foirWithoutINSR : Double?=null
    var insr : Double?=null
    var loanOfferAmount : Double?=null
    var ltvNorm : Double?=null
    var ltvNormValue : Double?=null
    var ltvWithoutINSR : Double?=null
    var outputType : String?=""
    var proposedEmi : Double?=null
    var proposedEmiWithoutINSR : Double?=null
    var proposedTenor : Int?=null
    var ruleEngineResponse : String?=""
    var sequence : Int?= null
    var loanApplicationID : Int?=null
    var requestedLoanAmount : Double?=null
    var requestedTenure : Double? =null
    var roi : Double? =null




}