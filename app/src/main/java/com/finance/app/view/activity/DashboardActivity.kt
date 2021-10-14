package com.finance.app.view.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.assent.Assent
import com.afollestad.assent.AssentCallback
import com.finance.app.R
import com.finance.app.databinding.ActivityDashboardNewBinding
import com.finance.app.eventBusModel.AppEvents
import com.finance.app.locationTracker.ForegroundLocationTrackerService
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.view.adapters.recycler.adapter.DashboardChartAdapter
import com.finance.app.viewModel.SyncDataViewModel
import com.finance.app.workers.UtilWorkManager
import com.google.firebase.analytics.FirebaseAnalytics
import motobeans.architecture.appDelegates.ViewModelType
import motobeans.architecture.appDelegates.viewModelProvider
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


class DashboardActivity : BaseAppCompatActivity() {

    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil

    private var adapterChart: DashboardChartAdapter? = null
    private val presenter = Presenter()

    private val binding: ActivityDashboardNewBinding by ActivityBindingProviderDelegate(this , R.layout.activity_dashboard_new)
    private val viewModel: SyncDataViewModel by viewModelProvider(
            activity = this ,
            viewModelType = ViewModelType.WITH_DAO
    )
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        fun start(context: Context) {
            val intent = Intent(context , DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        UtilWorkManager.globalWorkManagerPeriodically()
        viewModel.getUpdatedDataFromServer()

        hideSecondaryToolbar()
        setVersionName()

        initChartData()

        Assent.requestPermissions(AssentCallback { result ->
            //start location tracker service
            if (result.allPermissionsGranted())
                startLocationTrackerService()
        } , 1 , Assent.ACCESS_COARSE_LOCATION , Assent.ACCESS_FINE_LOCATION)

    }

    private fun startLocationTrackerService() {
        val intent = Intent(applicationContext , ForegroundLocationTrackerService::class.java)
        intent.action = "DashBoardActivity"
        intent.putExtra("LEADID",0)
        ContextCompat.startForegroundService(applicationContext , intent)
    }

    private fun setVersionName() {
        try {
            val pInfo = this.packageManager.getPackageInfo(this.packageName , 0)
            val version = pInfo.versionName
            binding.tvVersionName.text = version
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    fun initChartData() {

        presenter.callNetwork(ConstantsApi.CALL_DASBOARD , CallDasboardData())

        /* val dashboardResponse = Gson().fromJson(Constants.TEMP_DATA.apiChartResult, Response.DashboardResponse::class.java)
         initChartAdapter(dashboardResponse = dashboardResponse)*/
    }

    private fun initChartAdapter(dashboardResponse: Response.ResponseDashboard) {
        adapterChart = DashboardChartAdapter(mActivity = this , dashboardChartData = dashboardResponse)
        binding.rvDashboardCharts.adapter = adapterChart
        binding.rvDashboardCharts.addItemDecoration(DividerItemDecoration(this , LinearLayoutManager.VERTICAL))

    }

    public override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun getEventBackgroundSync(syncEnum: AppEvents.BackGroundSyncEvent) {
        AppEvents().initiateBackgroundSync(syncEnum = syncEnum)
    }

    inner class CallDasboardData : ViewGeneric<Requests.RequestDashBoard , Response.ResponseDashboard>(context = this) {
        override val apiRequest: Requests.RequestDashBoard
            get() = dasboardRequest

        override fun getApiSuccess(value: Response.ResponseDashboard) {
            if (value.responseCode == Constants.SUCCESS) {
                // binding.progressBar!!.visibility =View.GONE
                initChartAdapter(dashboardResponse = value)

            } else {
                showToast(value.responseMsg)
                // binding.progressBar!!.visibility =View.GONE
            }
        }

        override fun getApiFailure(msg: String) {

            if (msg.exIsNotEmptyOrNullOrBlank()) {
                super.getApiFailure(msg)
                // binding.progressBar!!.visibility = View.GONE
            } else {
                super.getApiFailure("Time out Error")
                // binding.progressBar!!.visibility = View.GONE
            }

        }

    }

    private val dasboardRequest: Requests.RequestDashBoard
        get() {
            val userName = ""
            return Requests.RequestDashBoard(userName = userName)
        }
}
