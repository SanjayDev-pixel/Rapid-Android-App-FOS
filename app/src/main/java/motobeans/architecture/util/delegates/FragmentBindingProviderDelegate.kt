package motobeans.architecture.util.delegates

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by munishkumarthakur on 29/12/17.
 */
class FragmentBindingProviderDelegate<out T : ViewDataBinding>(private val inflater: LayoutInflater,
    private val container: ViewGroup,
    @LayoutRes private val layoutRes: Int) : ReadOnlyProperty<androidx.fragment.app.Fragment, T> {

    private var binding: T? = null

    override fun getValue(thisRef: androidx.fragment.app.Fragment, property: KProperty<*>): T {
        return binding ?: createBinding(thisRef).also { binding = it }
    }

    private fun createBinding(thisRef: androidx.fragment.app.Fragment): T {
        return DataBindingUtil.inflate(inflater, layoutRes, container, false)
    }
}