package com.finance.app.view.fragment.loanApplicationFragments.bank

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.FragmentBankDetailFormBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.BankDetailBean
import com.finance.app.persistence.model.BankDetailModel
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.activity.DocumentUploadingActivity
import com.finance.app.view.adapters.recycler.adapter.BankDetailAdapter
import com.finance.app.view.dialogs.BankDetailDialogFragment
import kotlinx.android.synthetic.main.delete_dialog.view.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.customAppComponents.activity.BaseFragment
import motobeans.architecture.development.interfaces.DataBaseUtil
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class BankDetailFormFragment : BaseFragment() , BankDetailDialogFragment.OnBankDetailDialogCallback , BankDetailAdapter.ItemClickListener {
    private lateinit var mContext: Context
    @Inject
    lateinit var dataBase: DataBaseUtil
    private var bankAdapter: BankDetailAdapter? = null
    private var allMasterDropDown: AllMasterDropDown? = null
    private var selectedApplicant: PersonalApplicantsModel? = null
    private var selectedBankDetailPosition = -1
    private lateinit var binding: FragmentBankDetailFormBinding

    companion object {
        fun newInstance(selectedApplicant: PersonalApplicantsModel): BankDetailFormFragment {
            val fragment = BankDetailFormFragment()
            fragment.selectedApplicant = selectedApplicant
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!
        ArchitectureApp.instance.component.inject(this)
    }

    override fun init() {
    }

    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater , container , R.layout.fragment_bank_detail_form)
        binding.lifecycleOwner = this
        initViews()
        LeadMetaData.getLeadData()?.let {
            if (it.status.equals(AppEnums.LEAD_TYPE.SUBMITTED.type , true))
                binding.imageAddBank.visibility = View.GONE
        }
        setOnClickListeners()
        return view
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        //Now fetch data from where-ever you want....
        fetchLeadBankDetail()
        fetchSpinnersDataFromDB()
        //Show empty view if this applicant details not required...
        shouldShowEmptyView()
    }

    private fun initViews() {
    }

    private fun setOnClickListeners() {
        binding.vwAdd.setOnClickListener { showBankDetailFormDialog(BankDetailDialogFragment.Action.NEW) }
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

    private fun setBankDetailAdapter(bankDetailList: ArrayList<BankDetailBean>) {
        binding.rcBank.layoutManager = LinearLayoutManager(mContext , RecyclerView.VERTICAL , false)
        bankAdapter = BankDetailAdapter(mContext , bankDetailList)
        binding.rcBank.adapter = bankAdapter
        bankAdapter?.setOnItemClickListener(this)
    }

    private fun fetchLeadBankDetail() {
        LeadMetaData.getLeadObservable().observe(this@BankDetailFormFragment , Observer {
            it?.let { leadDetails ->
                val selectedApplicantBankDetails = leadDetails.bankData.bankDetailList.filter { bankDetail -> bankDetail.leadApplicantNumber.equals(selectedApplicant?.leadApplicantNumber , true) }
                if (selectedApplicantBankDetails.isNotEmpty())
                    setBankDetailAdapter(selectedApplicantBankDetails[0].applicantBankDetailsBean)
                else setBankDetailAdapter(ArrayList())
            }
        })
    }

    private fun fetchSpinnersDataFromDB() {
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(viewLifecycleOwner , Observer { masterDrownDownValues ->
            masterDrownDownValues?.let {
                allMasterDropDown = it
            }
        })
    }

    private fun showBankDetailFormDialog(action: BankDetailDialogFragment.Action , bankDetail: BankDetailBean? = null) {
        allMasterDropDown?.let { BankDetailDialogFragment.newInstance(action , this@BankDetailFormFragment , it , bankDetail).show(fragmentManager , "Bank Detail") }
    }

    private fun showBankDetailConfirmDeleteDialog() {
        val deleteDialogView = LayoutInflater.from(activity).inflate(R.layout.delete_dialog , null)
        val mBuilder = AlertDialog.Builder(mContext)
                .setView(deleteDialogView)
                .setTitle("Delete Bank Detail")
        val deleteDialog = mBuilder.show()
        deleteDialogView.tvDeleteConfirm.setOnClickListener {
            onDeleteBankDetail()
            deleteDialog.dismiss()
        }
        deleteDialogView.tvDonotDelete.setOnClickListener { deleteDialog.dismiss() }
    }

    override fun onBankDetailDeleteClicked(position: Int) {
        selectedBankDetailPosition = position
        showBankDetailConfirmDeleteDialog()
    }

    override fun onBankDetailEditClicked(position: Int , bank: BankDetailBean) {
        selectedBankDetailPosition = position
        showBankDetailFormDialog(BankDetailDialogFragment.Action.EDIT , bank)
    }

    override fun onSaveBankDetail(bankDetail: BankDetailBean) {
        bankAdapter?.addItem(bankDetail = bankDetail)
    }

    override fun onEditBankDetail(bankDetail: BankDetailBean) {
        bankAdapter?.updateItem(selectedBankDetailPosition , bankDetail)
    }

    override fun onUploadBankDetailClicked(position: Int , bank: BankDetailBean) {
        context?.let {
            allMasterDropDown?.let { dropDown ->
                val docCodeID = dropDown.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.BANK_STATEMENT , true) }
                val bundle = Bundle()
                bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)//Hardcoded for KYC proof...
                bundle.putString(Constants.KEY_TITLE , "Bank")
                bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicant?.leadApplicantNumber.toString())
                bundle.putString(Constants.KEY_FORM_ID , bank.applicationDocumentID)
                DocumentUploadingActivity.startActivity(it , bundle)
            }
        }
    }

    private fun onDeleteBankDetail() {
        bankAdapter?.deleteItem(selectedBankDetailPosition)
    }

    fun isBankDetailRequiredForApplicant(): Boolean {
        selectedApplicant?.let { applicant -> applicant.incomeConsidered?.let { return it } }
        return false
    }

    fun isBankDetailAvailable(): Boolean {
        selectedApplicant?.let {
            bankAdapter?.let { adapter ->
                return adapter.getItemList().size > 0
            }
        }
        return false
    }

    fun getApplicantBankDetails(): BankDetailModel {
        val bankDetailModel = BankDetailModel()
        selectedApplicant?.let { applicant ->
            bankDetailModel.firstName = applicant.firstName
            bankDetailModel.leadApplicantNumber = applicant.leadApplicantNumber
            bankDetailModel.isMainApplicant = applicant.isMainApplicant
            bankDetailModel.applicantBankDetailsBean = bankAdapter?.getItemList() ?: ArrayList()
        }
        return bankDetailModel
    }

}
