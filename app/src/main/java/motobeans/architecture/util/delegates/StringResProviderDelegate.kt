package motobeans.architecture.util.delegates

import android.app.Activity
import androidx.annotation.StringRes
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by munishkumarthakur on 29/12/17.
 */
class StringResProviderDelegate(@StringRes private val stringRes: Int) : ReadOnlyProperty<Activity, String> {

    override fun getValue(thisRef: Activity, property: KProperty<*>): String {
        return thisRef.resources.getString(stringRes)
    }
}