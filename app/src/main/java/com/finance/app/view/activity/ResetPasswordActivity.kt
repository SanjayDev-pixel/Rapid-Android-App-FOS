package com.finance.app.view.activity

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.finance.app.R
import com.finance.app.databinding.ActivityResetPasswordBinding
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
import javax.inject.Inject

class ResetPasswordActivity : BaseAppCompatActivity() {

    // used to bind element of layout to activity
    private val binding: ActivityResetPasswordBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_reset_password)

    @Inject
    lateinit var formValidation: FormValidation
    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    private val presenter = Presenter()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ResetPasswordActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        hideToolbar()
        hideSecondaryToolbar()
        binding.btnResetPassword.setOnClickListener {
           // LoginActivity.start(context = getContext())
           if(formValidations.validateResetPassword(binding)){

               presenter.callNetwork(ConstantsApi.Call_RESET_PASSWORD, CallResetPassword())
                binding.progressBar!!.visibility = View.VISIBLE
           }else{
               Toast.makeText(this,"Please fill mandatory fields", Toast.LENGTH_SHORT).show()
           }

        }
    }


    inner class CallResetPassword : ViewGeneric<Requests.RequestResetPassword , Response.ResponseResetPassword>(context = this) {
        override val apiRequest: Requests.RequestResetPassword
            get() = resetRequest

        override fun getApiSuccess(value: Response.ResponseResetPassword) {
            if (value.responseCode == Constants.SUCCESS) {
                binding.progressBar!!.visibility = View.GONE
                showToast(value.responseMsg)
                sharedPreferencesUtil.clearAll()
                this@ResetPasswordActivity.finish()
                LoginActivity.start(this@ResetPasswordActivity)

            } else {
                showToast(value.responseMsg)
                binding.progressBar!!.visibility = View.GONE
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

    private val resetRequest: Requests.RequestResetPassword
        get() {
            val oldpassword = binding.etOldPassword.text.toString()
            val newpassword = binding.etNewPassword.text.toString()
            val confirmpassword = binding.etConfirmNewPassword.text.toString()
            val username:String = sharedPreferencesUtil.getUserName().toString()
            return Requests.RequestResetPassword(
                    confirmPassword=confirmpassword,newPassword =newpassword, oldPassword = oldpassword, changeType = "reset"
                    )
        }
}