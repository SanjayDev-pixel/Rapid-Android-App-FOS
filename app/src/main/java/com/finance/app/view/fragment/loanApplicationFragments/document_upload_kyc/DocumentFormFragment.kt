package com.finance.app.view.fragment.loanApplicationFragments.document_upload_kyc

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.databinding.FragmentDocumentFormBinding
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.utility.LeadMetaData
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.Constants.APP.KEY_APPLICANT
import motobeans.architecture.customAppComponents.activity.BaseFragment

class DocumentFormFragment : BaseFragment() {
    private lateinit var mContext: Context
    private lateinit var binding: FragmentDocumentFormBinding
    //private var selectedApplicant: PersonalApplicantsModel? = null
    override fun init() {

    }
    companion object {

        fun newInstance(selectedApplicant: PersonalApplicantsModel): DocumentFormFragment {
            val fragment = DocumentFormFragment()
            val args = Bundle()
            args.putSerializable(KEY_APPLICANT , selectedApplicant)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!
       ArchitectureApp.instance.component.inject(this)
    }
    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater , container , R.layout.fragment_document_form)
        binding.lifecycleOwner = this

        return binding.root
    }
    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        //Now fetch arguments...
        fetchSelectedApplicant()
        //Show empty view if this applicant details not required...
        //shouldShowEmptyView()
    }

    private fun fetchSelectedApplicant() {
        arguments?.getSerializable(KEY_APPLICANT)?.let { applicantDetails ->
            val selectedApplicant = applicantDetails as PersonalApplicantsModel
            inflateFormView(selectedApplicant)
            System.out.println("DocumentFormFrgamentCaleed")
        }
    }
    private fun inflateFormView(applicant: PersonalApplicantsModel) {
        activity?.let { activityInstance ->
            LeadMetaData.getLeadObservable().observe(this, Observer { leadDetails ->
                leadDetails?.leadID?.let { id -> binding.customKycUploadDocument.attachView(activityInstance, arguments?.getInt(Constants.KEY_INDEX) ?: 0, applicant, id)}
            })
        }
    }

}