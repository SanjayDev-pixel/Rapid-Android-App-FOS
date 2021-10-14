package com.finance.app.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.app.R
import com.finance.app.databinding.FragmentLeadListingBinding
import com.finance.app.others.AppEnums
import com.finance.app.others.Injection
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.view.adapters.recycler.adapter.LeadListingAdapter
import com.finance.app.viewModel.AppDataViewModel
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.customAppComponents.activity.BaseFragment
import motobeans.architecture.development.interfaces.DataBaseUtil
import javax.inject.Inject

class LeadsListingFragment : BaseFragment() {

    @Inject
    lateinit var dataBase: DataBaseUtil
    private lateinit var binding: FragmentLeadListingBinding
    private lateinit var appDataViewModel: AppDataViewModel
    private var adapter: LeadListingAdapter? = null
    private var leads = ArrayList<AllLeadMaster>()
    private var currentLeadStatus: AppEnums.LEAD_TYPE? = null
    companion object {
        const val KEY_LEAD_STATUS = "leadStatus"

        fun newInstance(leadStatusEnum: AppEnums.LEAD_TYPE?): LeadsListingFragment {
            val fragment = LeadsListingFragment()
            leadStatusEnum?.let {
                val args = Bundle()
                args.putSerializable(KEY_LEAD_STATUS, leadStatusEnum)
                fragment.arguments = args
            }
            return fragment
        }
    }
    override fun init() {

    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        ArchitectureApp.instance.component.inject(this)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater, container, R.layout.fragment_lead_listing)
        val viewModelFactory: ViewModelProvider.Factory = Injection.provideViewModelFactory(activity!!)
        appDataViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(AppDataViewModel::class.java)
        setOnSwipeListener()

        return binding.root
    }
    private fun setOnSwipeListener() {
        binding.swipeLayoutRoot.setOnRefreshListener {
            //Now fetch lead data from database if we got the Lead Status...
            currentLeadStatus?.let { fetchLeadFromDatabase(it) }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.getSerializable(KEY_LEAD_STATUS)?.let { status ->
            val leadStatus = status as AppEnums.LEAD_TYPE
            currentLeadStatus = leadStatus
            //Now fetch lead data from database if we got the Lead Status...
            fetchLeadFromDatabase(leadStatus)
            getDataAfterSerach(leadStatus)
        }
    }
    private fun setLeadListAdapter(list: ArrayList<AllLeadMaster>, leadStatus: AppEnums.LEAD_TYPE) {
        if (adapter == null) {
            binding.rcLeads.layoutManager = LinearLayoutManager(this.activity)
            adapter = LeadListingAdapter(this.requireActivity(), list, leadStatus)
            binding.rcLeads.adapter = adapter
        } else {
            adapter?.updateAdapterList(list)
        }
    }
    private var leadAdapter: LeadListingAdapter? = null
    private fun setUpRecyclerView(leadStatus: AppEnums.LEAD_TYPE) {
        leadAdapter = LeadListingAdapter(mContext = this.requireActivity(), leadList = leads, leadStatusEnums = leadStatus)
        binding.rcLeads.adapter = leadAdapter
        //hideProgressDialog()
    }
    override fun showProgressDialog() {
        binding.swipeLayoutRoot.isRefreshing = true
    }
    override fun hideProgressDialog() {
        binding.swipeLayoutRoot.isRefreshing = false
    }
    private fun fetchLeadFromDatabase(leadStatus: AppEnums.LEAD_TYPE) {
        val observer = when (leadStatus) {
            AppEnums.LEAD_TYPE.ALL -> appDataViewModel.getAllLeads()
            else -> appDataViewModel.getLeadsByStatus(leadStatus.type)
        }
        showProgressDialog() //Show progress bar...
        observer.observe(this@LeadsListingFragment, Observer { leadList ->
            hideProgressDialog() //Hide progress bar...
            leadList?.let { setLeadListAdapter(ArrayList(it), leadStatus) }
        })
    }
    private fun getDataAfterSerach(leadStatus: AppEnums.LEAD_TYPE){
        appDataViewModel.getLeadsLiveDataByStatus(leadStatus)?.observe(this, Observer { itemsFromDB ->
            itemsFromDB?.let {
                leads.clear()
                leads.addAll(itemsFromDB)
                setUpRecyclerView(leadStatus)
            }
        })


    }
}