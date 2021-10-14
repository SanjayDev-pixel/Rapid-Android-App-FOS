package com.finance.app.view.adapters.recycler.holder

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.databinding.PreviewLayoutBankBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.BankDetailBean
import com.finance.app.persistence.model.BankDetailModel
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.adapters.recycler.adapter.PreviewInnerAdapter
import com.finance.app.viewModel.AppDataViewModel

class PreviewBankHolder(val binding: PreviewLayoutBankBinding, val mContext: FragmentActivity)
    : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(list: ArrayList<BankDetailModel>?, pos: Int, viewModel: AppDataViewModel) {

        if (!list.isNullOrEmpty()) {

            try {
                setUpInnerRecyclerView(list[pos].applicantBankDetailsBean, viewModel)
            }catch (e : IndexOutOfBoundsException ){


            }

            handleCollapse()
            //binding.tvCoApplicant.setText(LeadMetaData.getLeadData()?.personalData!!.applicantDetails[pos].firstName)
            var applicantType :String = ""
            if(LeadMetaData.getLeadData()?.personalData!!.applicantDetails[pos].isMainApplicant==true){
                applicantType =  "Applicant"
            }else{ applicantType="Co-Applicant"}
            binding.tvCoApplicant.setText(applicantType.plus("   : ").plus(LeadMetaData.getLeadData()?.personalData!!.applicantDetails[pos].firstName))
        }
    }

    private fun setUpInnerRecyclerView(data: ArrayList<BankDetailBean>, viewModel: AppDataViewModel) {
        val previewInnerAdapter = PreviewInnerAdapter(mContext,
                dataList = data, viewModel = viewModel, previewTypeEnums = AppEnums.PreviewType.BANK)
             binding.rcBank.adapter = previewInnerAdapter

    }

    private fun handleCollapse() {
        binding.collapseForm.setOnClickListener {
            binding.cardApplicant.visibility = View.GONE
            binding.collapseForm.visibility = View.GONE
            binding.expandForm.visibility = View.VISIBLE
        }

        binding.expandForm.setOnClickListener {
            binding.cardApplicant.visibility = View.VISIBLE
            binding.collapseForm.visibility = View.VISIBLE
            binding.expandForm.visibility = View.GONE
        }
    }
}