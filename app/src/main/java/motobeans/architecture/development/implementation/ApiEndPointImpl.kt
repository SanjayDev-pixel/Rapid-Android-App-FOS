package motobeans.architecture.development.implementation

import motobeans.architecture.development.interfaces.EndPoint

/**
 * Created by munishkumarthakur on 04/11/17.
 */

class ApiEndPointImpl : EndPoint {

    var sUrl: String? = null

    override val url: String?
        get() = sUrl

    override val name: String?
        get() = "Project" //To change initializer of created properties use File | Settings | File Templates.

    fun setEndPoint(url: String): ApiEndPointImpl {
        this.sUrl = url
        return this
    }
}