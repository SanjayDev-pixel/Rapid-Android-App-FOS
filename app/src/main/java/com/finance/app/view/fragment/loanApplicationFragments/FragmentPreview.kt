package com.finance.app.view.fragment.loanApplicationFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.finance.app.R
import com.finance.app.databinding.PreviewLayoutBinding
import com.finance.app.others.AppEnums
import com.finance.app.others.Injection
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.activity.FinalSubmitActivity
import com.finance.app.view.adapters.recycler.adapter.PreviewAdapter
import com.finance.app.viewModel.AppDataViewModel
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants.APP.KEY_APPLICATION_SCREEN
import motobeans.architecture.customAppComponents.activity.BaseFragment

class FragmentPreview : BaseFragment() {

    private lateinit var binding: PreviewLayoutBinding

    companion object {
        fun newInstance(screenNameEnum: AppEnums.ScreenLoanApp?): FragmentPreview {
            val fragment = FragmentPreview()
            val args = Bundle()
            args.putSerializable(KEY_APPLICATION_SCREEN, screenNameEnum)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater, container, R.layout.preview_layout)
        binding.lifecycleOwner = this
        init()
        initOnClickListener()
        return view
    }

    private fun initOnClickListener() {


    }

    override fun init() {
        ArchitectureApp.instance.component.inject(this)

        val viewModelFactory: ViewModelProvider.Factory = Injection.provideViewModelFactory(activity!!)
        val appDataViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(AppDataViewModel::class.java)

        arguments?.getSerializable(KEY_APPLICATION_SCREEN)?.let { screenName ->
            val screenNameEnum = screenName as AppEnums.ScreenLoanApp

            val leadData = LeadMetaData.getLeadData()
            leadData?.let {
                setUpRecyclerView(screenNameEnum, leadData, appDataViewModel)
            }
        }
    }

    private fun setUpRecyclerView(enum: AppEnums.ScreenLoanApp, leadData: AllLeadMaster, viewModel: AppDataViewModel) {
        val previewAdapter = PreviewAdapter(mContext = this.requireActivity(), screenNameEnums = enum,
                lead = leadData, viewModel = viewModel)
        binding.rcApplicants.adapter = previewAdapter
    }

}
