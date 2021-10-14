package com.finance.app.view.adapters.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.*
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.*
import com.finance.app.view.adapters.recycler.holder.*
import com.finance.app.viewModel.AppDataViewModel

class PreviewInnerAdapter<T>(private val mContext: FragmentActivity, private val dataList: ArrayList<T>,
                             private val previewTypeEnums: AppEnums.PreviewType, val viewModel: AppDataViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var bankPreviewBinding: PreviewInnerLayoutBankBinding
    private lateinit var assetsPreviewBinding: PreviewInnerLayoutAssetBinding
    private lateinit var cardsPreviewBinding: PreviewInnerLayoutCreditCardBinding
    private lateinit var obligationPreviewBinding: PreviewInnerLayoutObligationBinding
    private lateinit var documentsPreviewBinding: PreviewDocumentChecklistItemBinding

    override fun getItemCount() = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        return when (previewTypeEnums) {
            AppEnums.PreviewType.BANK -> {
                bankPreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_inner_layout_bank, parent, false)
                PreviewInnerBankHolder(bankPreviewBinding, mContext)
            }
            AppEnums.PreviewType.ASSETS -> {
                assetsPreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_inner_layout_asset, parent, false)
                PreviewInnerAssetHolder(assetsPreviewBinding, mContext)
            }
            AppEnums.PreviewType.CARD -> {
                cardsPreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_inner_layout_credit_card, parent, false)
                PreviewInnerCardHolder(cardsPreviewBinding, mContext)
            }
            AppEnums.PreviewType.OBLIGATION -> {
                obligationPreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_inner_layout_obligation, parent, false)
                PreviewInnerObligationHolder(obligationPreviewBinding, mContext)
            }
            AppEnums.PreviewType.DOCUMENT -> {
                documentsPreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_document_checklist_item, parent, false)
                PreviewInnerDocumentHolder(documentsPreviewBinding, mContext)
            }
            else -> {
                //documentsPreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_document_checklist_item, parent, false)
                PreviewInnerDocumentHolder(documentsPreviewBinding, mContext)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (previewTypeEnums) {
            AppEnums.PreviewType.BANK -> initLayoutInnerBankPreview(holder as PreviewInnerBankHolder, position)
            AppEnums.PreviewType.ASSETS -> initLayoutInnerAssetsPreview(holder as PreviewInnerAssetHolder, position)
            AppEnums.PreviewType.CARD -> initLayoutInnerCreditCardPreview(holder as PreviewInnerCardHolder, position)
            AppEnums.PreviewType.OBLIGATION -> initLayoutInnerObligationPreview(holder as PreviewInnerObligationHolder, position)
            AppEnums.PreviewType.DOCUMENT -> initLayoutInnerDocumentPreview(holder as PreviewInnerDocumentHolder, position)
            else -> initLayoutInnerBankPreview(holder as PreviewInnerBankHolder, position)
        }
    }

    private fun initLayoutInnerBankPreview(holder: PreviewInnerBankHolder, pos: Int) {
        val data = dataList as ArrayList<BankDetailBean>
        holder.bindItems(data, pos, viewModel)
    }

    private fun initLayoutInnerAssetsPreview(holder: PreviewInnerAssetHolder, pos: Int) {
        val data = dataList as ArrayList<AssetLiability>
        holder.bindItems(data, pos, viewModel)
    }

    private fun initLayoutInnerCreditCardPreview(holder: PreviewInnerCardHolder, pos: Int) {
        val data = dataList as ArrayList<CardDetail>
        holder.bindItems(data, pos, viewModel)
    }

    private fun initLayoutInnerObligationPreview(holder: PreviewInnerObligationHolder, pos: Int) {
        val data = dataList as ArrayList<ObligationDetail>
        holder.bindItems(data, pos, viewModel)
    }

    private fun initLayoutInnerDocumentPreview(holder: PreviewInnerDocumentHolder, pos: Int) {
        val data = dataList as ArrayList<DocumentCheckListDetailModel>
        holder.bindItems(data, pos, viewModel)
    }

}