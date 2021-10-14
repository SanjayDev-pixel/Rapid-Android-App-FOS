package com.finance.app.view.adapters.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemUplodedDocumentBinding
import com.finance.app.persistence.model.DocumentTypeModel

class UploadedDocumentListAdapter(private val documentList: ArrayList<DocumentTypeModel>) : RecyclerView.Adapter<UploadedDocumentListAdapter.UploadedDocumentViewHolder>() {

    private var itemClickListener: ItemClickListener? = null

    interface ItemClickListener {
        fun onKycDetailDeleteClicked(position: Int, documentTypeModel: DocumentTypeModel)
        fun onKycDetailDownloadClicked(position: Int, documentTypeModel: DocumentTypeModel)
    }

    inner class UploadedDocumentViewHolder(val binding: ItemUplodedDocumentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(position: Int, documentTypeModel: DocumentTypeModel) {
            binding.tvDocType.text = documentTypeModel.documentSubTypeDetailDisplayText
            binding.tvDocName.text = documentTypeModel.documentName

            addClickListener(position, documentTypeModel)
        }

        private fun addClickListener(position: Int, documentTypeModel: DocumentTypeModel) {
            binding.ivDownload.setOnClickListener {
                itemClickListener?.onKycDetailDownloadClicked(position, documentTypeModel)
            }
        }
    }

    fun setOnItemClickListener(listener: ItemClickListener) {
        itemClickListener = listener
    }

    override fun getItemCount() = documentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadedDocumentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UploadedDocumentViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.item_uploded_document, parent, false))
    }


    override fun onBindViewHolder(holder: UploadedDocumentViewHolder, position: Int) {
        holder.bindItems(position, documentList[position])
    }


}