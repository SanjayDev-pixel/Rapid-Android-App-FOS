package com.finance.app

import android.content.Context
import android.nfc.Tag
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.databinding.ReportRecyclerRowDataBinding
import motobeans.architecture.retrofit.response.Response
import kotlin.math.log

class ReportAdapter(private val mContext : Context,private val data:ArrayList<ResponseReportData>) : RecyclerView.Adapter<ReportAdapter.ReportHolder>() {
    private  lateinit var binding:ReportRecyclerRowDataBinding
    private  var items=ArrayList<ResponseReportData>()

    init {
        items.addAll(data)
    }




    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ReportHolder {
        val layoutInflator= LayoutInflater.from(parent.context)
        binding= DataBindingUtil.inflate(layoutInflator,R.layout.report_recycler_row_data,parent,false)
        return ReportHolder(binding,mContext)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ReportHolder , position: Int) {
        holder.bindItems(items[position])
    }
    inner class ReportHolder (var binding:ReportRecyclerRowDataBinding,val c:Context):RecyclerView.ViewHolder(binding.root){
        private lateinit var items: ResponseReportData
        fun bindItems(items:ResponseReportData) {
            this.items=items
        //  binding.tvLeadIDvalue.text= items.leadId.toString()
            binding.tvLeadNovalue.text=items.leadNumber
            binding.tvApplicantFirstNamevalue.text=items.applicantFirstName
if(items.applicantMiddleName.equals("")){
    binding.tvApplicantMiddleName.visibility=View.GONE
    binding.tvApplicantMiddleNamevalue.visibility=View.GONE
}else{
         binding.tvApplicantMiddleNamevalue.text=items.applicantMiddleName}

            binding.tvApplicantLastNamevalue.text=items.applicantLastName
            binding.tvBranchNamevalue.text=items.branchName
            binding.tvAssignedToUservalue.text=items.assignedToUser
            binding.tvLoanProductNamevalue.text=items.loanProductName
            binding.tvLoanStatusvalue.text=items.loanStatus
            binding.tvLoanPurposevalue.text=items.loanPurposeName
            binding.tvLoanSchemevalue.text=items.loanScheme
            binding.tvPropertySelectedvalue.text= items.isPropertySelected.toString()



            //sara data yahi me set kar do okay sir
            /*   binding.tvReportApplicationNo.text=items.applicationNo
             binding.tvReportBranch.text=items.branch
             binding.tvReportCurrentStatus.text=items.currentStatus
             binding.tvReportNomineeName.text=items.nomineeName*/

        }


    }
}