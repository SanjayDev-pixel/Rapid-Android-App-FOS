package com.finance.app.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class FAQ : Serializable {

    @PrimaryKey(autoGenerate = true)
    var ID: Int = 0
    //var Active:Int=0https://rapidservices-dev.dmihousingfinance.in/dmi/api/v1/master/auth/faq
    var question:String?=null
    var answer:String?=null
    override fun toString(): String {
        return answer!!
    }

}