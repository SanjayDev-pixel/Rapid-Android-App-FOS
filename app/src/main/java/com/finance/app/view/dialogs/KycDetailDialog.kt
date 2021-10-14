package com.finance.app.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.finance.app.R
import com.finance.app.databinding.DialogBankDetailFormBinding
import com.finance.app.databinding.DialogKycDetailBinding
import com.finance.app.persistence.model.KycListModel
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.development.interfaces.FormValidation
import javax.inject.Inject

class KycDetailDialog : DialogFragment() {

    enum class Action {
        NEW,
        CLOSE ,
        MOVE;
    }

    @Inject
    lateinit var formValidation: FormValidation

    private lateinit var binding:DialogKycDetailBinding

    private lateinit var action: KycDetailDialog.Action
    private var kycDetail: KycListModel? = null


    private lateinit var dialogCallback: OnKycDetailDialogCallback

    companion object {
        private const val SALARIED = 93
        fun newInstance(action: KycDetailDialog.Action , dialogCallback: OnKycDetailDialogCallback , kycDetail: KycListModel? = null): KycDetailDialog {
            val fragment = KycDetailDialog()
            fragment.action = action
            fragment.dialogCallback = dialogCallback
            fragment.kycDetail = kycDetail
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ArchitectureApp.instance.component.inject(this)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater , R.layout.dialog_kyc_detail , container , false)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)


        initViews()
        setOnClickListeners()
        checkActionType()
    }

    private fun checkActionType() {
       //
    }

    private fun setOnClickListeners() {
       //
    }

    private fun initViews() {
      //
    }

    interface OnKycDetailDialogCallback {
        fun onMoveKycDetail(kycDetail: KycListModel?)

    }


}