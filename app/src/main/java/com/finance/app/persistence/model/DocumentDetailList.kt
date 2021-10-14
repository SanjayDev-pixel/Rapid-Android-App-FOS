package com.finance.app.persistence.model

import java.io.Serializable

class DocumentDetailList : Serializable {
    var leadID : Int? = null
    var documentDetailList : ArrayList<DocumentCheckList> = ArrayList()
}