package com.finance.app.view.dialogs

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.finance.app.R
import com.finance.app.databinding.DialogReferenceDetailsBinding
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.persistence.model.ReferenceModel
import com.finance.app.persistence.model.StatesMaster
import com.finance.app.utility.ShowAsMandatory
import com.finance.app.view.adapters.recycler.spinner.MasterSpinnerAdapter
import kotlinx.android.synthetic.main.dialog_bank_detail_form.*
import kotlinx.android.synthetic.main.layout_zip_address.view.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.AppUtils
import javax.inject.Inject

class ReferenceDetailDialogFragment : DialogFragment() {
    enum class Action {
        NEW,
        EDIT,
        SUBMITTED
    }

    @Inject
    lateinit var formValidation: FormValidation

    private lateinit var binding: DialogReferenceDetailsBinding

    private lateinit var action: Action
    private var referenceDetail: ReferenceModel? = null
    private lateinit var allMasterDropDown: AllMasterDropDown

    private lateinit var dialogCallback: OnReferenceDetailDialogCallback

    companion object {
        fun newInstance(action: Action, dialogCallback: OnReferenceDetailDialogCallback, allMasterDropDown: AllMasterDropDown, referenceModel: ReferenceModel? = null): ReferenceDetailDialogFragment {
            val fragment = ReferenceDetailDialogFragment()
            fragment.action = action
            fragment.dialogCallback = dialogCallback
            fragment.allMasterDropDown = allMasterDropDown
            fragment.referenceDetail = referenceModel
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ArchitectureApp.instance.component.inject(this)
        isCancelable=false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_reference_details, container, false)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setOnClickListeners()
        checkActionType()
    }

    private fun initViews() {
        ShowAsMandatory(binding.inputLayoutName)
        ShowAsMandatory(binding.referenceAddressLayout.inputLayoutLandmark)
        ShowAsMandatory(binding.referenceAddressLayout.inputLayoutContact)
//        ShowAsMandatory(binding.inputLayoutKnownSince)

        binding.spinnerRelation.adapter = MasterSpinnerAdapter(context!!, allMasterDropDown.ReferenceRelationship!!)
        binding.spinnerOccupation.adapter = MasterSpinnerAdapter(context!!, allMasterDropDown.OccupationType!!)
        activity?.let { binding.referenceAddressLayout.customPermanentZipAddressView.attachActivity(it) }
    }

    private fun setOnClickListeners() {
        binding.ivClose.setOnClickListener { dismiss() }
        binding.btnAdd.setOnClickListener {
            if (formValidation.validateReference(binding)) {
                if (action == Action.NEW) dialogCallback.onSaveBankDetail(getFilledReferenceDetails())
                else if (action == Action.EDIT) dialogCallback.onEditBankDetail(getFilledReferenceDetails())
                dismiss()
            } else AppUtils.showToast(context, getString(R.string.validation_error))
        }
    }

    private fun checkActionType() {
        if (action == Action.EDIT) {
            btnAdd.text = getString(R.string.update)
            referenceDetail?.let { setFormDetails(it) }
        }
    }

    private fun setFormDetails(referenceDetail: ReferenceModel) {
        allMasterDropDown.ReferenceRelationship?.forEachIndexed { index, dropdownMaster ->
            if (dropdownMaster.typeDetailID == referenceDetail.relationTypeDetailID) {
                binding.spinnerRelation.setSelection(index + 1)
                return@forEachIndexed
            }
        }

        allMasterDropDown.OccupationType?.forEachIndexed { index, dropdownMaster ->
            if (dropdownMaster.typeDetailID == referenceDetail.occupationTypeDetailID) {
                binding.spinnerOccupation.setSelection(index + 1)
                return@forEachIndexed
            }
        }

        binding.etName.setText(referenceDetail.name)
        binding.etKnownSince.setText(referenceDetail.knowSince)
        binding.referenceAddressLayout.etAddress.setText(referenceDetail.addressBean?.address1)
        binding.referenceAddressLayout.etLandmark.setText(referenceDetail.addressBean?.landmark)
        binding.referenceAddressLayout.etContactNum.setText(referenceDetail.contactNumber)
        binding.referenceAddressLayout.customPermanentZipAddressView.etCurrentPinCode.setText(referenceDetail.addressBean?.zip)
    }

    private fun getFilledReferenceDetails(): ReferenceModel {
        val referenceModel = ReferenceModel()

        val relation = binding.spinnerRelation.selectedItem as DropdownMaster?
        val occupation = binding.spinnerOccupation.selectedItem as DropdownMaster?
        val cCity = binding.referenceAddressLayout.customPermanentZipAddressView.spinnerCurrentCity.selectedItem as Response.CityObj?
        val cDistrict = binding.referenceAddressLayout.customPermanentZipAddressView.spinnerCurrentDistrict.selectedItem as Response.DistrictObj?
        val cState = binding.referenceAddressLayout.customPermanentZipAddressView.spinnerCurrentState.selectedItem as StatesMaster?

        referenceModel.name = binding.etName.text.toString()
        referenceModel.knowSince = binding.etKnownSince.text.toString()

        referenceModel.relationTypeDetailID = relation?.typeDetailID
        referenceModel.relationTypeName = relation?.typeDetailCode
        referenceModel.occupationTypeDetailID = occupation?.typeDetailID
        referenceModel.occupationTypeName = occupation?.typeDetailCode


        referenceModel.addressBean?.cityID = cCity?.cityID
        referenceModel.addressBean?.cityName = cCity?.cityName
        referenceModel.addressBean?.districtID = cDistrict?.districtID
        referenceModel.addressBean?.districtName = cDistrict?.districtName
        referenceModel.addressBean?.stateName = cState?.stateName
        referenceModel.addressBean?.address1 = binding.referenceAddressLayout.etAddress.text.toString()
        referenceModel.addressBean?.landmark = binding.referenceAddressLayout.etLandmark.text.toString()
        referenceModel.addressBean?.zip = binding.referenceAddressLayout.customPermanentZipAddressView.etCurrentPinCode.text.toString()
        referenceModel.contactNumber = binding.referenceAddressLayout.etContactNum.text.toString()

        return referenceModel
    }

    interface OnReferenceDetailDialogCallback {
        fun onSaveBankDetail(referenceModel: ReferenceModel)
        fun onEditBankDetail(referenceModel: ReferenceModel)
    }

}