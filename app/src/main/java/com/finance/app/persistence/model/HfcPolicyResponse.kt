package com.finance.app.persistence.model


import java.io.Serializable

class HfcPolicyResponse :Serializable {

    var rejectionStatus : String=""
    var rejectionFlag : Boolean= false
    var deviationStatus : String=""
    var deviationFlag : Boolean= false
    var rejectionList : ArrayList<RejectionList> = ArrayList()
    var deviationList : ArrayList<DeviationList> = ArrayList()
    var foir : Float?=null
    var loanEligibleAmount : Double?=null
    var ltv : Double?=null
    var ltvMutiplier : Double?=null
    var finalTenure : Int?=null
    var finalEligibility : Double?=null
    var insrValue : Float?=null
    var proposedEMI : Double?=null
    var finalLTV : Double?=null
    var finalFoir : Double?=null
    var finalLoanOfferAmount : Double?=null
    var finalLTV_WO_Insurance : Double?=null
    var finalFoir_WO_Insurance : Double?=null
    var finalEligibleLoan_Amount_WO_Insurance : Double?=null
    var finalProposedAmount_WO_Insurance : Double?=null



}