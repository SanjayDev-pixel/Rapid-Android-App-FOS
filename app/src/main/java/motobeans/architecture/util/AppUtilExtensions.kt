package motobeans.architecture.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

class AppUtilExtensions {
    companion object {
        fun <T : ViewDataBinding> initCustomViewBinding(context: Context, container: ViewGroup?, layoutId: Int): T {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            return DataBindingUtil.inflate(inflater, layoutId, container, true)
        }

        fun <T : ViewDataBinding> initBinding(inflater: LayoutInflater, container: ViewGroup?,
                                              layoutId: Int): T {
            return DataBindingUtil.inflate(inflater, layoutId, container, true)
        }
    }
}