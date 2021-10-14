package com.finance.app.persistence.model

import java.io.Serializable

class FollowUpResponse : Serializable {
    var leadFollowUpId : Int ? =null
    var leadId : Int ? = null
    var customerFollowUpStatusTypeDetailId: Int ? = null
    var leadTypeDetailId : Int ? = null
    var leadRejectionReasonTypeDetailId : Int ? = null
    var meetingDate : String ? = null
    var notificationTypeDetailId : Int ?= null
    var messageShared : String? = null

}