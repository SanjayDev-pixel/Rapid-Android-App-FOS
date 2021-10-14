package com.finance.app.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class CoApplicantsMaster : Serializable {

    @PrimaryKey
    var leadID: Int? = 0
    var coApplicantsList: ArrayList<CoApplicantsList>? = ArrayList()
}