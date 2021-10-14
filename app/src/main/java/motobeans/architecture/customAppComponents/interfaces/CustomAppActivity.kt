package motobeans.architecture.customAppComponents.interfaces

import android.app.Activity
import android.content.Context

/**
 * Created by munishkumarthakur on 04/11/17.
 */
interface CustomAppActivity {
    fun getActivity(): Activity
    fun getContext(): Context
}