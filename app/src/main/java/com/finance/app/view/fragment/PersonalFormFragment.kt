package com.finance.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finance.app.R
import com.finance.app.databinding.FragmentPersonalFormBinding
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.viewModel.AppDataViewModel
import motobeans.architecture.customAppComponents.activity.BaseFragment
import motobeans.architecture.development.interfaces.DataBaseUtil
import javax.inject.Inject

class PersonalFormFragment : BaseFragment() {
    @Inject
    lateinit var dataBase: DataBaseUtil
    private lateinit var binding: FragmentPersonalFormBinding
    private lateinit var appDataViewModel: AppDataViewModel

    companion object {
        const val KEY_CO_APPLICANT = "coApplicant"

        fun newInstance(coApplicant: PersonalApplicantsModel?): PersonalFormFragment {
            val fragment = PersonalFormFragment()
            val args = Bundle()
            args.putSerializable(KEY_CO_APPLICANT, coApplicant)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater, container, R.layout.fragment_personal_form)
        init()
        return binding.root
    }

    override fun init() {
//        ArchitectureApp.instance.component.inject(this)
//        val viewModelFactory: ViewModelProvider.Factory = Injection.provideViewModelFactory(activity!!)
//        appDataViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(AppDataViewModel::class.java)
       arguments?.getSerializable(KEY_CO_APPLICANT)?.let { Applicant ->
           val applicant = Applicant as PersonalApplicantsModel
      }
    }
}