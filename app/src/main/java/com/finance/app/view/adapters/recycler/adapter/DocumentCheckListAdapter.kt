package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemDocumentChecklistBinding
import com.finance.app.persistence.model.DocumentCheckListDetailModel
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.view.customViews.CustomSpinnerView

class DocumentCheckListAdapter(private val mContext: Context, private val documentList: ArrayList<DocumentCheckListDetailModel>?) : RecyclerView.Adapter<DocumentCheckListAdapter.DocumentCheckListViewHolder>() {
    private lateinit var binding: ItemDocumentChecklistBinding
    private lateinit var selection: CustomSpinnerView<DropdownMaster>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentCheckListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_document_checklist, parent, false)
        return DocumentCheckListViewHolder(binding, mContext)
    }

    override fun getItemCount() = documentList?.size ?: 0

    override fun onBindViewHolder(holder: DocumentCheckListViewHolder, position: Int) {
        holder.bindItems(position)
    }

    inner class DocumentCheckListViewHolder(val binding: ItemDocumentChecklistBinding, val mContext: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(position: Int) {
            //selection = CustomSpinnerView(mContext = mContext, dropDowns = dropDowns, label = "Select *")
 //           binding.layoutDocumentStatus.addView(selection)
        }
    }
}
