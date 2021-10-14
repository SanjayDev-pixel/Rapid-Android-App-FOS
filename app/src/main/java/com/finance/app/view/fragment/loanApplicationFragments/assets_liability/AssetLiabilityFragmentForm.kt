package com.finance.app.view.fragment.loanApplicationFragments.assets_liability


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.finance.app.R
import com.finance.app.databinding.AssetLiabilityFragmentFormBinding
import com.finance.app.persistence.model.AssetLiabilityModel
import com.finance.app.persistence.model.PersonalApplicantsModel
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.customAppComponents.activity.BaseFragment
import motobeans.architecture.development.interfaces.DataBaseUtil
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */


class AssetLiabilityFragmentForm : BaseFragment() {

    @Inject
    lateinit var dataBase: DataBaseUtil

    private var selectedApplicant: PersonalApplicantsModel? = null

    private lateinit var binding: AssetLiabilityFragmentFormBinding


    companion object {
        fun newInstance(applicant: PersonalApplicantsModel): AssetLiabilityFragmentForm {
            val fragment = AssetLiabilityFragmentForm()
            fragment.selectedApplicant = applicant
            return fragment
        }

    }

    override fun init() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ArchitectureApp.instance.component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater , container , R.layout.asset_liability_fragment_form)

        initViews()
        setOnClickListener()

        return binding.root
    }

    private fun initViews() {

    }

    private fun setOnClickListener() {

    }

    private fun shouldShowEmptyView() {
        selectedApplicant?.let {
            if (it.incomeConsidered != null && it.incomeConsidered!!) {
                binding.vwIncomeConsider.visibility = View.VISIBLE
                binding.vwIncomeNotConsider.visibility = View.GONE
            } else {
                binding.vwIncomeConsider.visibility = View.GONE
                binding.vwIncomeNotConsider.visibility = View.VISIBLE
            }
        } ?: run {
            binding.vwIncomeConsider.visibility = View.GONE
            binding.vwIncomeNotConsider.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        shouldShowEmptyView()
        activity?.let { selectedApplicant?.let { applicant -> binding.customAssetView.initApplicantDetails(it , applicant) } }
    }

    fun getAssetsAndLiability(): AssetLiabilityModel {
        return binding.customAssetView.getCurrentApplicant()
    }
}
