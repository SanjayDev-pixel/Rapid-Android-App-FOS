package com.finance.app.view.fragment.loanApplicationFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.finance.app.R
import com.finance.app.databinding.FragmentLoanInformationBinding
import com.finance.app.eventBusModel.AppEvents
import com.finance.app.others.AppEnums
import com.finance.app.others.Injection
import com.finance.app.persistence.model.*
import com.finance.app.utility.CurrencyConversion
import com.finance.app.utility.DisableLoanInfoForm
import com.finance.app.utility.LeadMetaData
import com.finance.app.utility.SetLoanInfoMandatoryField
import com.finance.app.view.customViews.CustomSpinnerView
import com.finance.app.view.customViews.interfaces.IspinnerMainView
import com.finance.app.viewModel.AppDataViewModel
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.customAppComponents.activity.BaseFragment
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import javax.inject.Inject

class LoanInfoFragmentNew : BaseFragment() {
    @Inject
    lateinit var formValidation: FormValidation
    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    private lateinit var binding: FragmentLoanInformationBinding
    private lateinit var appDataViewModel: AppDataViewModel
    private lateinit var interestType: CustomSpinnerView<DropdownMaster>
    private lateinit var loanScheme: CustomSpinnerView<DropdownMaster>
    private lateinit var loanProduct: CustomSpinnerView<LoanProductMaster>
    private var leadDetail: AllLeadMaster? = null
    private lateinit var loanPurpose: CustomSpinnerView<LoanPurpose>
    private var spinnerDMList: ArrayList<CustomSpinnerView<DropdownMaster>> = ArrayList()
    companion object {
        fun newInstance(): LoanInfoFragmentNew = LoanInfoFragmentNew()
    }
    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater , container , R.layout.fragment_loan_information)
        binding.lifecycleOwner = this
        init()
        return view
    }

    override fun init() {
//        leadDetail = LeadMetaData.getLeadData()
        ArchitectureApp.instance.component.inject(this)
        val viewModelFactory: ViewModelProvider.Factory = Injection.provideViewModelFactory(activity!!)
        appDataViewModel = ViewModelProviders.of(activity!! , viewModelFactory).get(AppDataViewModel::class.java)
        SetLoanInfoMandatoryField(binding)
        //Fetch lead details from database..
        fetchLeadDetails()
    }

    private fun fetchLeadDetails() {
        LeadMetaData.getLeadObservable().observe(this , Observer { leadDetails ->
            leadDetails?.let {
                this@LoanInfoFragmentNew.leadDetail = it
                //Now set all dependable view..
                val loanInfo = leadDetail?.loanData
                setUpCustomViews(loanInfo)
                getDropDownsFromDB(loanInfo)
                setLeadInformation()
                setClickListeners()
            }
        })
    }

    private fun setLeadInformation() {
        LeadMetaData.getLeadData()?.amountRequest?.let { binding.etAmountRequest.setText(it.toString()) }
        //System.out.println("Loan Amount Requested>>>>>" + loanInfo.loanAmountRequest)
    }

    private fun setUpCustomViews(loanInfo: LoanInfoModel?) {
        activity?.let {
            binding.viewChannelPartner.attachActivity(activity = activity!! , loanData = loanInfo)

        }
    }

    private fun setClickListeners() {
        CurrencyConversion().convertToCurrencyType(binding.etAmountRequest)
        binding.btnNext.setOnClickListener {
            val lProductDD = loanProduct.getSelectedValue()
            if (lProductDD != null) {
                if (formValidation.validateLoanInformation(
                                binding , loanProduct , loanPurpose ,
                                spinnerDMList , binding.viewChannelPartner
                        )) {
                    checkPropertySelection()
                    LeadMetaData().saveLoanData(getLoanData())
                    AppEvents.fireEventLoanAppChangeNavFragmentNext()

                } else showToast(getString(R.string.validation_error))
            } else {
                showToast(getString(R.string.enter_mandatory))

            }
        }
    }

    private fun getDropDownsFromDB(loanInfo: LoanInfoModel?) {
        appDataViewModel.getLoanProductMaster().observe(viewLifecycleOwner , Observer { loanProductValue ->
            loanProductValue?.let {
                val arrayListOfLoanProducts = ArrayList<LoanProductMaster>()
                arrayListOfLoanProducts.addAll(loanProductValue)
                setLoanProductDropdown(arrayListOfLoanProducts , loanInfo)
            }
        })

        appDataViewModel.getAllMasterDropdown().observe(viewLifecycleOwner , Observer { masterDrownDownValues ->
            masterDrownDownValues?.let {
                setMasterDropDownValue(masterDrownDownValues , loanInfo)
            }
        })
    }

    private fun setLoanProductDropdown(products: ArrayList<LoanProductMaster> , loanInfo: LoanInfoModel?) {
        loanProduct = CustomSpinnerView(mContext = activity!! , isMandatory = true , dropDowns = products , label = "Loan Product *" , iSpinnerMainView = object : IspinnerMainView<LoanProductMaster> {
            override fun getSelectedValue(value: LoanProductMaster) {
                setLoanPurposeDropdown(value , loanInfo)
            }
        })
        binding.layoutLoanProduct.addView(loanProduct)

        if (loanInfo != null && loanInfo.applicationNumber != null) {
            loanProduct.setSelection(loanInfo.productID.toString())
        } else loanProduct.setSelection(leadDetail?.loanProductID.toString())
    }

    private fun setLoanPurposeDropdown(loan: LoanProductMaster? , loanInfo: LoanInfoModel?) {
        loan?.let {
            binding.layoutLoanPurpose.removeAllViews()
            loanPurpose = CustomSpinnerView(mContext = activity!! , isMandatory = true , dropDowns = loan.loanPurposeList , label = "Loan Purpose *")
            binding.layoutLoanPurpose.addView(loanPurpose)
            if (leadDetail!!.status == AppEnums.LEAD_TYPE.SUBMITTED.type) {
                loanPurpose.disableSelf()

            }
        }

        loanInfo?.loanPurposeID?.let {
            loanPurpose.setSelection(loanInfo.loanPurposeID.toString())
        }
    }

    private fun setMasterDropDownValue(allMasterDropDown: AllMasterDropDown , loanInfo: LoanInfoModel?) {
        setCustomSpinner(allMasterDropDown)
        loanInfo?.let {
            selectSpinnerValue(loanInfo)
            fillFormWithLoanData(loanInfo)
            checkSubmission()
        }
    }

    private fun selectSpinnerValue(loanInfo: LoanInfoModel) {
        interestType.setSelection(loanInfo.interestTypeTypeDetailID?.toString())
        loanScheme.setSelection(loanInfo.loanSchemeTypeDetailID?.toString())
    }

    private fun fillFormWithLoanData(loanInfo: LoanInfoModel) {
        loanInfo.loanAmountRequest?.let { binding.etAmountRequest.setText(it.toString()) }
        System.out.println("Loan Amount Requested>>>>>" + loanInfo.loanAmountRequest)
        loanInfo.affordableEMI?.let { binding.etEmi.setText(it.toInt().toString()) }
        loanInfo.tenure?.let { binding.etTenure.setText(it.toString()) }
        loanInfo.isPropertySelected?.let { binding.cbPropertySelected.isChecked = it }
        loanInfo.applicationNumber?.let { binding.etApplicationNumber.setText(it) }
        interestType.setSelection(loanInfo.interestTypeTypeDetailID?.toString())
    }

    private fun checkPropertySelection() {
        if (binding.cbPropertySelected.isChecked) {
            sharedPreferences.setPropertySelection("Yes")
        } else {
            sharedPreferences.setPropertySelection("No")
        }
    }

    private fun setCustomSpinner(allMasterDropDown: AllMasterDropDown) {
        interestType = CustomSpinnerView(mContext = activity!! , isMandatory = true , dropDowns = allMasterDropDown.LoanInformationInterestType!! , label = "Interest Type *")
        binding.layoutInterestType.addView(interestType)
        interestType.setSelection(allMasterDropDown.LoanInformationInterestType!![1].typeDetailID.toString())
        interestType.disableSelf()

        loanScheme = CustomSpinnerView(mContext = activity!! , isMandatory = true , dropDowns = allMasterDropDown.LoanScheme!! , label = "Loan Scheme *")
        binding.layoutLoanScheme.addView(loanScheme)

        spinnerDMList.add(interestType)
        spinnerDMList.add(loanScheme)
    }

    private fun checkSubmission() {
        if (leadDetail!!.status == AppEnums.LEAD_TYPE.SUBMITTED.type) {
            //Three Field is remaining need to change
            //1. Sourcing Channel Partner
            //2. Channel Partner Name
            //3.Loan purpose
            DisableLoanInfoForm(binding , loanProduct , loanScheme , interestType , binding.viewChannelPartner)

        }
    }

    private fun getLoanData(): LoanInfoModel {
        val loanInfoObj = LoanInfoModel()
        val sPartner = binding.viewChannelPartner.getSourcingPartner()
        val cPartnerName = binding.viewChannelPartner.getPartnerName()
        val lProductDD = loanProduct.getSelectedValue()
        val lPurposeDD = loanPurpose.getSelectedValue()
        val lScheme = loanScheme.getSelectedValue()
        val iType = interestType.getSelectedValue()
        val empId = sharedPreferences.getEmpId()

        loanInfoObj.leadID = leadDetail?.leadID
        loanInfoObj.productID = lProductDD?.productID
        loanInfoObj.salesOfficerEmpID = empId!!.toInt()
        loanInfoObj.loanPurposeID = lPurposeDD?.loanPurposeID
        loanInfoObj.loanSchemeTypeDetailID = lScheme?.typeDetailID
        loanInfoObj.interestTypeTypeDetailID = iType?.typeDetailID
        loanInfoObj.sourcingChannelPartnerTypeDetailID = sPartner?.typeDetailID
        loanInfoObj.isPropertySelected = binding.cbPropertySelected.isChecked
        loanInfoObj.loanAmountRequest = CurrencyConversion().convertToNormalValue(binding.etAmountRequest.text.toString()).toInt()
        loanInfoObj.tenure = binding.etTenure.text.toString().toInt()
        loanInfoObj.channelPartnerDsaID = cPartnerName?.dsaID
        loanInfoObj.affordableEMI = binding.etEmi.text.toString().toDouble()
        loanInfoObj.logginUserEntityID = sharedPreferences.getUserId()!!.toInt()
        loanInfoObj.channelPartnerName = cPartnerName.toString()
        if (binding.etApplicationNumber.text.toString().startsWith("GG")) {
            loanInfoObj.applicationNumber = binding.etApplicationNumber.text.toString()
        } else {
            loanInfoObj.applicationNumber = "GG".plus(binding.etApplicationNumber.text.toString())
        }

        return loanInfoObj
    }

}
