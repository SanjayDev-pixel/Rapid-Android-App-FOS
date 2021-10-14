package com.finance.app.view.activity

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.finance.app.R
import com.finance.app.databinding.ActivitySetPasswordBinding
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import java.lang.Exception
import javax.inject.Inject

class SetPasswordActivity : BaseAppCompatActivity() {
    // used to bind element of layout to activity
    private val binding: ActivitySetPasswordBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_set_password)
    @Inject
    lateinit var formValidation: FormValidation
    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    private val presenter = Presenter()
    var userName : String ? = null
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SetPasswordActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        try {
            //userName = intent!!.extras["userName"].toString()
            //binding.etUserName.setText(userName)
        }catch(e: Exception){
            e.printStackTrace()
        }
        hideToolbar()
        hideSecondaryToolbar()

        binding.btnResetPassword.setOnClickListener(){
            if(formValidation.validatePassword(binding)){
                presenter.callNetwork(ConstantsApi.CALL_SUBMIT_PASSWORD,dmiConnector = callGetSubmitPassword())

            }
        }
    }
    inner class callGetSubmitPassword : ViewGeneric<Requests.RequestSubmitPassword , Response.ResponseSubmitPassword>(context = this) {
        override val apiRequest: Requests.RequestSubmitPassword?
            get() = requestPassword//To change initializer of created properties use File | Settings | File Templates.

        override fun getApiSuccess(value: Response.ResponseSubmitPassword) {
            if (value.responseCode == Constants.SUCCESS) {
                binding.progressBar.visibility = View.GONE
                showToast(value.responseMsg)
                LoginActivity.start(context = getContext())
            } else {
                showToast(value.responseMsg)
                binding.progressBar.visibility = View.GONE
            }
        }

        override fun getApiFailure(msg: String) {
            if (msg.exIsNotEmptyOrNullOrBlank()) {
                super.getApiFailure(msg)
                binding.progressBar!!.visibility = View.GONE
            } else {
                super.getApiFailure("Time out Error")
                binding.progressBar!!.visibility = View.GONE
            }


        }
    }
    private val requestPassword: Requests.RequestSubmitPassword
        get() {
            val company = mCompany
            return Requests.RequestSubmitPassword(otpValue = binding.otpView.text.toString(),newPassword =binding.etNewPassword.text.toString(),confirmPassword = binding.etConfirmNewPassword.text.toString(), company = company
            )

        }
    private val mCompany: Requests.Company
        get() {
            return Requests.Company(1, "DMI_HFC")
        }

    override fun onBackPressed() = showDialog()

    private fun showDialog() {
        runOnUiThread {
            if (!isFinishing) {
                AlertDialog.Builder(this)
                        .setTitle(getString(R.string.warning_msg))
                        .setMessage("Do you want to cancel ?")
                        .setCancelable(false)
                        .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                        .setPositiveButton("Ok") { _, _ ->
                            this.finish()
                            super.onBackPressed()
                        }.show()
            }
        }
    }

}