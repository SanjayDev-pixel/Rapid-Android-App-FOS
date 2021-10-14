package com.finance.app.view.adapters.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.*
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.view.adapters.recycler.holder.*
import com.finance.app.viewModel.AppDataViewModel

class PreviewAdapter(private val mContext: FragmentActivity, private val lead: AllLeadMaster,
                     private val screenNameEnums: AppEnums.ScreenLoanApp, val viewModel: AppDataViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var loanPreviewBinding: PreviewLayoutLoanBinding
    private lateinit var personalPreviewBinding: PreviewLayoutPersonalBinding
    private lateinit var employmentPreviewBinding: PreviewLayoutEmploymentBinding
    private lateinit var bankPreviewBinding: PreviewLayoutBankBinding
    private lateinit var assetsLiabilityPreviewBinding: PreviewLayoutAssetsLiabilityBinding
    private lateinit var propertyPreviewBinding: PreviewLayoutPropertyBinding
    private lateinit var referencePreviewBinding: PreviewLayoutReferenceBinding
    private lateinit var documentsPreviewBinding: PreviewLayoutDocumentChecklistBinding

    override fun getItemCount(): Int {
        return when (screenNameEnums) {
            AppEnums.ScreenLoanApp.LOAN_INFORMATION -> 1
            AppEnums.ScreenLoanApp.REFERENCE -> 1
            AppEnums.ScreenLoanApp.PROPERTY -> 1
            else -> lead.personalData.applicantDetails.size
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        return when (screenNameEnums) {
            AppEnums.ScreenLoanApp.LOAN_INFORMATION -> {
                loanPreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_layout_loan, parent, false)
                PreviewLoanHolder(loanPreviewBinding, mContext)
            }
            AppEnums.ScreenLoanApp.PERSONAL -> {
                personalPreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_layout_personal, parent, false)
                PreviewPersonalHolder(personalPreviewBinding, mContext)
            }
            AppEnums.ScreenLoanApp.EMPLOYMENT -> {
                employmentPreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_layout_employment, parent, false)
                PreviewEmploymentHolder(employmentPreviewBinding, mContext)
            }
            AppEnums.ScreenLoanApp.BANK_DETAIL -> {
                bankPreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_layout_bank, parent, false)
                PreviewBankHolder(bankPreviewBinding, mContext)
            }
            AppEnums.ScreenLoanApp.LIABILITY_AND_ASSET -> {
                assetsLiabilityPreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_layout_assets_liability, parent, false)
                PreviewAssetsLiabilityHolder(assetsLiabilityPreviewBinding, mContext)
            }
            AppEnums.ScreenLoanApp.REFERENCE -> {
                referencePreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_layout_reference, parent, false)
                PreviewReferenceHolder(referencePreviewBinding, mContext)
            }
            AppEnums.ScreenLoanApp.PROPERTY -> {
                propertyPreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_layout_property, parent, false)
                PreviewPropertyHolder(propertyPreviewBinding, mContext)
            }

            AppEnums.ScreenLoanApp.DOCUMENT_CHECKLIST -> {
                documentsPreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_layout_document_checklist, parent, false)
                PreviewDocumentsHolder(documentsPreviewBinding, mContext)

            }
            AppEnums.ScreenLoanApp.DEFAULT -> {
                personalPreviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.preview_layout_personal, parent, false)
                PreviewPersonalHolder(personalPreviewBinding, mContext)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (screenNameEnums) {
            AppEnums.ScreenLoanApp.LOAN_INFORMATION -> initLayoutLoanPreview(holder as PreviewLoanHolder, position)
            AppEnums.ScreenLoanApp.PERSONAL -> initLayoutPersonalPreview(holder as PreviewPersonalHolder, position)
            AppEnums.ScreenLoanApp.EMPLOYMENT -> initLayoutEmploymentPreview(holder as PreviewEmploymentHolder, position)
            AppEnums.ScreenLoanApp.BANK_DETAIL -> initLayoutBankPreview(holder as PreviewBankHolder, position)
            AppEnums.ScreenLoanApp.LIABILITY_AND_ASSET -> initLayoutAssetsLiabilityPreview(holder as PreviewAssetsLiabilityHolder, position)
            AppEnums.ScreenLoanApp.REFERENCE -> initLayoutReferencePreview(holder as PreviewReferenceHolder, position)
            AppEnums.ScreenLoanApp.PROPERTY -> initLayoutPropertyPreview(holder as PreviewPropertyHolder, position)
            AppEnums.ScreenLoanApp.DOCUMENT_CHECKLIST -> initLayoutDocumentPreview(holder as PreviewDocumentsHolder, position)
            AppEnums.ScreenLoanApp.DEFAULT -> initLayoutLoanPreview(holder as PreviewLoanHolder, position)
        }
    }

    private fun initLayoutLoanPreview(holder: PreviewLoanHolder, pos: Int) {
        holder.bindItems(lead.loanData, viewModel)
    }

    private fun initLayoutPersonalPreview(holder: PreviewPersonalHolder, pos: Int) {
        holder.bindItems(lead.personalData.applicantDetails, pos, viewModel)
    }

    private fun initLayoutEmploymentPreview(holder: PreviewEmploymentHolder, pos: Int) {
        holder.bindItems(lead.employmentData.applicantDetails, pos, viewModel)
    }

    private fun initLayoutBankPreview(holder: PreviewBankHolder, pos: Int) {

        holder.bindItems(lead.bankData.bankDetailList, pos, viewModel)

    }

    private fun initLayoutAssetsLiabilityPreview(holder: PreviewAssetsLiabilityHolder, pos: Int) {
        holder.bindItems(lead.assetLiabilityData.loanApplicationObj, pos, viewModel)
    }

    private fun initLayoutPropertyPreview(holder: PreviewPropertyHolder, pos: Int) {
        holder.bindItems(lead.propertyData, pos, viewModel)
    }

    private fun initLayoutReferencePreview(holder: PreviewReferenceHolder, pos: Int) {
        holder.bindItems(lead.referenceData.loanApplicationObj, pos, viewModel)
    }

    private fun initLayoutDocumentPreview(holder: PreviewDocumentsHolder, pos: Int) {
        holder.bindItems(lead.documentData.documentDetailList, pos, viewModel)
    }

}