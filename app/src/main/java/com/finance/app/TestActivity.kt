package com.finance.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.finance.app.presenter.connector.TestConnector
import com.finance.app.presenter.presenter.TestPresenter
import com.finance.app.viewModel.TempViewModel
import com.finance.app.databinding.TempActivityBinding
import motobeans.architecture.appDelegates.ViewModelType.WITH_DAO
import motobeans.architecture.appDelegates.viewModelProvider
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.FormValidation

import motobeans.architecture.retrofit.request.Requests.RequestSample
import motobeans.architecture.retrofit.response.Response.ResponseSample
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import motobeans.architecture.util.delegates.BundleValueProvider
import motobeans.architecture.util.delegates.ColorResProviderDelegate
import motobeans.architecture.util.delegates.DimenResProviderDelegate
import javax.inject.Inject

/**
 * Created by motobeans on 1/3/2018.
 */
class TestActivity : BaseAppCompatActivity(), TestConnector.ViewOpt {

  companion object {
    private val KEY_TEMP_STRING_1 = "tempString1"
    fun start(context: Context) {
      val bundle = Bundle()
      bundle.putString(KEY_TEMP_STRING_1, "We found String from Bundle Delegate")

      val intent = Intent(context, TestActivity::class.java)
      intent.putExtras(bundle)
      context.startActivity(intent)
    }
  }

  @Inject
  lateinit var formValidation: FormValidation

  lateinit var presenter: TestConnector.PresenterOpt
  private val binding: TempActivityBinding by ActivityBindingProviderDelegate(this,
      R.layout.temp_activity)
  private val viewModel: TempViewModel by viewModelProvider(activity = this,
      viewModelType = WITH_DAO)

  //private val temp1 by StringResProviderDelegate(string.temp_1)
  private val dimension1 by DimenResProviderDelegate(R.dimen.activity_horizontal_margin)
  private val materialBlue by ColorResProviderDelegate(colorRes = R.color.md_blue_100)

  private val bundleString1: String? by BundleValueProvider(this,
      KEY_TEMP_STRING_1)

  override fun init() {

    ArchitectureApp.instance.component.inject(this)
    presenter = TestPresenter(viewOpt = this)
    binding.btnCreatePin.setOnClickListener {
      hitTempApi()
    }
  }

  private fun hitTempApi() {
    when (formValidation.validateTemp(binding)) {
      true -> presenter.callNetwork(ConstantsApi.CALL_TEMP)
    }
  }

  override val sampleRequest: RequestSample
    get() {
      return RequestSample()
    }

  override fun getObjectSuccess(value: ResponseSample) {
  }

  override fun getObjectFailure(msg: String) {
  }
}