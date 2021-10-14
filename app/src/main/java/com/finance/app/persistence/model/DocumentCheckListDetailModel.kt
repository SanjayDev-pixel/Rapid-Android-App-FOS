package com.finance.app.persistence.model

import androidx.room.Ignore
import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable

class DocumentCheckListDetailModel : Serializable {
    var checklistDetailId: Int? = null
    var checklistId: Int? = null
    var description: String? = null
    var typeDetailId: Int? = null
    var typeDetailDisplayText: String? = null
    @Transient
    var selectedCheckListValue: ChecklistAnswerType? = null

}