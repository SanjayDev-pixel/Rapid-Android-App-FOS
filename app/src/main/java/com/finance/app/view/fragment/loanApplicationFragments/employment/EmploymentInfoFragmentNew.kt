package com.finance.app.view.fragment.loanApplicationFragments.employment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.databinding.FragmentEmploymentNewBinding
import com.finance.app.eventBusModel.AppEvents
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.adapters.pager.EmploymentFormPagerAdapter
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.customAppComponents.activity.BaseFragment
import motobeans.architecture.development.interfaces.FormValidation
import javax.inject.Inject

class EmploymentInfoFragmentNew : BaseFragment() {

    @Inject
    lateinit var formValidation: FormValidation
    private var pagerAdapterApplicants: EmploymentFormPagerAdapter? = null

//    private val alCoApplicants = ArrayList<EmploymentApplicantsModel>()
//    private val pApplicantList = ArrayList<PersonalApplicantsModel>()
    private lateinit var binding: FragmentEmploymentNewBinding
    companion object {
        fun newInstance(): EmploymentInfoFragmentNew {
            return EmploymentInfoFragmentNew()
        }
    }

    override fun init() {
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        System.out.println("Pintoo Rawat")
        ArchitectureApp.instance.component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater, container, R.layout.fragment_employment_new)
        binding.lifecycleOwner = this

        setOnClickListener()

        return binding.root
    }

    private fun setOnClickListener() {
        binding.btnPrevious.setOnClickListener { AppEvents.fireEventLoanAppChangeNavFragmentPrevious() }
        binding.btnNext.setOnClickListener { saveApplicantEmploymentDetails() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Now fetch applicants from database.....
        fetchLeadApplicantDetails()
    }

    private fun fetchLeadApplicantDetails() {
        LeadMetaData.getLeadObservable().observe(this, Observer { leadDetail ->
            leadDetail?.let {
                //Now Set Applicant Tabs....
                setApplicantTabLayout(ArrayList(it.personalData.applicantDetails))
            }
        })
    }

    private fun setApplicantTabLayout(applicantList: ArrayList<PersonalApplicantsModel>) {
        pagerAdapterApplicants = EmploymentFormPagerAdapter(fragmentManager!!, /*alCoApplicants,*/ applicantList)
        binding.viewPager.offscreenPageLimit = 5 //Must be called before setting adapter
        binding.viewPager.adapter = pagerAdapterApplicants
        binding.tabLead.setupWithViewPager(binding.viewPager)
    }

    private fun saveApplicantEmploymentDetails() {
        pagerAdapterApplicants?.let { adapter ->
            if (adapter.isEmploymentDetailsValid()) {
                LeadMetaData().saveEmploymentData(adapter.getEmploymentDetails())
                AppEvents.fireEventLoanAppChangeNavFragmentNext()
            }
        }
    }
}
