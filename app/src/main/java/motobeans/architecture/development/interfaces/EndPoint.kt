package motobeans.architecture.development.interfaces

/**
 * Created by munishkumarthakur on 04/11/17.
 */

interface EndPoint {

    /** The base API URL. */
    val url: String?

    /** A name for differentiating multiple API URLs */
    val name: String?
}