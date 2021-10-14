package com.finance.app.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class LocationTrackerModel {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var userId: String? = null
    var leadID: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var timestamp: String? = null
    var event: String? = null

}