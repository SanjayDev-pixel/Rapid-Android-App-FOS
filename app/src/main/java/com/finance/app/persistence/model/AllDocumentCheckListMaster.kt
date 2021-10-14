package com.finance.app.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class AllDocumentCheckListMaster : Serializable {
    @PrimaryKey
    var productID : Int? = null
    var productName : String ? = null
    var isMainApplicant: Boolean = false
    var leadApplicantNumber: String? = ""
    var checklistDetails:ArrayList<DocumentCheckListDetailModel> = ArrayList()

}