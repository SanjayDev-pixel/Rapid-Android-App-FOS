package com.finance.app.view.fragment.loanApplicationFragments.personal

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.finance.app.R
import com.finance.app.databinding.FragmentPersonalInfoNewBinding
import com.finance.app.eventBusModel.AppEvents
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.utility.LeadAndLoanDetail
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.adapters.pager.PersonalPagerAdapter
import kotlinx.android.synthetic.main.delete_dialog.view.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.customAppComponents.activity.BaseFragment
import motobeans.architecture.development.interfaces.FormValidation
import javax.inject.Inject

class PersonalInfoFragmentNew : BaseFragment() {

    @Inject
    lateinit var formValidation: FormValidation

    private var  pagerAdapterApplicants: PersonalPagerAdapter? = null

    private lateinit var binding: FragmentPersonalInfoNewBinding
    private lateinit var selectedApplicantNumber: String
    private var pagerPosition = 0
    private var checkLeadIsSubmitted = 0

    companion object {
        fun newInstance(): PersonalInfoFragmentNew {
            return PersonalInfoFragmentNew()
        }
    }

    override fun init() {
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        ArchitectureApp.instance.component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater, container, R.layout.fragment_personal_info_new)
        binding.lifecycleOwner = this
        setOnClickListeners()
        LeadMetaData.getLeadData()?.let {
            if (it.status.equals(AppEnums.LEAD_TYPE.SUBMITTED.type, true)) {
                binding.btnAddApplicantTab.visibility = View.GONE
                checkLeadIsSubmitted = 1
            } else {
                binding.btnAddApplicantTab.visibility = View.VISIBLE
                checkLeadIsSubmitted = 0
            }

        }


        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Now fetch applicants from database.....
        fetchLeadApplicantDetails()
        if(pagerPosition>0  && checkLeadIsSubmitted ==0)
        {
            binding.btnDeleteCoApplicant.visibility =  View.VISIBLE
        }
        else{
            binding.btnDeleteCoApplicant.visibility =  View.GONE

        }


        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }
            override fun onPageScrolled(position: Int , positionOffset: Float , positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                pagerPosition = position
                if(pagerPosition>0 && checkLeadIsSubmitted ==0)
                {
                    binding.btnDeleteCoApplicant.visibility =  View.VISIBLE
                }
                else{
                    binding.btnDeleteCoApplicant.visibility =  View.GONE

                }
               /* if(checkLeadIsSubmitted == 1)
                {
                    binding.btnDeleteCoApplicant.visibility = View.GONE
                }
                else{
                    binding.btnDeleteCoApplicant.visibility = View.VISIBLE
                }*/

            }

        })

        binding.btnDeleteCoApplicant.setOnClickListener {
            showBankDetailConfirmDeleteDialog()
            System.out.println("Size>>>>?.deleteItem(pagerPosition)"+pagerAdapterApplicants?.count)
        }
    }
    private fun showBankDetailConfirmDeleteDialog() {
        val deleteDialogView = LayoutInflater.from(activity).inflate(R.layout.delete_dialog , null)
        val mBuilder = AlertDialog.Builder(activity)
                .setView(deleteDialogView)
                .setTitle("Delete Co-applicant")
        val deleteDialog = mBuilder.show()
        deleteDialogView.tvDeleteConfirm.setOnClickListener {
            //pagerAdapterApplicants?.deleteItem(pagerPosition)
            LeadMetaData.getLeadObservable().observe(this, Observer { leadDetail ->
                leadDetail?.let {
                    //it.bankData.bankDetailList.
                    //Now Set Applicant Tabs....
                    //setApplicantTabLayout(ArrayList(it.personalData.applicantDetails))
                    //it.personalData.applicantDetails[pagerPosition].isActive = false
                    //setApplicantTabLayout(ArrayList(it.personalData.applicantDetails))

                    pagerAdapterApplicants?.deleteItem(pagerPosition)
                    if (it.bankData.bankDetailList.size > 0) {
                            if(pagerPosition < it.bankData.bankDetailList.size) {
                                it.bankData.bankDetailList.removeAt(pagerPosition)
                            }
                        }
                    if(it.employmentData.applicantDetails.size > 0) {
                       if(pagerPosition < it.employmentData.applicantDetails.size) {
                           it.employmentData.applicantDetails.removeAt(pagerPosition)
                       }
                    }

                        if(it.documentData.documentDetailList.size > 0) {
                            if(pagerPosition < it.documentData.documentDetailList.size) {
                                it.documentData.documentDetailList.removeAt(pagerPosition)
                            }
                        }


                    /*pagerAdapterApplicants?.let { adapter ->
                        if (adapter.isApplicantDetailsValid()) {
                            LeadMetaData().savePersonalData(adapter.getApplicantDetails())

                        }
                    }*/
                }
            })
            deleteDialog.dismiss()
        }
        deleteDialogView.tvDonotDelete.setOnClickListener { deleteDialog.dismiss() }
    }

    private fun fetchLeadApplicantDetails() {
        LeadMetaData.getLeadObservable().observe(this, Observer { leadDetail ->
            leadDetail?.let {
                //Now Set Applicant Tabs....
                setApplicantTabLayout(ArrayList(it.personalData.applicantDetails))


            }
        })

    }    private fun setApplicantTabLayout(applicantList: ArrayList<PersonalApplicantsModel>) {
                    pagerAdapterApplicants = PersonalPagerAdapter(fragmentManager!! , applicantList)
                    binding.viewPager.offscreenPageLimit = 5 //Must be called before setting adapter
                    binding.viewPager.adapter = pagerAdapterApplicants
                    binding.tabLead.setupWithViewPager(binding.viewPager)
    }


    private fun setOnClickListeners() {

        binding.btnPrevious.setOnClickListener { AppEvents.fireEventLoanAppChangeNavFragmentPrevious() }
        binding.btnNext.setOnClickListener { addApplicant() }
        binding.btnAddApplicantTab.setOnClickListener {
            pagerAdapterApplicants?.addItem()
            if (binding.tabLead.tabCount > 2)//Scroll tab to last item....
                binding.tabLead.getTabAt(binding.tabLead.tabCount - 1)?.select()
        }
    }

    private fun addApplicant() {

        pagerAdapterApplicants?.let { adapter ->
            if (adapter.isApplicantDetailsValid()) {
                LeadMetaData().savePersonalData(adapter.getApplicantDetails())

                AppEvents.fireEventLoanAppChangeNavFragmentNext()
            }
        }
    }

}