package com.finance.app.view.fragment.loanApplicationFragments.document_checklist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.finance.app.R
import com.finance.app.databinding.LayoutDocumentChecklistFormBinding
import com.finance.app.persistence.model.DocumentCheckList
import com.finance.app.persistence.model.DocumentCheckListModel
import com.finance.app.persistence.model.PersonalApplicantsModel
import kotlinx.android.synthetic.main.activity_upload_document.*
import kotlinx.android.synthetic.main.asset_liability_fragment_form.*
import kotlinx.android.synthetic.main.layout_document_checklist_form.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.customAppComponents.activity.BaseFragment
import motobeans.architecture.development.interfaces.DataBaseUtil
import javax.inject.Inject


class DocumentChecklistForm : BaseFragment() {


    @Inject
    lateinit var dataBase: DataBaseUtil
    private lateinit var binding: LayoutDocumentChecklistFormBinding
    private var selectedApplicant: PersonalApplicantsModel? = null

    companion object {
        fun newInstance(applicant: PersonalApplicantsModel): DocumentChecklistForm {
            val fragment = DocumentChecklistForm()
            fragment.selectedApplicant = applicant
            return fragment
        }

    }

    override fun init() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ArchitectureApp.instance.component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater, container, R.layout.layout_document_checklist_form)
        initViews()
        setOnClickListener()
        return binding.root
    }


    private fun initViews() {

    }

    private fun setOnClickListener() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { selectedApplicant?.let { it1 -> binding.customDocumentchecklistView.initApplicantDetails(it, it1) } }
    }
    fun getDocumentChecklist() = binding.customDocumentchecklistView.getDocumentChecklist()
    fun isDocumentDetailsValid() = binding.customDocumentchecklistView.isDocumentDetailsValid()


}
