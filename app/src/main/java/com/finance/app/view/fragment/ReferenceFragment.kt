package com.finance.app.view.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.FragmentReferenceBinding
import com.finance.app.databinding.LayoutEmploymentAddressBinding
import com.finance.app.eventBusModel.AppEvents
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.*
import com.finance.app.presenter.connector.DistrictCityConnector
import com.finance.app.presenter.connector.PinCodeDetailConnector
import com.finance.app.presenter.presenter.*
import com.finance.app.utility.*
import com.finance.app.view.adapters.recycler.adapter.ReferenceAdapter
import com.finance.app.view.adapters.recycler.spinner.CitySpinnerAdapter
import com.finance.app.view.adapters.recycler.spinner.DistrictSpinnerAdapter
import com.finance.app.view.adapters.recycler.spinner.MasterSpinnerAdapter
import com.finance.app.view.adapters.recycler.spinner.StatesSpinnerAdapter
import com.google.android.material.textfield.TextInputEditText
import fr.ganfra.materialspinner.MaterialSpinner
import kotlinx.android.synthetic.main.delete_dialog.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseFragment
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

class ReferenceFragment {

}

/*
class ReferenceFragment : BaseFragment(), PinCodeDetailConnector.PinCode,
        ReferenceAdapter.ItemClickListener, DistrictCityConnector.District,
        DistrictCityConnector.City {

    @Inject
    lateinit var formValidation: FormValidation
    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    private lateinit var binding: FragmentReferenceBinding
    private lateinit var mContext: Context
    private var mLead: AllLeadMaster? = null
    private lateinit var referenceAdapter: ReferenceAdapter
    private val presenter = Presenter()
    private lateinit var leadIdForApplicant: String
    private val pinCodePresenter = PinCodeDetailPresenter(this)
    private val districtPresenter = DistrictPresenter(this)
    private val cityPresenter = CityPresenter(this)
    private lateinit var allMasterDropDown: AllMasterDropDown
    private lateinit var states: List<StatesMaster>
    private var referenceMaster: ReferenceMaster = ReferenceMaster()
    private var rDraftData = ReferencesList()
    private var referencesList: ArrayList<ReferenceModel>? = ArrayList()
    private var currentReference: ReferenceModel = ReferenceModel()
    private var currentPosition: Int? = null
    private var pinCodeObj: Response.PinCodeObj? = null
    private var mPinCode: String = ""
    private var mStateId: String = ""
    private var mDistrictId: String = ""

    companion object {
        private val leadAndLoanDetail = LeadAndLoanDetail()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater, container, R.layout.fragment_reference)
        init()
        return binding.root
    }

    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        mLead = sharedPreferences.getLeadDetail()
        mContext = context!!
        leadIdForApplicant = mLead!!.leadID.toString()
        SetReferenceMandatoryField(binding)
        getReferenceInfo()
        setClickListeners()
    }

    private fun getReferenceInfo() {
        presenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP, CallGetLoan())
    }

    private fun showData(referenceList: ArrayList<ReferenceModel>?) {
        if (referenceList != null && referenceList.size > 0) {
            setUpAdapterForListing(referenceList)
        }
        getDropDownsFromDB()
        fillFormWithCurrentReference(currentReference)
    }

    private fun setUpAdapterForListing(references: ArrayList<ReferenceModel>?) {
        binding.rcReference.layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
        referenceAdapter = ReferenceAdapter(mContext, references!!)
        binding.rcReference.adapter = referenceAdapter
        referenceAdapter.setOnItemClickListener(this)
        binding.pageIndicatorAsset.attachTo(binding.rcReference)
        binding.pageIndicatorAsset.visibility = View.VISIBLE
        binding.rcReference.visibility = View.VISIBLE
    }

    private fun getDropDownsFromDB() {
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(viewLifecycleOwner, Observer { masterDrownDownValues ->
            masterDrownDownValues?.let {
                allMasterDropDown = masterDrownDownValues
                setMasterDropDownValue(allMasterDropDown)
            }
        })
        dataBase.provideDataBaseSource().statesDao().getAllStates().observe(viewLifecycleOwner, Observer {stateMaster->
            stateMaster?.let{
                states = stateMaster
                setStateDropDownValue()
            }
        })
    }

    private fun fillFormWithCurrentReference(currentReference: ReferenceModel) {
        binding.etName.setText(currentReference.name)
        binding.etKnownSince.setText(currentReference.knowSince)
        binding.referenceAddressLayout.etContactNum.setText(currentReference.contactNumber)
        selectMasterDropdownValue(binding.spinnerRelation, currentReference.relationTypeDetailID)
        selectMasterDropdownValue(binding.spinnerOccupation, currentReference.occupationTypeDetailID)
        fillAddressFields(binding.referenceAddressLayout, currentReference.addressBean)
        checkSubmission()
    }

    private fun checkSubmission() {
        if (mLead!!.status == AppEnums.LEAD_TYPE.SUBMITTED.type) {
            DisableReferenceForm(binding)
        }
    }

    private fun fillAddressFields(binding: LayoutEmploymentAddressBinding, address: ReferenceAddressDetail?) {
        binding.etAddress.setText(address!!.address1)
        binding.etLandmark.setText(address.landmark)
        binding.etPinCode.setText(address.zip)
    }

    private fun setMasterDropDownValue(allMasterDropDown: AllMasterDropDown) {
        binding.spinnerRelation.adapter = MasterSpinnerAdapter(context!!, allMasterDropDown.ReferenceRelationship!!)
        binding.spinnerOccupation.adapter = MasterSpinnerAdapter(context!!, allMasterDropDown.OccupationType!!)
        selectMasterDropdownValue(binding.spinnerRelation, currentReference.relationTypeDetailID)
        selectMasterDropdownValue(binding.spinnerOccupation, currentReference.occupationTypeDetailID)
    }

    private fun selectMasterDropdownValue(spinner: Spinner, id: Int?) {
        for (index in 0 until spinner.count - 1) {
            val obj = spinner.getItemAtPosition(index) as DropdownMaster
            if (obj.typeDetailID == id) {
                spinner.setSelection(index + 1)
                return
            }
        }
    }

    private fun setStateDropDownValue() {
        binding.referenceAddressLayout.spinnerState.adapter = StatesSpinnerAdapter(mContext, states)
        binding.referenceAddressLayout.spinnerCity.adapter = CitySpinnerAdapter(mContext, ArrayList())
        binding.referenceAddressLayout.spinnerDistrict.adapter = DistrictSpinnerAdapter(mContext, ArrayList())
        binding.referenceAddressLayout.spinnerState.adapter = StatesSpinnerAdapter(mContext, states)
        binding.referenceAddressLayout.spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0) {
                    val state = parent.selectedItem as StatesMaster
                    mStateId = state.stateID.toString()
                    districtPresenter.callDistrictApi()
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.btnAddReference.setOnClickListener {
            if (formValidation.validateReference(binding = binding)) {
                saveReference()
                ClearReferenceForm(binding, mContext, allMasterDropDown, states)
                clearPinCodeData()
            }
        }
        binding.btnPrevious.setOnClickListener { AppEvents.fireEventLoanAppChangeNavFragmentPrevious() }
        binding.btnNext.setOnClickListener {
            if (referencesList!!.size > 0 || formValidation.validateReference(binding = binding)) {
                referencesList?.add(getReferenceModel())
                presenter.callNetwork(ConstantsApi.CALL_POST_LOAN_APP, dmiConnector = CallPostLoanApp())
            }
        }
        pinCodeListener(binding.referenceAddressLayout.etPinCode)
    }

    private fun saveReference() {
        if (currentPosition == null) {
            referencesList?.add(getReferenceModel())
        } else referencesList!![currentPosition!!] = getReferenceModel()
        setUpAdapterForListing(referencesList)
    }

    private fun pinCodeListener(pinCodeField: TextInputEditText?) {
        pinCodeField!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (pinCodeField.text!!.length == 6) {
                    mPinCode = pinCodeField.text.toString()
                    pinCodePresenter.callPinCodeDetailApi()
                }
            }
        })
    }

    override val pinCode: String
        get() = mPinCode

    override fun getPinCodeFailure(msg: String) = clearPinCodeData()

    private fun clearPinCodeData() {
        binding.referenceAddressLayout.spinnerState.isEnabled = true
        binding.referenceAddressLayout.spinnerDistrict.isEnabled = true
        binding.referenceAddressLayout.spinnerCity.isEnabled = true
    }

    override fun getPinCodeSuccess(value: Response.ResponsePinCodeDetail, addressType: AppEnums.ADDRESS_TYPE?) {
        if (value.responseObj!!.size > 0) {
            pinCodeObj = value.responseObj[0]
            selectStateValue(binding.referenceAddressLayout.spinnerState)
        } else {
            clearPinCodeData()
        }
    }

    private fun selectStateValue(spinner: MaterialSpinner) {
        for (index in 0 until spinner.count - 1) {
            val obj = spinner.getItemAtPosition(index) as StatesMaster
            if (obj.stateID == pinCodeObj!!.stateID) {
                spinner.setSelection(index + 1)
                mStateId = pinCodeObj!!.stateID.toString()
                districtPresenter.callDistrictApi()
                spinner.isEnabled = false
                return
            }
        }
    }

    override val stateId: String
        get() = mStateId

    override fun getDistrictFailure(msg: String) = showToast(msg)

    override fun getDistrictSuccess(value: Response.ResponseDistrict, addressType: AppEnums.ADDRESS_TYPE?) {
        if (value.responseObj != null && value.responseObj.size > 0) {
            setDistrict(binding.referenceAddressLayout.spinnerDistrict, value)
        }
    }

    private fun setDistrict(spinner: MaterialSpinner, response: Response.ResponseDistrict) {
        spinner.adapter = DistrictSpinnerAdapter(mContext, response.responseObj!!)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0) {
                    val district = parent.selectedItem as Response.DistrictObj
                    mDistrictId = district.districtID.toString()
                    cityPresenter.callCityApi()
                }
            }
        }
        selectDistrictValue(spinner)
    }

    private fun selectDistrictValue(spinner: Spinner) {
        for (index in 0 until spinner.count - 1) {
            val obj = spinner.getItemAtPosition(index) as Response.DistrictObj
            if (obj.districtID == pinCodeObj!!.districtID) {
                spinner.setSelection(index + 1)
                mDistrictId = obj.districtID.toString()
                cityPresenter.callCityApi()
                spinner.isEnabled = false
                return
            }
        }
    }

    override val districtId: String
        get() = mDistrictId

    override fun getCityFailure(msg: String) = showToast(msg)

    override fun getCitySuccess(value: Response.ResponseCity, addressType: AppEnums.ADDRESS_TYPE?) {
        if (value.responseObj != null && value.responseObj.size > 0) {
            setCityValue(binding.referenceAddressLayout.spinnerCity, value.responseObj)
        }
    }

    private fun setCityValue(spinner: MaterialSpinner, responseObj: ArrayList<Response.CityObj>) {
        spinner.adapter = CitySpinnerAdapter(mContext, responseObj)
        for (index in 0 until spinner.count - 1) {
            val obj = spinner.getItemAtPosition(index) as Response.CityObj
            if (obj.cityID == pinCodeObj!!.cityID) {
                spinner.setSelection(index + 1)
                spinner.isEnabled = false
                return
            }
        }
    }

    private fun getReferenceModel(): ReferenceModel {
        val referenceModel = ReferenceModel()
        val relation = binding.spinnerRelation.selectedItem as DropdownMaster?
        val occupation = binding.spinnerOccupation.selectedItem as DropdownMaster?
        val cCity = binding.referenceAddressLayout.spinnerCity.selectedItem as Response.CityObj?
        val cDistrict = binding.referenceAddressLayout.spinnerDistrict.selectedItem as Response.DistrictObj?

        referenceModel.addressBean?.cityID = cCity?.cityID
        referenceModel.addressBean?.districtID = cDistrict?.districtID
        referenceModel.addressBean?.address1 = binding.referenceAddressLayout.etAddress.text.toString()
        referenceModel.addressBean?.landmark = binding.referenceAddressLayout.etLandmark.text.toString()
        referenceModel.addressBean?.zip = binding.referenceAddressLayout.etPinCode.text.toString()
        referenceModel.name = binding.etName.text.toString()
        referenceModel.contactNumber = binding.referenceAddressLayout.etContactNum.text.toString()
        referenceModel.knowSince = binding.etKnownSince.text.toString()
        referenceModel.relationTypeDetailID = relation?.typeDetailID
        referenceModel.occupationTypeDetailID = occupation?.typeDetailID
        return referenceModel
    }

    private fun getReferenceMaster(): ReferenceMaster {
        rDraftData.referenceDetails = referencesList
        rDraftData.isMainApplicant = true
        rDraftData.leadApplicantNumber = leadAndLoanDetail.getLeadApplicantNum(1, mLead!!.leadNumber!!)
        referenceMaster.draftData = rDraftData
        referenceMaster.leadID = leadIdForApplicant.toInt()
        return referenceMaster
    }

    override fun onReferenceDetailDeleteClicked(position: Int) = showAlertDialog(position)

    override fun onReferenceDetailEditClicked(position: Int, reference: ReferenceModel) {
        currentPosition = position
        fillFormWithCurrentReference(reference)
    }

    private fun showAlertDialog(position: Int) {
        val deleteDialogView = LayoutInflater.from(activity).inflate(R.layout.delete_dialog, null)
        val mBuilder = AlertDialog.Builder(mContext)
                .setView(deleteDialogView)
                .setTitle("Delete Reference")
        val deleteDialog = mBuilder.show()
        deleteDialogView.tvDeleteConfirm.setOnClickListener { deleteReference(position, deleteDialog) }
        deleteDialogView.tvDonotDelete.setOnClickListener { deleteDialog.dismiss() }
    }

    private fun deleteReference(position: Int, deleteDialog: AlertDialog) {
        referencesList?.removeAt(position)
        binding.rcReference.adapter!!.notifyItemRemoved(position)
        deleteDialog.dismiss()
        if (referencesList!!.size > 0) binding.pageIndicatorAsset.visibility = View.GONE
        else binding.pageIndicatorAsset.visibility = View.GONE

    }

    inner class CallGetLoan : ViewGeneric<ArrayList<String>?, Response.ResponseGetLoanApplication>(context = mContext) {

        override val apiRequest: ArrayList<String>?
            get() = arrayListOf(leadIdForApplicant, referenceMaster.storageType)

        override fun getApiSuccess(value: Response.ResponseGetLoanApplication) {
            if (value.responseCode == Constants.SUCCESS) {
                value.responseObj?.let {
                    referenceMaster = LeadRequestResponseConversion().toReferenceMaster(value.responseObj)
                    rDraftData = referenceMaster.draftData
                    referencesList = rDraftData.referenceDetails
                }
                showData(referencesList)
            } else getDataFromDB()
        }
    }

    inner class CallPostLoanApp : ViewGeneric<LoanApplicationRequest, Response.ResponseGetLoanApplication>(context = mContext) {
        override val apiRequest: LoanApplicationRequest
            get() = RequestConversion().referenceRequest(getReferenceMaster())

        override fun getApiSuccess(value: Response.ResponseGetLoanApplication) {
            if (value.responseCode == Constants.SUCCESS) {
                saveDataToDB(getReferenceMaster())
                AppEvents.fireEventLoanAppChangeNavFragmentNext()
            } else saveDataToDB(getReferenceMaster())
        }
    }

    private fun getDataFromDB() {
        dataBase.provideDataBaseSource().referenceDao().getReference(leadIdForApplicant).observe(this, Observer { referenceInfo ->
            referenceInfo?.let {
                referenceMaster = referenceInfo
                rDraftData = referenceMaster.draftData
                referencesList = rDraftData.referenceDetails
                if (referencesList!!.size < 0) {
                    referencesList!!.add(ReferenceModel())
                }
            }
            showData(referencesList)
        })
    }

    private fun saveDataToDB(reference: ReferenceMaster) {
        GlobalScope.launch {
            dataBase.provideDataBaseSource().referenceDao().insertReference(reference)
        }
    }

}*/
