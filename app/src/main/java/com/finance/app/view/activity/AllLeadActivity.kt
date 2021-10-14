package com.finance.app.view.activity

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.finance.app.R
import com.finance.app.databinding.ActivityAllLeadsBinding
import com.finance.app.others.AppEnums
import com.finance.app.view.adapters.pager.LeadPagerAdapter
import com.finance.app.view.fragment.LeadsListingFragment
import com.finance.app.viewModel.AppDataViewModel
import com.finance.app.viewModel.SyncDataViewModel
import motobeans.architecture.appDelegates.ViewModelType
import motobeans.architecture.appDelegates.viewModelProvider
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import javax.inject.Inject

class AllLeadActivity : BaseAppCompatActivity() {
    private val binding: ActivityAllLeadsBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_all_leads)
    @Inject
    lateinit var dataBase: DataBaseUtil
    private var pagerAdapter: LeadPagerAdapter? = null
    private val syncDataViewModel: SyncDataViewModel by viewModelProvider(this, ViewModelType.WITH_DAO)
    private val viewModelLeadData: AppDataViewModel by viewModelProvider(activity = this,
            viewModelType = ViewModelType.WITH_DAO)
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AllLeadActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        hideSecondaryToolbar()
        refreshLead()
        setUpLeadFragments()
        setUpClickListener()
    }
    private fun refreshLead() = syncDataViewModel.getAllLeads()
    private fun setUpClickListener() {
        binding.fabCreate.setOnClickListener {
            //CreateLeadActivity.start(this)
            val intent = Intent(this, CreateLeadActivity::class.java)
            this.startActivity(intent)
        }
    }
    private fun setUpLeadFragments() {
        pagerAdapter = LeadPagerAdapter(supportFragmentManager)
        pagerAdapter?.addFragment(LeadsListingFragment.newInstance(AppEnums.LEAD_TYPE.PENDING), AppEnums.LEAD_TYPE.PENDING.type)
        pagerAdapter?.addFragment(LeadsListingFragment.newInstance(AppEnums.LEAD_TYPE.SUBMITTED), AppEnums.LEAD_TYPE.SUBMITTED.type)
        pagerAdapter?.addFragment(LeadsListingFragment.newInstance(AppEnums.LEAD_TYPE.REJECTED), AppEnums.LEAD_TYPE.REJECTED.type)
        pagerAdapter?.addFragment(LeadsListingFragment.newInstance(AppEnums.LEAD_TYPE.ALL), AppEnums.LEAD_TYPE.ALL.type)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 4 //Total Number fragments to load at once..
        binding.tabLead.setupWithViewPager(binding.viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search_bar,menu)
        val mSearch :MenuItem = menu.findItem(R.id.appSearchBar)
        val mSearchView: SearchView = mSearch.actionView as SearchView
        val searchEditText = mSearchView.findViewById(R.id.search_src_text) as EditText
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.white))
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.white))
        mSearchView.queryHint = ("Search")
               mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                System.out.println("Search>>>>"+newText)
                if(!(newText.equals(""))) {
                    viewModelLeadData.setSearch(newText!! , binding.viewPager.currentItem)
                }

                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}
