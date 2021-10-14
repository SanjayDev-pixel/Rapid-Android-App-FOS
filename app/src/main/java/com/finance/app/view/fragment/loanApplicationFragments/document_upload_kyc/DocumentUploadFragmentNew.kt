package com.finance.app.view.fragment.loanApplicationFragments.document_upload_kyc

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.databinding.FragmentDocumentUploadNewBinding
import com.finance.app.eventBusModel.AppEvents
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.adapters.pager.DocumentUploadPagerAdapter
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants.APP.KEY_APPLICANT
import motobeans.architecture.customAppComponents.activity.BaseFragment

class DocumentUploadFragmentNew : BaseFragment() {
    private lateinit var mContext: Context
    private lateinit var binding : FragmentDocumentUploadNewBinding
    private var pagerAdapterApplicants: DocumentUploadPagerAdapter? = null

    companion object {
        fun newInstance(): DocumentUploadFragmentNew {
            return DocumentUploadFragmentNew()
        }
    }
    override fun init() {

    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!
        ArchitectureApp.instance.component.inject(this)
    }
    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater, container, R.layout.fragment_document_upload_new)
        binding.lifecycleOwner = this

        setOnClickListener()

        return binding.root
    }
    private fun setOnClickListener() {
        binding.btnPrevious.setOnClickListener { AppEvents.fireEventLoanAppChangeNavFragmentPrevious() }
        //binding.btnNext.setOnClickListener { saveApplicantEmploymentDetails() }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Now fetch applicants from database.....
        fetchLeadApplicantDetails()
    }
    private fun fetchLeadApplicantDetails() {
        var incomeConsiderApplicantList: ArrayList<PersonalApplicantsModel>
        LeadMetaData.getLeadObservable().observe(this, Observer { leadDetail ->
            leadDetail?.let {
                //Now Set Applicant Tabs....

                setApplicantTabLayout(ArrayList(it.personalData.applicantDetails))
            }
        })
    }
    private fun setApplicantTabLayout(applicantList: ArrayList<PersonalApplicantsModel>) {
        pagerAdapterApplicants = DocumentUploadPagerAdapter(fragmentManager!!, /*alCoApplicants,*/ applicantList)
        binding.viewPager.offscreenPageLimit = 5 //Must be called before setting adapter
        binding.viewPager.adapter = pagerAdapterApplicants
        binding.tabLead.setupWithViewPager(binding.viewPager)
    }
}