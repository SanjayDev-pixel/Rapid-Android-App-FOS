package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemDocumentChecklistBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.ChecklistAnswerType
import com.finance.app.persistence.model.DocumentCheckListDetailModel
import com.finance.app.utility.LeadMetaData

class CheckListAdapter(private val c: Context, private val documentCheckList: ArrayList<DocumentCheckListDetailModel>) : RecyclerView.Adapter<CheckListAdapter.CheckListDetailViewHolder>() {


    private lateinit var binding: ItemDocumentChecklistBinding
    private var mOnCheckClickListener: CheckListClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListDetailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_document_checklist, parent, false)
        return CheckListDetailViewHolder(binding, c)
    }

    override fun getItemCount() = documentCheckList.size


    fun setOnCheckListClickListener(listner: CheckListClickListener) {
        mOnCheckClickListener = listner
    }

    interface CheckListClickListener {

    }

    fun getItemList(): ArrayList<DocumentCheckListDetailModel> {
        return documentCheckList
    }

    override fun onBindViewHolder(holder: CheckListDetailViewHolder, position: Int) {

        holder.bindItems(position, documentCheckList[position])

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class CheckListDetailViewHolder(val binding: ItemDocumentChecklistBinding, val c: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bindItems(position: Int, documentCheck: DocumentCheckListDetailModel) {
            System.out.println("TypeIDDDDocument>>>>"+documentCheck.typeDetailId)
            //  binding.tvValue.text = documentCheck.assetValue.toString()

            binding.questionNo.text = (position + 1).toString()
            binding.questiontext.text = documentCheck.description
            binding.radiogroup.tag = position
            //  binding.questiontext.text=documentCheck.
            when {
                documentCheck.selectedCheckListValue == ChecklistAnswerType.YES || documentCheck.typeDetailDisplayText.equals("YES", true) -> binding.radiogroup.check(binding.rbYes.id)
                documentCheck.selectedCheckListValue == ChecklistAnswerType.NO || documentCheck.typeDetailDisplayText.equals("NO", true) -> binding.radiogroup.check(binding.rbNo.id)
                documentCheck.selectedCheckListValue == ChecklistAnswerType.NA || documentCheck.typeDetailDisplayText.equals("NA", true) -> binding.radiogroup.check(binding.rbNa.id)
            }
            LeadMetaData.getLeadData()?.let {
                if (it.status.equals(AppEnums.LEAD_TYPE.SUBMITTED.type , true)) {
                    disableDocumentChecklist()
                } else {
                    addClickListener(position , documentCheck)
                }
            }


        }

        private fun addClickListener(position: Int, documentCheck: DocumentCheckListDetailModel) {
            binding.radiogroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

                    when (checkedId) {

                        binding.rbYes.id -> documentCheck.selectedCheckListValue = ChecklistAnswerType.YES
                        binding.rbNo.id -> documentCheck.selectedCheckListValue = ChecklistAnswerType.NO
                        binding.rbNa.id -> documentCheck.selectedCheckListValue = ChecklistAnswerType.NA
                    }
                }
            })
            System.out.println("SelctedValue>>>>"+documentCheck.selectedCheckListValue)

        }
    }
    private fun disableDocumentChecklist(){
        binding.rbYes.isEnabled = false
        binding.rbNa.isEnabled = false
        binding.rbNo.isEnabled = false
    }
}
