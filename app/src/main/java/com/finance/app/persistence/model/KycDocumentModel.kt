package com.finance.app.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class KycDocumentModel : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var leadID: Int? = null
    var applicationDocumentID: String? = null
    //var formId: String? = null
    var documentID: Int? = null
    var documentName: String? = null
    var leadApplicantNumber: String? = null
    var document: String? = null
    //var Active: Int ? = null
    var moduleName : String ? = null
    var latitude : String ? = null
    var longitude : String ? = null

}