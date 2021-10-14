package motobeans.architecture.development.implementation

import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.retrofit.api.Api
import retrofit2.Retrofit

/**
 * Created by munishkumarthakur on 04/11/17.
 */

class ApiProjectImpl(retrofit: Retrofit) : ApiProject {

    var apiObject: Api? = null

    init {
        apiObject = retrofit.create(Api::class.java)
    }

    override val api: Api
        get() = apiObject!!
}