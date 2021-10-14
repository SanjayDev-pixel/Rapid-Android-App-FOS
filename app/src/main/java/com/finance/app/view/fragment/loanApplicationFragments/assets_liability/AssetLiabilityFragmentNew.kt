package com.finance.app.view.fragment.loanApplicationFragments.assets_liability

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finance.app.R
import com.finance.app.databinding.FragmentAssetliabilityNewBinding
import com.finance.app.eventBusModel.AppEvents
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.adapters.pager.AssetLiabilityPagerAdapter
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.customAppComponents.activity.BaseFragment
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.FormValidation
import java.util.*
import javax.inject.Inject


/**
 * Created by motobeans on 2/16/2018.
 */

class AssetLiabilityFragmentNew : BaseFragment() {

    @Inject
    lateinit var dataBase: DataBaseUtil
    private lateinit var binding: FragmentAssetliabilityNewBinding
    private var pagerAdapterAsset: AssetLiabilityPagerAdapter? = null
    @Inject
    lateinit var formValidation: FormValidation
    private var applicantList: ArrayList<PersonalApplicantsModel>? = null


    companion object {
        fun newInstance(): AssetLiabilityFragmentNew {
            return AssetLiabilityFragmentNew()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ArchitectureApp.instance.component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater, container, R.layout.fragment_assetliability_new)
        binding.lifecycleOwner = this

        initViews()
        setOnClickListener()


        return view
    }

    override fun init() {
    }

    private fun initViews() {
    }

    private fun setOnClickListener() {
        binding.btnPrevious.setOnClickListener { AppEvents.fireEventLoanAppChangeNavFragmentPrevious() }
        binding.btnNext.setOnClickListener {
            pagerAdapterAsset?.getALlAssetsAndLiability()?.let { it1 -> LeadMetaData().saveAssetLiabilityData(it1) }
            AppEvents.fireEventLoanAppChangeNavFragmentNext()
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchLeadDetails()
    }


    private fun fetchLeadDetails() {
        LeadMetaData.getLeadObservable().observe(this, androidx.lifecycle.Observer { leadDetail ->
            leadDetail?.let {
                applicantList = it.personalData?.applicantDetails
                applicantList?.let {
                    //Set Tab Adapter...
                    setApplicantTabAdapter(it)
                }
            }
        })
    }

    private fun setApplicantTabAdapter(applicantList: ArrayList<PersonalApplicantsModel>) {
        pagerAdapterAsset = AssetLiabilityPagerAdapter(fragmentManager!!, applicantList)
        binding.viewPager.offscreenPageLimit = 5 //Must be called before setting adapter
        binding.viewPager.adapter = pagerAdapterAsset
        binding.tabLead.setupWithViewPager(binding.viewPager)
    }

    private fun onSaveAssetAndLibilityDetails() {
//        val pApplicantList = LeadMetaData.getLeadData()?.assetLiabilityData
//        LeadMetaData().saveAssetLiabilityData(pApplicantList)


    }
}


