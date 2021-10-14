package com.finance.app.view.activity

import android.content.Context
import android.content.Intent
import android.util.Log
import com.finance.app.BuildConfig
import com.finance.app.R
import com.finance.app.databinding.ActivityLoginBinding
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.view.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.request.Requests.RequestLogin
import motobeans.architecture.retrofit.response.Response.ResponseLogin
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import retrofit2.HttpException
import retrofit2.http.HTTP
import javax.inject.Inject

class LoginActivity : BaseAppCompatActivity() {

    // used to bind element of layout to activity
    private val binding: ActivityLoginBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_login)
    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    @Inject
    lateinit var formValidation: FormValidation
    private val loginPresenter = Presenter()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        hideToolbar()
        hideSecondaryToolbar()
       // addFirebase()    // open when google json file change and open  services.MyFirebaseMessagingService in manifest
        setClickListeners()

        binding.txtVersion.setText("Version "+ BuildConfig.VERSION_NAME)
    }

    private fun setClickListeners() {
//        Call login api on login button
        binding.btnLogin.setOnClickListener {
            //if (formValidation.validateLogin(binding)) {
                //binding.btnLogin.ena
                loginPresenter.callNetwork(ConstantsApi.CALL_LOGIN, dmiConnector = LoginApiCall())
           //}
        }
        binding.tvForgotPassword.setOnClickListener {
            ForgetPasswordActivity.start(this)
        }
    }

    inner class LoginApiCall: ViewGeneric<RequestLogin, ResponseLogin>(context = this) {
        override val apiRequest: RequestLogin
            get() = mLoginRequestLogin
        private val mLoginRequestLogin: RequestLogin
            get() {
                /*binding.etUserName.setText("dmi")
                binding.etPassword.setText("Default@123")*/
//                binding.etUserName.setText("dmi")
//                binding.etPassword.setText("Default@123")
                val username = binding.etUserName.text.toString()
                val password = binding.etPassword.text.toString()
                val company = mCompany
                return RequestLogin(username = username, password = password, company = company)
            }
        private val mCompany: Requests.Company
            get() {
                return Requests.Company(1, "DMI_HFC")
            }

        override fun getApiSuccess(value: ResponseLogin) {
            if (value.responseCode == Constants.SUCCESS) {
               // System.out.println("loginValue>>>>"+value)
                sharedPreferences.saveLoginData(value)
                //SyncActivity.start(this@LoginActivity)
                if(sharedPreferences.getPasswordChangeRequired() ==true){
                    ResetPasswordActivity.start(this@LoginActivity)

                }else{
                    DashboardActivity.start(this@LoginActivity)

                }

            } else if(value.responseCode == "400"){
                showToast(value.responseMsg)
                System.out.println("ResponseMsg>>>"+value.responseMsg)
            }
        }
        override fun getApiFailure(msg: String) {
            showToast(msg)
        }
    }

    /*private fun addFirebase() {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
               //         Log.w("TAG", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                })
    }*/
}
