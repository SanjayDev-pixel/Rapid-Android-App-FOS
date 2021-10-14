package motobeans.architecture.util.delegates

import android.app.Activity
import androidx.annotation.DimenRes
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by munishkumarthakur on 29/12/17.
 */
class DimenResProviderDelegate(@DimenRes private val dimensionRes: Int) : ReadOnlyProperty<Activity, Float> {

    override fun getValue(thisRef: Activity, property: KProperty<*>): Float {
        return thisRef.resources.getDimension(dimensionRes)
    }
}