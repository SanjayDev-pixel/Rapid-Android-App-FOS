package com.finance.app.view.activity

import android.content.Context
import android.content.Intent
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.databinding.ActivityLoanApplicationBinding
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.persistence.model.CoApplicantsList
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.fragment.NavMenuFragment
import com.finance.app.view.fragment.loanApplicationFragments.LoanInfoFragmentNew
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoanApplicationActivity : BaseAppCompatActivity() {
    private val binding: ActivityLoanApplicationBinding by ActivityBindingProviderDelegate(
        this, R.layout.activity_loan_application
    )
    private lateinit var navFragment: NavMenuFragment
    private lateinit var secondaryFragment: Fragment
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoanApplicationActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun init() {
        hideToolbar()
        hideSecondaryToolbar()
        ArchitectureApp.instance.component.inject(this)
        populateLeadRelatedData()
        setNavFragment()
        setClickListeners()
        secondaryFragment = LoanInfoFragmentNew.newInstance()
        setSecondaryFragment(secondaryFragment)
    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }
    private fun setClickListeners() {
        binding.collapseImageView.setOnClickListener { navFragment.toggleMenu() }
        val layoutBack: LinearLayout = findViewById(R.id.lytBack)
        layoutBack.setOnClickListener { showDialog() }
    }
    private fun populateLeadRelatedData() {
        LeadMetaData.getLeadObservable().observe(this, Observer { leadDetail ->
            leadDetail?.let {
                setLeadNum(leadDetail.leadNumber)
                fillLeadData(leadDetail)
            }
        })
    }
    private fun fillLeadData(leadMaster: AllLeadMaster) {
        val leadName = leadMaster.applicantFirstName + " " + leadMaster.applicantMiddleName + " " + leadMaster.applicantLastName
        binding.tvMobile.text = leadMaster.applicantContactNumber
        binding.header.tvLeadNumber.text = leadMaster.leadNumber
        binding.applicantName.text = leadName
        binding.tvDesignation.text = getString(R.string.applicant)
    }
    private fun setNavFragment() {
        navFragment = NavMenuFragment()
        val ft = supportFragmentManager.beginTransaction().apply {
            add(R.id.navMenuContainer, navFragment)
            addToBackStack(null)
        }
        ft.commit()
    }
    private fun setSecondaryFragment(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction().apply {
            add(R.id.secondaryFragmentContainer, fragment)
            addToBackStack(null)
        }
        ft.commit()
    }
    override fun onBackPressed() = showDialog()
    private fun showDialog() {
        runOnUiThread {
            if (!isFinishing) {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.warning_msg))
                    .setMessage(getString(R.string.data_loss_msg))
                    .setCancelable(false)
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton("ok") { _, _ ->
                        this.finish()
                        super.onBackPressed()
                    }.show()
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(coApplicantsList: CoApplicantsList) {

        binding.applicantName.text = coApplicantsList.firstName.plus(" " + coApplicantsList.middleName)
        binding.header.tvLeadNumber.text = coApplicantsList.leadApplicantNumber
        binding.tvDesignation.text = coApplicantsList.applicantType
        binding.tvMobile.text = coApplicantsList.mobile
    }
    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}


