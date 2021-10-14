package motobeans.architecture.customAppComponents.activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.finance.app.BuildConfig
import com.finance.app.R
import com.finance.app.Report
import com.finance.app.databinding.ActivityBaseBinding
import com.finance.app.persistence.dao.AllMasterDropDownDao
import com.finance.app.persistence.db.MasterDB
import com.finance.app.presenter.connector.ReusableView
import com.finance.app.view.activity.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.view.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.customAppComponents.fragment.CommonDialogFragment
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.util.DialogFactory
import motobeans.architecture.util.exShowToast
import javax.inject.Inject

abstract class BaseAppCompatActivity : BaseAppActivityImpl(), ReusableView {

  abstract fun init()
  private lateinit var toggle: ActionBarDrawerToggle
  private var isBackPressDialogToShow = false
  private var view: View? = null
  private lateinit var bindingParent: ActivityBaseBinding
  @Inject
  lateinit var sharedPreferencesUtil: SharedPreferencesUtil
  @Inject
  lateinit var mDataBase: DataBaseUtil
  @Inject
  lateinit var formValidations: FormValidation

  companion object {
    internal var progressDialog: ProgressDialog? = null
  }

  private fun setContentBindingTemp() {
    bindingParent = DataBindingUtil.setContentView(getActivity(), R.layout.activity_base)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ArchitectureApp.instance.component.inject(this)
    setContentBindingTemp()
    setToolbar()
    showToolbar()
    setUpHeaderView()
    setOnClickListener()
    initializeViewBindingTemp()
    System.out.println("backHandlePressed>>>>"+Constants.APP.backPressed)
  }

  private fun initializeViewBindingTemp() {
    view = bindingParent.root
    initializeOtherViews()
    init()
  }

  private fun initializeOtherViews() {
    toggle = ActionBarDrawerToggle(this, bindingParent.drawerLayout,
            bindingParent.appBarWithLayout.toolbarMain, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close)
    bindingParent.drawerLayout.addDrawerListener(toggle)
    setupDrawerContent(bindingParent.navView)
  }

  private fun setupDrawerContent(navView: NavigationView) {
    navView.setNavigationItemSelectedListener {
      selectDrawerItem(it)
      return@setNavigationItemSelectedListener true
    }
  }

  private fun selectDrawerItem(menuItem: MenuItem) {
    when (menuItem.itemId) {
      R.id.notification -> {
        NotificationActivity.start(this)
      }
      R.id.dashboard -> {
        DashboardActivity.start(this)
      }
      R.id.createLead -> {
        //CreateLeadActivity.start(this)
        val intent = Intent(this, CreateLeadActivity::class.java)
        this.startActivity(intent)
      }
        R.id.Reports ->{
            val intent= Intent(this,  Report::class.java)
            this.startActivity(intent)
        }
      R.id.EmployeeTracking ->{
        val intent= Intent(this,EmployeeTrackingActivity::class.java)
        this.startActivity(intent)
      }
      R.id.logout -> {
        sharedPreferencesUtil.clearAll()

        GlobalScope.launch {
          mDataBase.provideDataBaseSource().kycDocumentDao().truncate()
           mDataBase.provideDataBaseSource().allMasterDropDownDao().deleteAllMasterDropdownValue()
         }
        this.cacheDir.deleteRecursively()
        this.finish()
        LoginActivity.start(this)
      }
      R.id.assignedLeads -> {
        AllLeadActivity.start(this)
      }
    }

    bindingParent.drawerLayout.closeDrawer(GravityCompat.START)
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    toggle.syncState()
  }

  override fun onConfigurationChanged(newConfig: Configuration?) {
    super.onConfigurationChanged(newConfig)
    toggle.onConfigurationChanged(newConfig)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lead_action, menu)
        return true
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
        R.id.send_notification -> showToast("Notification")
        R.id.send_sms -> showToast("SMS")
        //R.id.search -> showToast("Searching")
    }
    return super.onOptionsItemSelected(item)
  }

  override fun showToast(msg: String) {
    msg.exShowToast(getContext())
  }

  override fun showProgressDialog() {
    when (progressDialog == null) {
      true -> progressDialog = DialogFactory.getInstance(context = getContext())
    }
    progressDialog?.show()
  }

  override fun hideProgressDialog() {
    progressDialog?.hide()
  }

  override fun onBackPressed() {
    when(isBackPressDialogToShow){
      true -> {
        val msg = "You really want to exit?"
        val fragment = CommonDialogFragment.newInstance(msg)
        fragment.show(supportFragmentManager, "")
        fragment.setListener({ fragment.dismiss() }, { super.onBackPressed() })
      }
      false -> {
        super.onBackPressed()
      }
    }
  }

  private fun setToolbar() {
    setSupportActionBar(bindingParent.appBarWithLayout.toolbarMain)

    supportActionBar?.setLogo(R.drawable.dmi_logo_white)
    bindingParent.navView.inflateMenu(R.menu.menu_lead_action)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setHomeButtonEnabled(true)

    bindingParent.appBarWithLayout.tvBackSecondary.setOnClickListener {
      onBackPressed()
    }

    bindingParent.navView.versionNumber.setText("Version "+BuildConfig.VERSION_NAME)
  }

  fun setLeadNum(leadNum: String?) {
    leadNum?.let {
      bindingParent.appBarWithLayout.tvLeadNumber.text = leadNum
    }
  }

  private fun setUpHeaderView() {
      bindingParent.navView.inflateHeaderView(R.layout.drawer_header)
      val headerLayout = bindingParent.navView.getHeaderView(0)
    headerLayout.tvProfileName.setText(sharedPreferencesUtil.getEmployeeName())
    headerLayout.setOnClickListener {
          //ProfileActivity.start(this)
      }
  }

  private fun showToolbar() {
    supportActionBar?.show()
  }

  fun hideToolbar() {
    supportActionBar?.hide()
  }

  fun showLeadOptionsMenu() {
    val menuOption = bindingParent.appBarWithLayout.toolbarMain.menu
    menuOption.findItem(R.id.group_lead_action).isVisible = true
  }

  fun hideSecondaryToolbar() {
    bindingParent.appBarWithLayout.secondaryToolbar.visibility = View.GONE
  }

  fun getParentBinding(): ActivityBaseBinding {
    return bindingParent
  }

  override fun getApiFailure(msg: String) {
    showToast(msg)
  }
  private fun setOnClickListener()
  {
    bindingParent.appBarWithLayout.faq.setOnClickListener {
      val intent=Intent(this,FaqActivity::class.java)
      startActivity(intent)
     // val intent= Intent(this,EmployeeTrackingActivity::class.java)
    //  startActivity(intent)
    }
}}