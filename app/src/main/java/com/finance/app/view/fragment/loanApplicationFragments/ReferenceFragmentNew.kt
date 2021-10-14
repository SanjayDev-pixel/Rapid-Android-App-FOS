package com.finance.app.view.fragment.loanApplicationFragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.FragmentReferenceBinding
import com.finance.app.eventBusModel.AppEvents
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.ReferenceModel
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.adapters.recycler.adapter.ReferenceAdapter
import com.finance.app.view.dialogs.ReferenceDetailDialogFragment
import kotlinx.android.synthetic.main.delete_dialog.view.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.customAppComponents.activity.BaseFragment
import motobeans.architecture.development.interfaces.DataBaseUtil
import java.util.*
import javax.inject.Inject

class ReferenceFragmentNew : BaseFragment(), ReferenceDetailDialogFragment.OnReferenceDetailDialogCallback, ReferenceAdapter.ItemClickListener {

    @Inject
    lateinit var dataBase: DataBaseUtil
    private lateinit var mContext: Context
    private lateinit var binding: FragmentReferenceBinding

    private var referenceAdapter: ReferenceAdapter? = null
    private var allMasterDropDown: AllMasterDropDown? = null

    private var leadDetails: AllLeadMaster? = null
    private var selectedReferenceDetailPosition = -1

    companion object {
        fun newInstance(): ReferenceFragmentNew {
            return ReferenceFragmentNew()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!
        ArchitectureApp.instance.component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater, container, R.layout.fragment_reference)
        binding.lifecycleOwner = this

        initViews()
        setOnClickListeners()
        LeadMetaData.getLeadData()?.let { if(it.status.equals(AppEnums.LEAD_TYPE.SUBMITTED.type,true))
            binding.imageAddReference.visibility = View.GONE
        }


        return view
    }

    override fun init() {
    }

    private fun initViews() {
    }

    private fun setOnClickListeners() {
        binding.vwAdd.setOnClickListener { showReferenceDetailFormDialog(ReferenceDetailDialogFragment.Action.NEW) }
        binding.btnNext.setOnClickListener {


            leadDetails?.let { it.referenceData.loanApplicationObj.let {
                referenceDetails -> LeadMetaData().saveReferenceData(referenceDetails)
                if(it.referenceData.loanApplicationObj.size > 0){
                    AppEvents.fireEventLoanAppChangeNavFragmentNext()}else{
                    Toast.makeText(mContext,"Please fill reference detail", Toast.LENGTH_SHORT).show()}} }


        }
        binding.btnPrevious.setOnClickListener { AppEvents.fireEventLoanAppChangeNavFragmentPrevious() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Now fetch data from where-ever you want....
        fetchLeadDetails()
        fetchSpinnersDataFromDB()
    }

    private fun fetchLeadDetails() {
        LeadMetaData.getLeadObservable().observe(this, Observer { leadDetails ->
            leadDetails?.let {
                this@ReferenceFragmentNew.leadDetails = it
                //Set Reference Details List.
                it.referenceData.loanApplicationObj.let { it1 -> setReferenceDetailAdapter(it1) }
            }
        })
    }

    private fun fetchSpinnersDataFromDB() {
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(viewLifecycleOwner, Observer { masterDrownDownValues ->
            masterDrownDownValues?.let {
                allMasterDropDown = it
            }
        })
    }

    private fun setReferenceDetailAdapter(referenceDetails: ArrayList<ReferenceModel>) {
        binding.rcReference.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        referenceAdapter = ReferenceAdapter(mContext, referenceDetails)
        binding.rcReference.adapter = referenceAdapter
        referenceAdapter?.setOnItemClickListener(this)
        binding.rcReference.visibility = View.VISIBLE
    }

    override fun onReferenceDetailEditClicked(position: Int, referenceDetail: ReferenceModel) {
        selectedReferenceDetailPosition = position
        showReferenceDetailFormDialog(
                if (leadDetails?.status == AppEnums.LEAD_TYPE.SUBMITTED.type) ReferenceDetailDialogFragment.Action.SUBMITTED
                else ReferenceDetailDialogFragment.Action.EDIT, referenceDetail)
    }

    override fun onReferenceDetailDeleteClicked(position: Int) {
        selectedReferenceDetailPosition = position
        showReferenceDetailConfirmDeleteDialog()
    }

    private fun showReferenceDetailFormDialog(action: ReferenceDetailDialogFragment.Action, referenceModel: ReferenceModel? = null) {
        allMasterDropDown?.let { ReferenceDetailDialogFragment.newInstance(action, this@ReferenceFragmentNew, it, referenceModel).show(fragmentManager, "Reference Detail") }
    }

    private fun showReferenceDetailConfirmDeleteDialog() {
        val deleteDialogView = LayoutInflater.from(activity).inflate(R.layout.delete_dialog, null)
        val mBuilder = AlertDialog.Builder(mContext)
                .setView(deleteDialogView)
                .setTitle("Delete Reference Detail")
        val deleteDialog = mBuilder.show()
        deleteDialogView.tvDeleteConfirm.setOnClickListener {
            onDeleteReferenceDetail()
            deleteDialog.dismiss()
        }
        deleteDialogView.tvDonotDelete.setOnClickListener { deleteDialog.dismiss() }
    }

    override fun onSaveBankDetail(referenceModel: ReferenceModel) {
        referenceAdapter?.addItem(referenceDetail = referenceModel)
    }

    override fun onEditBankDetail(referenceModel: ReferenceModel) {
        referenceAdapter?.updateItem(selectedReferenceDetailPosition, referenceModel)
    }

    private fun onDeleteReferenceDetail() {
        referenceAdapter?.deleteItem(selectedReferenceDetailPosition)
    }
}
