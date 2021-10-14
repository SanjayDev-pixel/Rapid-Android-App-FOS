package com.finance.app.view.activity

import android.content.Context
import android.content.Intent
import com.finance.app.R
import com.finance.app.databinding.ActivitySyncBinding
import com.finance.app.viewModel.SyncDataViewModel
import motobeans.architecture.appDelegates.ViewModelType
import motobeans.architecture.appDelegates.viewModelProvider
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import javax.inject.Inject

class SyncActivity : BaseAppCompatActivity() {

    private val binding: ActivitySyncBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_sync)
    private val viewModel: SyncDataViewModel by viewModelProvider(activity = this,
            viewModelType = ViewModelType.WITH_DAO)
    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    @Inject
    lateinit var formValidation: FormValidation
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SyncActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        hideToolbar()
        hideSecondaryToolbar()
        getAllAppRelatedDataFromApi()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.btnSync.setOnClickListener {
            getAllAppRelatedDataFromApi()
        }

        binding.btnSkip.setOnClickListener {
            DashboardActivity.start(this)
        }
    }

    private fun getAllAppRelatedDataFromApi() {
        viewModel.getUpdatedDataFromServer()
    }
}
