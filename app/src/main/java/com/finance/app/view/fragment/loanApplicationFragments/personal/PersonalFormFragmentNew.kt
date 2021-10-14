package com.finance.app.view.fragment.loanApplicationFragments.personal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.databinding.FragmentPersonalFormBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.utility.LeadMetaData
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants.APP.KEY_APPLICANT
import motobeans.architecture.constants.Constants.APP.KEY_INDEX
import motobeans.architecture.customAppComponents.activity.BaseFragment

class PersonalFormFragmentNew : BaseFragment() {

    private lateinit var binding: FragmentPersonalFormBinding

    companion object {
        fun newInstance(applicant: PersonalApplicantsModel, index: Int): PersonalFormFragmentNew {
            val fragment = PersonalFormFragmentNew()
            val args = Bundle()
            args.putSerializable(KEY_APPLICANT, applicant)
            args.putInt(KEY_INDEX, index)
            fragment.arguments = args
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
        binding = initBinding(inflater, container, R.layout.fragment_personal_form)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchArguments()
    }

    private fun fetchArguments() {
        arguments?.getSerializable(KEY_APPLICANT)?.let { applicantDetails ->
            val applicant = applicantDetails as PersonalApplicantsModel
            inflateFormView(applicant)
        }
    }

    private fun inflateFormView(applicant: PersonalApplicantsModel) {
        activity?.let { activityInstance ->
            LeadMetaData.getLeadObservable().observe(this, Observer { leadDetails ->
                leadDetails?.leadID?.let { id -> binding.customPersonalView.attachView(activityInstance, arguments?.getInt(KEY_INDEX) ?: 0, applicant, id)}
            })
        }
    }

    fun isApplicantDetailsValid() = binding.customPersonalView.isApplicantDetailsValid()

    fun getApplicant() = binding.customPersonalView.getApplicant()
}