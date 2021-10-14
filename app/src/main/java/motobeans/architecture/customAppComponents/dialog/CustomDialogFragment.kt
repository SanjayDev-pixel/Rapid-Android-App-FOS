package motobeans.architecture.customAppComponents.dialog

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by munishkumarthakur on 04/11/17.
 */
abstract class CustomDialogFragment<T : ViewDataBinding> : androidx.fragment.app.DialogFragment() {

    abstract fun getView(binding: T)
    abstract fun setView(): Int
    abstract fun init()

    private var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        initBinding(inflater, container)

        return rootView()
    }

    fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {

        val binding = DataBindingUtil.inflate<T>(inflater, setView(), container, false)
        rootView = binding.root

        getView(binding)
        init()
    }

    fun rootView(): View? {
        return rootView
    }

}