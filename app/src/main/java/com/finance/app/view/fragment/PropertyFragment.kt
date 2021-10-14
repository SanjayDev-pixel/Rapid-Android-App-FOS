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
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.databinding.FragmentPropertyInfoBinding
import com.finance.app.eventBusModel.AppEvents
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.*
import com.finance.app.presenter.connector.DistrictCityConnector
import com.finance.app.presenter.connector.PinCodeDetailConnector
import com.finance.app.presenter.connector.TransactionCategoryConnector
import com.finance.app.presenter.presenter.*
import com.finance.app.utility.*
import com.finance.app.view.adapters.recycler.spinner.*
import com.google.android.material.textfield.TextInputEditText
import fr.ganfra.materialspinner.MaterialSpinner
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

class PropertyFragment {

//Test Changes

}

/*


class PropertyFragment : BaseFragment(), TransactionCategoryConnector.TransactionCategory,
        PinCodeDetailConnector.PinCode, DistrictCityConnector.District, DistrictCityConnector.City {

    @Inject
    lateinit var formValidation: FormValidation
    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    private lateinit var binding: FragmentPropertyInfoBinding
    private lateinit var mContext: Context
    private lateinit var allMasterDropDown: AllMasterDropDown
    private lateinit var states: List<StatesMaster>
    private val transactionCategoryPresenter = TransactionCategoryPresenter(this)
    private val pinCodePresenter = PinCodeDetailPresenter(this)
    private val presenter = Presenter()
    private lateinit var leadIdForApplicant: String
    private val districtPresenter = DistrictPresenter(this)
    private val cityPresenter = CityPresenter(this)
    private var propertyMaster: PropertyMaster = PropertyMaster()
    private var propertyModel: PropertyModel? = PropertyModel()
    private var pinCodeObj: Response.PinCodeObj? = null
    private val leadAndLoanDetail = LeadAndLoanDetail()
    private var mPinCode: String = ""
    private var mStateId: String = ""
    private var mDistrictId: String = ""
    private var mLead: AllLeadMaster? = null
    private var mOwnershipId: String = ""
    private var mTransactionId: String = ""
    private var ownershipID: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = initBinding(inflater, container, R.layout.fragment_property_info)
        init()
        return binding.root
    }

    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        mLead = sharedPreferences.getLeadDetail()
        mContext = context!!
        leadIdForApplicant = mLead!!.leadID.toString()
        checkPropertySelection()
        SetPropertyMandatoryField(binding)
        setClickListeners()
    }

    private fun checkPropertySelection() {
        getPropertyInfo()
    }

    private fun getPropertyInfo() {
        presenter.callNetwork(ConstantsApi.CALL_GET_LOAN_APP, CallGetLoan())
    }

    private fun showData(property: PropertyModel?) {
        getDropDownsFromDB()
        fillFormWithPropertyData(property!!)
    }

    private fun getDropDownsFromDB() {
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(viewLifecycleOwner, Observer { masterDrownDownValues ->
            masterDrownDownValues?.let {
                allMasterDropDown = masterDrownDownValues
                setMasterDropDown(allMasterDropDown)
            }
        })

        dataBase.provideDataBaseSource().statesDao().getAllStates().observe(viewLifecycleOwner, Observer { stateMaster ->
            stateMaster?.let {
                states = stateMaster
                setStateDropDown(states)
            }
        })
    }

    private fun fillFormWithPropertyData(property: PropertyModel) {
        binding.cbIsFirstProperty.isChecked = property.isFirstProperty
        binding.etDistanceFromBranch.setText(property.distanceFromBranch)
        binding.etDistanceFromResidence.setText(property.distanceFromExistingResidence)
        binding.etPropertyArea.setText(property.propertyAreaSquareFt.toString())
        binding.etPropertyAddress.setText(property.propertyAddress)
        binding.etLandmark.setText(property.landmark)
        binding.etNumOfTenants.setText(property.numberOfTenants.toString())
        binding.etCashOcr.setText(property.cashOCRValue.toString())
        binding.etOcr.setText(property.ocrValue.toString())
        binding.etPinCode.setText(property.pinCode)
        binding.etMvProperty.setText(property.mvOfProperty)
        binding.etAgreementValue.setText(property.agreementValue.toString())
        checkSubmission()
    }

    private fun checkSubmission() {
        if (mLead!!.status == AppEnums.LEAD_TYPE.SUBMITTED.type) {
            DisablePropertyFields(binding)
        }
    }

    private fun setMasterDropDown(allMasterDropDown: AllMasterDropDown) {
        binding.spinnerUnitType.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.PropertyUnitType!!)
        binding.spinnerOwnedProperty.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.AlreadyOwnedProperty!!)
        binding.spinnerOccupiedBy.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.PropertyOccupiedBy!!)
        binding.spinnerTenantNocAvailable.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.TenantNocAvailable!!)
        binding.spinnerTransactionCategory.adapter = PropertyNatureSpinnerAdapter(mContext, ArrayList())
        setUpOwnership(binding.spinnerOwnership, allMasterDropDown)
        setUpPropertyNature(binding.spinnerPropertyNature, allMasterDropDown)
        fillDropDownValue()
    }

    private fun fillDropDownValue() {
        selectMasterDropdownValue(binding.spinnerOwnedProperty, propertyModel!!.alreadyOwnedPropertyTypeDetailID)
        selectMasterDropdownValue(binding.spinnerOwnership, propertyModel!!.ownershipTypeDetailID)
        selectMasterDropdownValue(binding.spinnerUnitType, propertyModel!!.unitTypeTypeDetailID)
        selectMasterDropdownValue(binding.spinnerPropertyNature, propertyModel!!.natureOfPropertyTransactionTypeDetailID)
        selectMasterDropdownValue(binding.spinnerOccupiedBy, propertyModel!!.occupiedByTypeDetailID)
        selectMasterDropdownValue(binding.spinnerTenantNocAvailable, propertyModel!!.tenantNocAvailableTypeDetailID)
        selectPropertyNatureValue(binding.spinnerPropertyNature)
    }

    private fun setUpOwnership(ownershipSpinner: Spinner, allMasterDropDown: AllMasterDropDown) {
        ownershipSpinner.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.PropertyOwnership!!)
        ownershipSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0) {
                    val ownership = parent.selectedItem as DropdownMaster?
                    ownershipID = ownership?.typeDetailID!!
                    binding.spinnerTransactionCategory.adapter = PropertyNatureSpinnerAdapter(mContext, ArrayList())
                }
            }
        }
    }

    private fun setUpPropertyNature(spinner: Spinner, allMasterDropDown: AllMasterDropDown) {
        spinner.adapter = MasterSpinnerAdapter(mContext, allMasterDropDown.NatureOfPropertyTransaction!!)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0) {
                    val transaction = parent.selectedItem as DropdownMaster
                    callTransactionCategoryApi(transaction.typeDetailID)
                }
            }
        }
    }

    private fun selectPropertyNatureValue(spinner: Spinner) {
        for (index in 0 until spinner.count - 1) {
            val obj = spinner.getItemAtPosition(index) as DropdownMaster
            if (obj.typeDetailID == propertyModel!!.natureOfPropertyTransactionTypeDetailID) {
                spinner.setSelection(index + 1)
                return
            }
        }
    }

    override val ownershipId: String
        get() = mOwnershipId

    override val transactionId: String
        get() = mTransactionId

    private fun callTransactionCategoryApi(transactionId: Int?) {
        mOwnershipId = ownershipID.toString()
        mTransactionId = transactionId.toString()
        transactionCategoryPresenter.callNetwork(ConstantsApi.CALL_TRANSACTON_CATEGORY)
    }

    override fun getTransactionCategoryFailure(msg: String) = showToast(msg)

    override fun getTransactionCategorySuccess(value: Response.ResponsePropertyNature) {
        binding.spinnerTransactionCategory.adapter = PropertyNatureSpinnerAdapter(mContext, value.responseObj)
        selectTransactionCategory(binding.spinnerTransactionCategory)
    }

    private fun setStateDropDown(states: List<StatesMaster>) {
        binding.spinnerState.adapter = StatesSpinnerAdapter(mContext, states)
        binding.spinnerCity.adapter = CitySpinnerAdapter(mContext, ArrayList())
        binding.spinnerDistrict.adapter = DistrictSpinnerAdapter(mContext, ArrayList())
        binding.spinnerState?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

    private fun selectMasterDropdownValue(spinner: Spinner, id: Int?) {
        for (index in 0 until spinner.count - 1) {
            val obj = spinner.getItemAtPosition(index) as DropdownMaster
            if (obj.typeDetailID == id) {
                spinner.setSelection(index + 1)
                return
            }
        }
    }

    private fun selectTransactionCategory(spinner: Spinner) {
        for (index in 0 until spinner.count - 1) {
            val obj = spinner.getItemAtPosition(index) as Response.TransactionCategoryObj
            if (obj.propertyNatureTransactionCategoryID == propertyModel!!.propertyNatureOfTransactionCategoryTypeDetailID) {
                spinner.setSelection(index + 1)
                return
            }
        }
    }

    private fun setClickListeners() {
        binding.btnPrevious.setOnClickListener { AppEvents.fireEventLoanAppChangeNavFragmentPrevious() }
        binding.btnNext.setOnClickListener {
            if (formValidation.validateProperty(binding)) {
                presenter.callNetwork(ConstantsApi.CALL_POST_LOAN_APP, dmiConnector = CallPostLoanApp())
            } else showToast(getString(R.string.validation_error))
        }
        pinCodeListener(binding.etPinCode)
        CurrencyConversion().convertToCurrencyType(binding.etCashOcr)
        CurrencyConversion().convertToCurrencyType(binding.etOcr)
        CurrencyConversion().convertToCurrencyType(binding.etMvProperty)
        CurrencyConversion().convertToCurrencyType(binding.etAgreementValue)
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

    override fun getPinCodeSuccess(value: Response.ResponsePinCodeDetail, addressType: AppEnums.ADDRESS_TYPE?) {
        if (value.responseObj!!.size > 0) {
            pinCodeObj = value.responseObj[0]
            selectStateValue(binding.spinnerState)
        } else {
            clearPinCodeData()
        }
    }

    private fun selectStateValue(spinner: Spinner) {
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
            setDistrict(binding.spinnerDistrict, value)
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
            setCityValue(binding.spinnerCity, value.responseObj)
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

    private fun clearPinCodeData() {
        binding.spinnerState.isEnabled = true
        binding.spinnerDistrict.isEnabled = true
        binding.spinnerCity.isEnabled = true
        binding.spinnerState.adapter = StatesSpinnerAdapter(mContext, states)
        binding.spinnerDistrict.adapter = DistrictSpinnerAdapter(mContext, ArrayList())
        binding.spinnerCity.adapter = CitySpinnerAdapter(mContext, ArrayList())
    }

    private fun getPropertyModel(): PropertyModel {
        val propertyModel = PropertyModel()
        val transactionCategory = binding.spinnerTransactionCategory.selectedItem as Response.TransactionCategoryObj?
        val propertyNature = binding.spinnerPropertyNature.selectedItem as DropdownMaster?
        val state = binding.spinnerState.selectedItem as StatesMaster?
        val district = binding.spinnerDistrict.selectedItem as Response.DistrictObj?
        val city = binding.spinnerCity.selectedItem as Response.CityObj?
        val unitType = binding.spinnerUnitType.selectedItem as DropdownMaster?
        val ownership = binding.spinnerOwnership.selectedItem as DropdownMaster?
        val ownedProperty = binding.spinnerOwnedProperty.selectedItem as DropdownMaster?
        val occupiedBy = binding.spinnerOccupiedBy.selectedItem as DropdownMaster?
        val tenantNoc = binding.spinnerTenantNocAvailable.selectedItem as DropdownMaster?

        propertyModel.leadID = leadIdForApplicant.toInt()
        propertyModel.cityID = city?.cityID
        propertyModel.districtID = district?.districtID
        propertyModel.stateID = state?.stateID
        propertyModel.occupiedByTypeDetailID = occupiedBy?.typeDetailID
        propertyModel.natureOfPropertyTransactionTypeDetailID = propertyNature?.typeDetailID
        propertyModel.propertyNatureOfTransactionCategoryTypeDetailID = transactionCategory?.propertyNatureTransactionCategoryID
        propertyModel.unitTypeTypeDetailID = unitType?.typeDetailID
        propertyModel.alreadyOwnedPropertyTypeDetailID = ownedProperty?.typeDetailID
        propertyModel.ownershipTypeDetailID = ownership?.typeDetailID
        propertyModel.propertyAreaSquareFt = binding.etPropertyArea.text.toString().toInt()
        propertyModel.propertyAddress = binding.etPropertyArea.text.toString()
        propertyModel.landmark = binding.etLandmark.text.toString()
        propertyModel.pinCode = binding.etPinCode.text.toString()
        propertyModel.distanceFromBranch = binding.etDistanceFromBranch.text.toString()
        propertyModel.numberOfTenants = binding.etNumOfTenants.text.toString().toInt()
        propertyModel.cashOCRValue = CurrencyConversion().convertToNormalValue(binding.etCashOcr.text.toString()).toDouble()
        propertyModel.ocrValue = CurrencyConversion().convertToNormalValue(binding.etOcr.text.toString()).toDouble()
        propertyModel.tenantNocAvailableTypeDetailID = tenantNoc?.typeDetailID
        propertyModel.mvOfProperty = CurrencyConversion().convertToNormalValue(binding.etMvProperty.text.toString())
        propertyModel.agreementValue = CurrencyConversion().convertToNormalValue(binding.etAgreementValue.text.toString()).toDouble()
        propertyModel.leadApplicantNumber = leadAndLoanDetail.getLeadApplicantNum(1, mLead!!.leadNumber!!)
        propertyModel.isFirstProperty = binding.cbIsFirstProperty.isChecked
        propertyModel.distanceFromExistingResidence = binding.etDistanceFromResidence.text.toString()
        return propertyModel
    }

    private fun getPropertyMaster(): PropertyMaster {
        propertyMaster.draftData = getPropertyModel()
        propertyMaster.leadID = leadIdForApplicant.toInt()
        return propertyMaster
    }

    inner class CallGetLoan : ViewGeneric<ArrayList<String>?, Response.ResponseGetLoanApplication>(context = mContext) {

        override val apiRequest: ArrayList<String>?
            get() = arrayListOf(leadIdForApplicant, propertyMaster.storageType)

        override fun getApiSuccess(value: Response.ResponseGetLoanApplication) {
            if (value.responseCode == Constants.SUCCESS) {
                value.responseObj?.let {
                    propertyMaster = LeadRequestResponseConversion().toPropertyMaster(value.responseObj)
                    propertyModel = propertyMaster.draftData
                }
                showData(propertyModel)
            } else getDataFromDB()
        }
    }

    inner class CallPostLoanApp : ViewGeneric<LoanApplicationRequest, Response.ResponseGetLoanApplication>(context = mContext) {
        override val apiRequest: LoanApplicationRequest
            get() = RequestConversion().propertyRequest(getPropertyMaster())

        override fun getApiSuccess(value: Response.ResponseGetLoanApplication) {
            if (value.responseCode == Constants.SUCCESS) {
                saveDataToDB(getPropertyMaster())
                AppEvents.fireEventLoanAppChangeNavFragmentNext()
            } else saveDataToDB(getPropertyMaster())
        }
    }

    private fun getDataFromDB() {
        dataBase.provideDataBaseSource().propertyDao().getProperty(leadIdForApplicant).observe(this, Observer { propertyInfo ->
            propertyInfo?.let {
                propertyMaster = propertyInfo
                propertyModel = propertyMaster.draftData
            }
            showData(propertyModel)
        })
    }

    private fun saveDataToDB(property: PropertyMaster) {
        GlobalScope.launch {
            dataBase.provideDataBaseSource().propertyDao().insertProperty(property)
        }
    }

}
*/

