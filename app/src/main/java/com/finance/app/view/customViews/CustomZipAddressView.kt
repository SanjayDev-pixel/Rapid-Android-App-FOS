package com.finance.app.view.customViews

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AddressDetail
import com.finance.app.persistence.model.StatesMaster
import com.finance.app.presenter.connector.DistrictCityConnector
import com.finance.app.presenter.connector.PinCodeDetailConnector
import com.finance.app.presenter.presenter.CityPresenter
import com.finance.app.presenter.presenter.DistrictPresenter
import com.finance.app.presenter.presenter.PinCodeDetailPresenter
import com.finance.app.utility.ShowAsMandatory
import com.finance.app.view.adapters.recycler.spinner.CitySpinnerAdapter
import com.finance.app.view.adapters.recycler.spinner.DistrictSpinnerAdapter
import com.finance.app.view.adapters.recycler.spinner.StatesSpinnerAdapter
import com.finance.app.view.utils.EditTexNormal
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import fr.ganfra.materialspinner.MaterialSpinner
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import motobeans.architecture.util.exShowToast
import javax.inject.Inject

/**
 * Created by munishkumarthakur on 14/12/19.
 */

class CustomZipAddressView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context,
        attrs), DistrictCityConnector.District, PinCodeDetailConnector.PinCode, DistrictCityConnector.City {

    private val TAG = "CustomSignatureView"

    @Inject
    lateinit var dataBase: DataBaseUtil

    private val pinCodePresenter = PinCodeDetailPresenter(this)
    private val districtPresenter = DistrictPresenter(this)
    private val cityPresenter = CityPresenter(this)

    private var mStateId: String = ""
    private var mDistrictId: String = ""
    private var mCityId: String = ""
    private var mStateName: String=""
    private var mCityName: String=""
    private var mDistrictName: String?=""

    private var listStatesDB: ArrayList<StatesMaster> = ArrayList()
    private var listStates: ArrayList<StatesMaster> = ArrayList()
    private var listDistrict: ArrayList<Response.DistrictObj> = ArrayList()
    private var listCity: ArrayList<Response.CityObj> = ArrayList()

    private lateinit var adapterState: StatesSpinnerAdapter
    private lateinit var adapterDistrict: DistrictSpinnerAdapter
    private lateinit var adapterCity: CitySpinnerAdapter

    private var pinCodeObj: Response.PinCodeObj? = null
    private var serverPinCodeObj: Response.PinCodeObj? = null

    private lateinit var inputLayoutCurrentPinCode: TextInputLayout
    private lateinit var etCurrentPinCode: EditTexNormal
    private lateinit var spinnerCurrentState: MaterialSpinner
    private lateinit var spinnerCurrentDistrict: MaterialSpinner
    private lateinit var spinnerCurrentCity: MaterialSpinner

    private fun initializeViews(rootView: View) {
        inputLayoutCurrentPinCode = rootView.findViewById(R.id.inputLayoutCurrentPinCode)
        etCurrentPinCode = rootView.findViewById(R.id.etCurrentPinCode)
        spinnerCurrentState = rootView.findViewById(R.id.spinnerCurrentState)
        spinnerCurrentDistrict = rootView.findViewById(R.id.spinnerCurrentDistrict)
        spinnerCurrentCity = rootView.findViewById(R.id.spinnerCurrentCity)
    }

    private lateinit var activity: FragmentActivity
    private var isMandatory: Boolean = false

    fun attachActivity(activity: FragmentActivity) {
        this.activity = activity

        proceedFurther()
    }

    private fun updatePinCodeData(serverPinCodeObj: Response.PinCodeObj? = null) {
        this.serverPinCodeObj = serverPinCodeObj
        handleUserSpecificPinCode()
    }

    fun updateAddressData(addressDetail: AddressDetail? = null) {
        addressDetail?.let {
            val serverPinCodeObj = Response.PinCodeObj(pincode = addressDetail.zip, stateID = addressDetail.stateID, stateName = addressDetail.stateName,
                    districtID = addressDetail.districtID, districtName = addressDetail.districtName,
                    cityID = addressDetail.cityID, cityName = addressDetail.cityName, pincodeID = -1)
            updatePinCodeData(serverPinCodeObj = serverPinCodeObj)
        }
    }

    fun isMandatory(isMandatory: Boolean) {
        this.isMandatory = isMandatory
    }

    private fun proceedFurther() {
        ArchitectureApp.instance.component.inject(this)

        initView()
        pinCodeListener()
        setUpSpinners()
        getDropDownsFromDB()
    }

    private fun initView() = ShowAsMandatory(inputLayoutCurrentPinCode)

    private fun setUpSpinners() {
        adapterState = StatesSpinnerAdapter(activity, listStates)
        adapterDistrict = DistrictSpinnerAdapter(activity, listDistrict)
        adapterCity = CitySpinnerAdapter(activity, listCity)

        spinnerCurrentState.adapter = adapterState
        spinnerCurrentDistrict.adapter = adapterDistrict
        spinnerCurrentCity.adapter = adapterCity

        spinnerCurrentState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0) {
                    val state = parent.selectedItem as StatesMaster
                    mStateId = state.stateID.toString()
                     mStateName=state.stateName
                    districtPresenter.callDistrictApi()
                }
            }
        }

        spinnerCurrentDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0) {
                    val district = parent.selectedItem as Response.DistrictObj?
                    mDistrictId = district?.districtID.toString()
                    mDistrictName=district?.districtName
                    cityPresenter.callCityApi()
                }
            }
        }
    }

    private var userAutomaticValueSet = false
    private fun pinCodeListener() {
        etCurrentPinCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etCurrentPinCode.text!!.length == 6) {
                    if (!userAutomaticValueSet) {
                        pinCodePresenter.callPinCodeDetailApi()
                    } else {
                        userAutomaticValueSet = false
                        setServerCodes()
                    }
                } else clearPinCodes()
            }
        })
    }

    private fun getDropDownsFromDB() {
        dataBase.provideDataBaseSource().statesDao().getAllStates().observe(activity, Observer {
            it?.let {
                listStatesDB.clear()
                listStatesDB.addAll(it)
            }
            updateStates(it)
            handleUserSpecificPinCode()
        })
    }

    private fun handleUserSpecificPinCode() {
        serverPinCodeObj?.pincode?.let {
            userAutomaticValueSet = true
            etCurrentPinCode.setText("${serverPinCodeObj?.pincode}")
        }
    }

    private fun updateStates(statesList: List<StatesMaster>?, isReset: Boolean = false) {
        listStates.clear()
        statesList?.let {
            listStates.addAll(statesList)
        }
        if (listStates.size == 0 || isReset) {
            spinnerCurrentState.setSelection(0)
        }
        adapterState.notifyDataSetChanged()
    }

    private fun updateDistrict(districtList: List<Response.DistrictObj>?) {
        listDistrict.clear()
        districtList?.let {
            listDistrict.addAll(districtList)
        }
        if (listDistrict.size == 0) {
            spinnerCurrentDistrict.setSelection(0)
        }
        adapterDistrict.notifyDataSetChanged()
    }

    private fun updateCity(cityList: List<Response.CityObj>?) {
        listCity.clear()
        cityList?.let {
            listCity.addAll(cityList)
        }
        if (listCity.size == 0) {
            spinnerCurrentCity.setSelection(0)
        }
        adapterCity.notifyDataSetChanged()
    }

    override fun showToast(msg: String) = msg.exShowToast(activity)

    override fun showProgressDialog() {}

    override fun hideProgressDialog() {}

    override val pinCode: String
        get() = etCurrentPinCode.text.toString()

    override fun getPinCodeSuccess(value: Response.ResponsePinCodeDetail, addressType: AppEnums.ADDRESS_TYPE?) {
        if (value.responseObj!!.size > 0) {
            pinCodeObj = value.responseObj[0]
            selectStateValue()
        } else {
            setServerCodes()
        }
    }

    private fun setServerCodes() {
        this.pinCodeObj = serverPinCodeObj
        clearPinCodes()
        selectStateValue()

        serverPinCodeObj = null
    }

    override fun getPinCodeFailure(msg: String) = clearPinCodes()

    private fun parseAddressInt(value: String?): Int {
        return if (!value.isNullOrEmpty()) value.toInt() else 0
    }

    fun getStateId(): Int = parseAddressInt(value = stateId)

    fun getDistrictId(): Int = parseAddressInt(value = districtId)

    fun getCityId(): Int = parseAddressInt(value = mCityId)
    fun getCityName(): String = mCityName
    fun getDistrictName():String?= mDistrictName
    fun getStateName():String=mStateName



    override val stateId: String
        get() = mStateId

    override fun getDistrictFailure(msg: String) = showToast(msg)

    override fun getDistrictSuccess(value: Response.ResponseDistrict, addressType: AppEnums.ADDRESS_TYPE?) {
        if (value.responseObj != null && value.responseObj.size > 0) {
            setUpDistrict(value)
        }
    }

    override val districtId: String
        get() = mDistrictId

    override fun getCityFailure(msg: String) = showToast(msg)

    override fun getCitySuccess(value: Response.ResponseCity, addressType: AppEnums.ADDRESS_TYPE?) {
        if (value.responseObj != null && value.responseObj.size > 0) {
            setUpCity(value)
        }
    }

    fun clearPinCodes() {
//        etCurrentPinCode.text?.clear()
        spinnerCurrentDistrict.isEnabled = true
        spinnerCurrentCity.isEnabled = true
        spinnerCurrentState.isEnabled = true

        updateStates(listStatesDB, isReset = true)
        updateDistrict(ArrayList())
        updateCity(ArrayList())
    }

    private fun setUpDistrict(response: Response.ResponseDistrict) {
        updateDistrict(response.responseObj)
        selectDistrictValue()
    }

    private fun selectStateValue() {
        for (index in 0 until listStatesDB.size) {
            val obj = listStatesDB[index]
            if (obj.stateID == pinCodeObj?.stateID) {
                spinnerCurrentState.setSelection(index + 1)
                mStateId = pinCodeObj?.stateID.toString()
                spinnerCurrentState.isEnabled = false

                districtPresenter.callDistrictApi()
                return
            }
        }
    }

    private fun selectDistrictValue() {
        for (index in 0 until listDistrict.size) {
            val obj = listDistrict[index]
            if (obj.districtID == pinCodeObj?.districtID) {
                spinnerCurrentDistrict.setSelection(index + 1)
                mDistrictId = obj.districtID.toString()
                spinnerCurrentDistrict.isEnabled = false

                cityPresenter.callCityApi()
                return
            }
        }
    }

    private fun setUpCity(response: Response.ResponseCity) {
        updateCity(response.responseObj)
        for (index in 0 until listCity.size) {
            val obj = listCity[index]
            if (obj.cityID == pinCodeObj!!.cityID) {
                spinnerCurrentCity.setSelection(index + 1)
                mCityId = obj.cityID.toString()
                mCityName=obj.cityName
                spinnerCurrentCity.isEnabled = false
                return
            }
        }
    }

    fun clearZipCode() = etCurrentPinCode.text?.clear()

    fun disableSelf() {
        System.out.println("Disable Self")
        /* etCurrentPinCode.isEnabled = false
         spinnerCurrentCity.isEnabled = false
         spinnerCurrentDistrict.isEnabled = false
         spinnerCurrentCity.isEnabled = false*/
        spinnerCurrentState.isEnabled = false
        spinnerCurrentDistrict.isEnabled = false
        spinnerCurrentCity.isEnabled = false
    }

    fun validateAndHandleError(): Boolean {
        var errorCount = 0
        val currentPinCode = etCurrentPinCode.text.toString()
        val city = spinnerCurrentCity.selectedItem as Response.CityObj?
        val district = spinnerCurrentDistrict.selectedItem as Response.DistrictObj?
        val state = spinnerCurrentState.selectedItem as StatesMaster?

        if (city == null) {
            errorCount++
            spinnerCurrentCity.error = context.getString(R.string.error_required_field)
        }
        if (district == null) {
            errorCount++
            spinnerCurrentDistrict.error = context.getString(R.string.error_required_field)
        }
        if (state == null) {
            errorCount++
            spinnerCurrentState.error = context.getString(R.string.error_required_field)
        }
        if (!currentPinCode.exIsNotEmptyOrNullOrBlank()) {
            errorCount++
            etCurrentPinCode.error = context.getString(R.string.error_pin_code)
        }
        return errorCount <= 0
    }

    override fun getApiFailure(msg: String) {
        showToast(msg)
    }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView = inflater.inflate(R.layout.layout_zip_address, this, true)
        initializeViews(rootView)
    }
}