package com.finance.app.view.adapters.recycler.holder

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemLeadsBinding
import com.finance.app.others.AppEnums
import com.finance.app.others.setTextVertically
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.utility.ConvertDate
import com.finance.app.view.activity.ApplicantKycListActivity
import com.finance.app.view.activity.LeadDetailActivity

class LeadsListingHolder(val binding: ItemLeadsBinding, val mContext: Context) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bindItems(lead: AllLeadMaster, adapterFor: AppEnums.LEAD_TYPE?) {
        if (adapterFor == AppEnums.LEAD_TYPE.ALL) {
            binding.tvStatusLine.setTextVertically(lead.status)
        }

        binding.tvLeadName.text = lead.applicantFirstName
        binding.tvLeadID.text = ": ${lead.leadID.toString()}"
        binding.tvLoanType.text = ": ${lead.loanProductName}"
        binding.tvCreatedDate.text = ": ${lead.createdOn}"
        binding.tvUpdatedDate.text = lead.lastModifiedOn
        binding.tvLeadnumber.text = ": ${lead.leadNumber}"
        binding.tvAmountValue.text = lead.amountRequest?.let { ": Rs. $it" } ?: kotlin.run { ": Rs. N/A" }
        when (lead.status) {
            AppEnums.LEAD_TYPE.NEW.type -> binding.tvStatusLine.setBackgroundColor(mContext.resources.getColor(R.color.lead_status_new))
            AppEnums.LEAD_TYPE.SUBMITTED.type -> binding.tvStatusLine.setBackgroundColor(mContext.resources.getColor(R.color.lead_status_submitted))
            AppEnums.LEAD_TYPE.PENDING.type -> binding.tvStatusLine.setBackgroundColor(mContext.resources.getColor(R.color.lead_status_pending))
            AppEnums.LEAD_TYPE.REJECTED.type -> binding.tvStatusLine.setBackgroundColor(mContext.resources.getColor(R.color.lead_status_rejected))
            else -> binding.tvStatusLine.setBackgroundColor(mContext.resources.getColor(R.color.lead_status_extra))
        }
        if(lead.status == "Submitted"){
            binding.btnperformKyc.visibility = View.VISIBLE
        }
        else
        {
            binding.btnperformKyc.visibility = View.GONE
        }
        binding.leadCard.setOnClickListener {
            LeadDetailActivity.start(mContext, lead)
        }
        binding.btnperformKyc.setOnClickListener{
            val intent = Intent(mContext,ApplicantKycListActivity::class.java)
            intent.putExtra("leadId",""+lead.leadID)
            mContext.startActivity(intent)


        }
    }
}