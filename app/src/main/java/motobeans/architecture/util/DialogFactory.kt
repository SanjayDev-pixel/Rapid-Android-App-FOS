package motobeans.architecture.util

import android.app.ProgressDialog
import android.content.Context

/**
 * Created by munishkumarthakur on 04/11/17.
 */
class DialogFactory {

    companion object {

        private var progressDialog: ProgressDialog? = null
        private val defaultString = "Please wait !!"

        fun getInstance(context: Context, message: String? = null): ProgressDialog {
            when(progressDialog == null){
                true -> progressDialog = createProgressDialog(context = context, message = message)
            }

            return progressDialog!!
        }

        private fun createProgressDialog(context: Context, message: String?): ProgressDialog {
            val progressDialog = ProgressDialog(context)
            when(message != null) {
                true -> progressDialog.setMessage(message)
                false -> progressDialog.setMessage(defaultString)
            }

            return progressDialog
        }
    }
}