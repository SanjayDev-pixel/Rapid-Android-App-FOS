package com.finance.app.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class EmployeeTracking :Serializable{

    @PrimaryKey(autoGenerate = true)
    var id: Int=0
    var latitude:String?=null
    var longitude:String?=null
    var createdTime:String?=null
    var employeeId:Int=0
}