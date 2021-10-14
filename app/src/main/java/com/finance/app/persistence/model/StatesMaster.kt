package com.finance.app.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finance.app.view.customViews.interfaces.IspinnerModel
import java.io.Serializable

@Entity
class StatesMaster : Serializable, IspinnerModel {

    @PrimaryKey
    var stateID: Int? = 0
    var stateName: String = ""

    override fun getCompareValue(): String {
        return "$stateID"
    }

    override fun toString(): String {
        return stateName
    }
}