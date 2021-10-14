package motobeans.architecture.appDelegates

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.finance.app.others.Injection
import motobeans.architecture.appDelegates.ViewModelType.WITHOUT_FACTORY
import motobeans.architecture.appDelegates.ViewModelType.WITH_DAO

/**
 * Created by munishkumarthakur on 29/12/17.
 */

enum class ViewModelType {
    WITHOUT_FACTORY, WITH_DAO
}

inline fun <reified VM : ViewModel> viewModelProvider(activity: FragmentActivity, viewModelType: ViewModelType = WITHOUT_FACTORY) = lazy {
    var viewModelFactory: ViewModelProvider.Factory? = null
    when (viewModelType) {
        WITH_DAO -> viewModelFactory = Injection.provideViewModelFactory(activity)
    }

    // Initialize Product View Model
    if (viewModelFactory != null) {
        ViewModelProviders.of(activity, viewModelFactory).get(VM::class.java)
    } else {
        ViewModelProviders.of(activity).get(VM::class.java)
    }
}

inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(activity: FragmentActivity,
                                                                       viewModelType: ViewModelType = WITHOUT_FACTORY) = lazy {
    var viewModelFactory: ViewModelProvider.Factory? = null
    when (viewModelType) {
        WITH_DAO -> viewModelFactory = Injection.provideViewModelFactory(activity)
    }

    // Initialize Product View Model
    if (viewModelFactory != null) {
        ViewModelProviders.of(activity, viewModelFactory).get(VM::class.java)
    } else {
        ViewModelProviders.of(activity).get(VM::class.java)
    }
}