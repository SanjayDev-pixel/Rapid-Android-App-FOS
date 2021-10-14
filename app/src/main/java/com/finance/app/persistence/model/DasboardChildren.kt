package com.finance.app.persistence.model

import java.io.Serializable

class DasboardChildren:Serializable {

    var heading: String?=null
    var description: String? =null
    var chartData:ArrayList<ChartData> =ArrayList()
}