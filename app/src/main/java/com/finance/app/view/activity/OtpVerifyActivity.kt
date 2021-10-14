package com.finance.app.view.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.finance.app.R
import com.finance.app.databinding.ActivityOtpVerifyBinding
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.utility.LeadMetaData
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import java.lang.Exception

class OtpVerifyActivity : BaseAppCompatActivity() {

    // used to bind element of layout to activity
    private val binding: ActivityOtpVerifyBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_otp_verify)
    private val presenter = Presenter()
    var userName : String ? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, OtpVerifyActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

    }

    override fun init() {
        try {
            userName = intent!!.extras["userName"].toString()
        }catch(e: Exception){
            e.printStackTrace()
        }
        hideToolbar()
        hideSecondaryToolbar()
        binding.btnProceed.setOnClickListener {
            val otpValue = binding.otpView.text.toString()
            if(otpValue.length<4)
            {
                Toast.makeText(this , "Enter 4 digit OTP Number" , Toast.LENGTH_SHORT).show()
            }
            else{
                binding.progressBar.visibility = View.VISIBLE
                presenter.callNetwork(ConstantsApi.CALL_VERIFY_FORGOT_OTP,dmiConnector = callGetVerifyOTP())
            }
          //  ResetPasswordActivity.start(context = getContext())

        }
    }
    inner class callGetVerifyOTP : ViewGeneric<Requests.RequestVerifyOTPforForgetPassword , Response.ResponseVerifyOTP>(context = this) {
        override val apiRequest: Requests.RequestVerifyOTPforForgetPassword?
            get() = requestVerifyOTP//To change initializer of created properties use File | Settings | File Templates.

        override fun getApiSuccess(value: Response.ResponseVerifyOTP) {
            if (value.responseCode == Constants.SUCCESS) {
                binding.progressBar.visibility = View.GONE
                showToast(value.responseMsg)
                val intent = Intent(context, SetPasswordActivity::class.java)
                intent.putExtra("userName", userName)
                startActivity(intent)
                finish()
                //SetPasswordActivity.start(context = getContext())

            } else {
                showToast(value.responseMsg)
                binding.progressBar.visibility = View.GONE

            }
        }

        /*override fun getApiValidationFail(value: Response.ResponseVerifyOTP) {
            if (value.responseCode == Constants.FAILURE) {
                binding.progressBar.visibility = View.GONE
                showToast(value.responseMsg)
            }
        }*/

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
    private val requestVerifyOTP: Requests.RequestVerifyOTPforForgetPassword
        get() {
            val company = mCompany
            val otpValue = binding.otpView.text.toString()
            var enteredOTP :String? = null
            if(otpValue.length==4)
            {
               enteredOTP = otpValue
            }
            return enteredOTP?.let {
                Requests.RequestVerifyOTPforForgetPassword(otpValue = it ,company = company
                )
            }!!
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
