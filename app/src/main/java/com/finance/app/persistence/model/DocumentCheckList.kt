package com.finance.app.persistence.model

import java.io.Serializable

class DocumentCheckList : Serializable {
    @Transient
    var productID : Int ? =null
    @Transient
    var productName : String ? = null
    var checklistDetails : ArrayList<DocumentCheckListDetailModel> =  ArrayList()
    var isMainApplicant: Boolean = false
    var leadApplicantNumber: String? = null
}