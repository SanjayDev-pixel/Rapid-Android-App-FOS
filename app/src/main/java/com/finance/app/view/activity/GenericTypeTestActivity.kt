package com.finance.app.view.activity

import android.content.Context
import android.content.Intent
import com.finance.app.R
import com.finance.app.databinding.ActivityLoginBinding
import com.finance.app.presenter.connector.IBaseConnector
import com.finance.app.presenter.presenter.BasePresenter
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import javax.inject.Inject

class GenericTypeTestActivity : BaseAppCompatActivity(), IBaseConnector {

    // used to bind element of layout to activity
    private val binding: ActivityLoginBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_login)
    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    @Inject
    lateinit var formValidation: FormValidation

    private val basePresenter = BasePresenter(this)

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, GenericTypeTestActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        hideToolbar()
        hideSecondaryToolbar()
        setClickListeners()
    }

    private fun setClickListeners() {
//        Call login api on login button
        binding.btnLogin.setOnClickListener {
            basePresenter.callNetwork(ConstantsApi.CALL_LOGIN, mLoginRequestLogin, null)
        }
        binding.tvForgotPassword.setOnClickListener {
            ForgetPasswordActivity.start(this)
        }
    }

    override fun <Response> getApiSuccess(value: Response) {
        value as motobeans.architecture.retrofit.response.Response.ResponseLogin
        DashboardActivity.start(this)
        sharedPreferencesUtil.saveLoginData(value)
    }

    private val mCompany: Requests.Company
        get() {
            return Requests.Company(1, "comp1")
        }

    private val mLoginRequestLogin: Requests.RequestLogin
        get() {
            binding.etUserName.setText("kuldeep.saini@gmail.com")
            binding.etPassword.setText("Default@123")
            val username = binding.etUserName.text.toString()
            val password = binding.etPassword.text.toString()
            val company = mCompany
            return Requests.RequestLogin(username = username, password = password, company = company)
        }
}
