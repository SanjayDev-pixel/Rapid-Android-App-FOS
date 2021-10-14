package com.finance.app.persistence.model

import java.io.Serializable

class UploadLocationRequest : Serializable {

    var userLocationHistoryList: List<LocationTrackerModel>? = null

}