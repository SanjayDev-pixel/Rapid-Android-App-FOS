package com.finance.app.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.finance.app.R
import com.finance.app.databinding.DialogBankDetailFormBinding
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.BankDetailBean
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.utility.ShowAsMandatory
import com.finance.app.view.adapters.recycler.spinner.MasterSpinnerAdapter
import kotlinx.android.synthetic.main.dialog_bank_detail_form.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.util.AppUtils.showToast
import java.util.*
import javax.inject.Inject

class BankDetailDialogFragment : DialogFragment() {
    enum class Action {
        NEW ,
        EDIT ,
        SUBMITTED;
    }

    @Inject
    lateinit var formValidation: FormValidation

    private lateinit var binding: DialogBankDetailFormBinding

    private lateinit var action: Action
    private var bankDetail: BankDetailBean? = null
    private lateinit var allMasterDropDown: AllMasterDropDown

    private lateinit var dialogCallback: OnBankDetailDialogCallback

    companion object {
        private const val SALARIED = 93
        fun newInstance(action: Action , dialogCallback: OnBankDetailDialogCallback , allMasterDropDown: AllMasterDropDown , bankDetail: BankDetailBean? = null): BankDetailDialogFragment {
            val fragment = BankDetailDialogFragment()
            fragment.action = action
            fragment.dialogCallback = dialogCallback
            fragment.allMasterDropDown = allMasterDropDown
            fragment.bankDetail = bankDetail
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ArchitectureApp.instance.component.inject(this)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater , R.layout.dialog_bank_detail_form , container , false)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)


        initViews()
        setOnClickListeners()
        checkActionType()
    }

    private fun initViews() {
        ShowAsMandatory(binding.inputLayoutAccountHolderName)
        ShowAsMandatory(binding.inputLayoutAccountNum)

//        binding.spinnerBankName.adapter = MasterSpinnerAdapter(context!!, allMasterDropDown.BankName!!)
        val adapter = ArrayAdapter(context!! , android.R.layout.simple_list_item_1 , allMasterDropDown.BankName!!)
        binding.actBankName.setAdapter(adapter)
        binding.actBankName.setOnItemClickListener { parent , view , position , id ->
            binding.actBankName.tag = parent.getItemAtPosition(position) as DropdownMaster
        }



        binding.spinnerAccountType.adapter = MasterSpinnerAdapter(context!! , allMasterDropDown.AccountType!!)
        binding.spinnerSalaryCredit.adapter = MasterSpinnerAdapter(context!! , allMasterDropDown.SalaryCredit!!)
        binding.spinnerSalaryCredit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*> , view: View? , position: Int , id: Long) {
                if (position >= 0) {
                    val salaryCredit = parent.selectedItem as DropdownMaster
                    if (salaryCredit.typeDetailID == SALARIED) binding.inputLayoutSalaryCreditInSixMonth.visibility = View.VISIBLE
                    else binding.inputLayoutSalaryCreditInSixMonth.visibility = View.GONE
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.ivClose.setOnClickListener { dismiss() }
        binding.btnAdd.setOnClickListener {
            if (formValidation.validateBankDetail(binding)) {
                if (action == Action.NEW) dialogCallback.onSaveBankDetail(getFilledBankDetails())
                else if (action == Action.EDIT) dialogCallback.onEditBankDetail(getFilledBankDetails())
                dismiss()
            } else showToast(context , getString(R.string.validation_error))
        }
    }

    private fun checkActionType() {
        if (action == Action.EDIT) {
            btnAdd.text = getString(R.string.update)
            bankDetail?.let { setFormDetails(it) }
        } else if (action == Action.SUBMITTED) {
            btnAdd.visibility = View.GONE
            bankDetail?.let { setFormDetails(it) }
        }
    }

    private fun setFormDetails(bankDetail: BankDetailBean) {
        allMasterDropDown.BankName?.forEachIndexed { index , dropdownMaster ->
            if (dropdownMaster.typeDetailID == bankDetail.bankNameTypeDetailID) {
                (binding.actBankName.setText(dropdownMaster.typeDetailDisplayText.toString() , false))
                binding.actBankName.tag = dropdownMaster
                return@forEachIndexed
            }
        }

        allMasterDropDown.AccountType?.forEachIndexed { index , dropdownMaster ->
            if (dropdownMaster.typeDetailID == bankDetail.accountTypeDetailID) {
                binding.spinnerAccountType.setSelection(index + 1)
                return@forEachIndexed
            }
        }

        allMasterDropDown.SalaryCredit?.forEachIndexed { index , dropdownMaster ->
            if (dropdownMaster.typeDetailID == bankDetail.salaryCreditTypeDetailID) {
                binding.spinnerSalaryCredit.setSelection(index + 1)
                return@forEachIndexed
            }
        }

        binding.etAccountNum.setText(bankDetail.accountNumber)
        binding.etAccountHolderName.setText(bankDetail.accountHolderName)
        binding.etSalaryCreditedInSixMonths.setText(bankDetail.numberOfCredit.toString())
    }

    private fun getFilledBankDetails(): BankDetailBean {
        val bankDetails = BankDetailBean()
        //For Auto complete view..
        val bName = binding.actBankName.tag as DropdownMaster?

        val aType = binding.spinnerAccountType.selectedItem as DropdownMaster?
        val salaryCredit = binding.spinnerSalaryCredit.selectedItem as DropdownMaster?

        bankDetails.applicationDocumentID = if (this.bankDetail?.applicationDocumentID.isNullOrEmpty()) Date().time.toString() else this.bankDetail?.applicationDocumentID
        bankDetails.bankNameTypeDetailID = bName?.typeDetailID
        bankDetails.bankName = bName?.typeDetailCode
        bankDetails.accountTypeDetailID = aType?.typeDetailID
        bankDetails.accountTypeName = aType?.typeDetailCode
        bankDetails.salaryCreditTypeDetailID = salaryCredit?.typeDetailID
        bankDetails.accountHolderName = binding.etAccountHolderName.text.toString()
        bankDetails.accountNumber = binding.etAccountNum.text.toString()
        bankDetails.numberOfCredit = binding.etSalaryCreditedInSixMonths.text.toString()
        bankDetails.salaryCreditTypeDetailID = salaryCredit?.typeDetailID

        return bankDetails
    }

    interface OnBankDetailDialogCallback {
        fun onSaveBankDetail(bankDetail: BankDetailBean)
        fun onEditBankDetail(bankDetail: BankDetailBean)
    }

}