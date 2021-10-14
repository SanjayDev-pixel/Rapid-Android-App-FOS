package com.finance.app.view.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import com.finance.app.R
import com.finance.app.databinding.ActivityKycBinding
import com.finance.app.databinding.KycoptiondialogBinding
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.utils.EditTexNormal
import com.finance.app.viewModel.AppDataViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.dialogekyconmobile.*
import motobeans.architecture.appDelegates.ViewModelType
import motobeans.architecture.application.ArchitectureApp

import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.Constants.API.URL.URL_KYC
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import java.nio.charset.StandardCharsets
import java.util.*
import javax.inject.Inject


class KYCActivity : BaseAppCompatActivity() {
    @Inject
    lateinit var dataBase: DataBaseUtil
    private val binding: ActivityKycBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_kyc)

    private val kycPresenter = Presenter()
    private var bundle: Bundle? = null
    var kyCID: String? = null
    private var kycOptionDialog: Dialog? = null
    private var kycOptionMobileDialoge : Dialog ? = null
    var encodedStringScanned=""
    var  leadIDnumber:String?= null
    var kycTypeValue : String ? = null
    val lead = LeadMetaData.getLeadData()
    private var allMasterDropdown: AllMasterDropDown? = null
    private val appDataViewModel: AppDataViewModel by motobeans.architecture.appDelegates.viewModelProvider(this, ViewModelType.WITH_DAO)
    //var IncomeConsider : Int ? = 0
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL = (2 * 1000).toLong()  /* 10 secs */
    private var latitude : Double ? = null
    private var longitude : Double ? = null
    companion object {
        fun start(context: Context, leadApplicantNum: String?,isIncomeConsider : Int,isLeadSubmitted : Int,isKycAttempt : String,kycStatus :String,isKycByPassAllowed :String) {
            val intent = Intent(context, KYCActivity::class.java)
            val bundle = Bundle()
            bundle.putString(Constants.KEY_LEAD_APP_NUM, leadApplicantNum)
            bundle.putInt("isIncomeConsider",isIncomeConsider)
            bundle.putInt("isLeadSubmitted",isLeadSubmitted)
            bundle.putString(Constants.IS_KYC_ATTEMPT,isKycAttempt)
            bundle.putString(Constants.ISKYC_BYPASS_ALLOWED,isKycByPassAllowed)
            bundle.putString(Constants.KYC_STATUS,kycStatus)
            intent.putExtras(bundle)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

        }

    }

     override fun init() {
        ArchitectureApp.instance.component.inject(this)
        hideToolbar()
        hideSecondaryToolbar()
        setClickListeners()
        //proceedFurther()
        performKycAlert()
         fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

         //init location request based on need.
         locationRequest = LocationRequest.create()
         locationRequest?.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY //will return only when if GPS or network is  active...
         locationRequest?.interval = UPDATE_INTERVAL
         showLocationPrompt()
        //SAcn Adhar QR
       // scanNow()
         startLocationClient()
         //Checking Commit
    }
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            if (locationAvailability.isLocationAvailable) Log.i("TAG" , "Location is available")
            else Log.i("TAG" , "Location is un-available")
        }

        override fun onLocationResult(locationResult: LocationResult) {
            Log.i("TAG" , "Latitude:- ${locationResult.lastLocation?.latitude}")
            latitude = locationResult.lastLocation?.latitude
            longitude = locationResult.lastLocation?.longitude
            //Now insert location into database...

        }
    }


    private fun showLocationPrompt() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            // Cast to a resolvable exception.
                            val resolvable: ResolvableApiException = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                    this, LocationRequest.PRIORITY_HIGH_ACCURACY
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.

                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                    }
                }
            }
        }
    }
    private fun startLocationClient() {
        fusedLocationProviderClient?.requestLocationUpdates(locationRequest , locationCallback , null)
        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener { location -> Log.i("TAG" , "Latitude:- ${location?.latitude} and Longitude:- ${location?.longitude}") }
        fusedLocationProviderClient?.lastLocation?.addOnFailureListener { exception -> Log.i("TAG" , "Exception while getting the location ${exception.message}") }
    }

    private fun setClickListeners() {
    }
    private fun scanNow(){
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        integrator.setPrompt("Scan a Aadharcard QR Code")
        integrator.setResultDisplayDuration(500)
        integrator.setCameraId(0) // Use a specific camera of the device
        integrator.initiateScan()

    }


    override fun onActivityResult(requestCode: Int , resultCode: Int , data: Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        //Reterive Scan Result
        val scanningResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if(scanningResult != null){
            //we have a result

            val scanContent = scanningResult.contents
            val scanFormat = scanningResult.formatName
           // val byteData = scanningResult.rawBytes.toString()
            if(data !=null) {
                val extradTa = data.toString()
                System.out.println("byteData>>>>" + scanningResult.toString())
                val encodedString: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Base64.getEncoder().encodeToString(scanContent.toByteArray())
                } else {
                    val data = scanContent.toByteArray(charset("UTF-8"))
                    android.util.Base64.encodeToString(data , android.util.Base64.DEFAULT)
                }
                encodedStringScanned = encodedString
                if(kycTypeValue.equals("QRCODE_PAN_REQUEST")) {
                    kycPresenter.callNetwork(ConstantsApi.CALL_KYC_PREPARE , dmiConnector = KYCidApiCall(leadIDnumber , "QRCODE_PAN_REQUEST"))
                }
                else if(kycTypeValue.equals("QRCODE_DL_REQUEST")){
                    kycPresenter.callNetwork(ConstantsApi.CALL_KYC_PREPARE , dmiConnector = KYCidApiCall(leadIDnumber , "QRCODE_DL_REQUEST"))
                }
                else if(kycTypeValue.equals("QRCODE_VC_REQUEST")){
                    kycPresenter.callNetwork(ConstantsApi.CALL_KYC_PREPARE , dmiConnector = KYCidApiCall(leadIDnumber , "QRCODE_VC_REQUEST"))
                }
            }
        }

    }


    private fun proceedFurther() {
        bundle = intent.extras
        bundle?.let {

           val  leadAppNum = bundle?.getString(Constants.KEY_LEAD_APP_NUM)
            val isIncomeConsider = bundle?.getInt("isIncomeConsider")
            val isLeadSubmitted = bundle?.getInt("isLeadSubmitted")
            val isKycAttempt = bundle?.getString(Constants.IS_KYC_ATTEMPT)
            val kycStatus = bundle?.getString(Constants.KYC_STATUS)
            val isKYCByPassAllowed = bundle?.getString(Constants.ISKYC_BYPASS_ALLOWED)

            System.out.println("IsIncomeConsider>>>>"+isLeadSubmitted)
            leadAppNum?.let {
                if (isIncomeConsider != null) {
                    showKycDialog(leadAppNum,isIncomeConsider,isLeadSubmitted,isKycAttempt!!,isKYCByPassAllowed!!,kycStatus!!)
                }
                leadIDnumber= leadAppNum
                System.out.println("Lead Id numer>>>"+leadIDnumber)
              //  kycPresenter.callNetwork(ConstantsApi.CALL_KYC, dmiConnector = KYCApiCall(leadAppNum))
            }
        }
    }

    private fun showKycMobileoption(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialogekyconmobile)
        val mobileNo = dialog.findViewById(R.id.etMobileNo) as EditTexNormal
        val btnClose = dialog.findViewById(R.id.btnClose) as Button
        val btnProceedMobile = dialog.findViewById(R.id.btnProceedMobile) as Button
        btnClose.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        btnProceedMobile.setOnClickListener { if(mobileNo.text?.length!! < 10)
        {
            showToast("Please Enter 10 digit mobile no")
        }
        else
        {
            bundle = intent.extras
            bundle?.let {
                val  leadAppNum = bundle?.getString(Constants.KEY_LEAD_APP_NUM)
                System.out.println("LeadApp Number>>>"+leadAppNum)
                leadAppNum?.let {
                    //showKycDialog(leadAppNum)
                    leadIDnumber= leadAppNum
                    System.out.println("Leadidumber>>>"+leadIDnumber)
                    val splitLeadId = leadIDnumber!!.split("_".toRegex())[0]
                    System.out.println("SplitleadId>>>"+splitLeadId)
                    encodedStringScanned = ""
                    kycPresenter.callNetwork(ConstantsApi.CALL_KYC_MOBILE_PREPARE , dmiConnector = KYCMobileApiCall(leadIDnumber,"AADHAAR_ZIP_INLINE",mobileNo.text.toString(),"true",splitLeadId))

                }
            }
        }
        }
        dialog.show()

    }


    private fun showKycDialog(leadApplicantNumber: String?,isIncomeConsider: Int,isLeadSubmitted: Int?,isKycAttempt: String,isKycByPassAllowed: String,kycStatus: String) {

        val bindingDialog = DataBindingUtil.inflate<KycoptiondialogBinding>(LayoutInflater.from(this) , R.layout.kycoptiondialog , null , false)
        val mBuilder = AlertDialog.Builder(this)
                .setView(bindingDialog.root)
                .setCancelable(false)

        kycOptionDialog = mBuilder.show()

        bindingDialog?.btnClose?.setOnClickListener() {
            kycOptionDialog?.dismiss()
            finish()
        }
        if(isIncomeConsider == 1)
        {
            bindingDialog.selfDeclartion.visibility = View.VISIBLE
            bindingDialog.rdbKycDocument.visibility = View.GONE
        }

        else if(isKycAttempt == "yes" && kycStatus == "pending" && isKycByPassAllowed == "yes" && isLeadSubmitted == 0)
        {
            bindingDialog.selfDeclartion.visibility = View.GONE
            bindingDialog.rdbKycDocument.visibility = View.VISIBLE
        }


       /* if(isLeadSubmitted == 1)
        {
            bindingDialog.selfDeclartion.visibility = View.GONE
        }
        else if(isIncomeConsider == 1)
        {
            bindingDialog.selfDeclartion.visibility = View.VISIBLE
        }*/
        bindingDialog?.groupRadioButton?.setOnCheckedChangeListener(
                RadioGroup.OnCheckedChangeListener { group , checkedId ->

                    if (checkedId == R.id.adharotp) {
                        kycTypeValue = "AADHAAR_ZIP_INLINE"
                        val leadAppNum = leadApplicantNumber
                       /* leadAppNum?.let {
                            kycPresenter.callNetwork(ConstantsApi.CALL_KYC , dmiConnector = KYCApiCall(leadAppNum))
                        }*/

                    } else if (checkedId == R.id.codeand_pan) {
                           kycTypeValue = "QRCODE_PAN_REQUEST"

                    } else if (checkedId == R.id.codeand_dl) {
                        kycTypeValue = "QRCODE_DL_REQUEST"
                        //Toast.makeText(this , "Currently System working on Aadhar Otp and QR Code and PAN." , Toast.LENGTH_SHORT).show()
                    } else if( checkedId == R.id.selfDeclartion)
                    {
                        kycTypeValue = "Self Declaration"
                    }
                    else if( checkedId == R.id.codeand_voter){
                        kycTypeValue = "QRCODE_VC_REQUEST"
                    }
                    else if(checkedId == R.id.pan_dl) {
                        kycTypeValue = "PAN_DL_REQUEST"
                        //Toast.makeText(this , "Currently System not supporting this request." , Toast.LENGTH_SHORT).show()
                    }else if(checkedId == R.id.rdbKycDocument)
                    {
                        kycTypeValue = "KYCDOCUMENT"
                    }
                })
        bindingDialog?.btnProceed?.setOnClickListener() {
            val leadAppNum = leadApplicantNumber
            val radioButtonselect = bindingDialog.groupRadioButton.checkedRadioButtonId
            if (radioButtonselect == R.id.adharotp) {
                encodedStringScanned = ""
                kycPresenter.callNetwork(ConstantsApi.CALL_KYC_PREPARE , dmiConnector = KYCidApiCall(leadIDnumber,"AADHAAR_ZIP_INLINE"))
            }else if (radioButtonselect == R.id.codeand_pan) {
                       scanNow()
            } else if(radioButtonselect == R.id.codeand_dl){
                      scanNow()
                //Toast.makeText(this , "Currently System working on Aadhar Otp and QR Code and PAN." , Toast.LENGTH_SHORT).show()
            }
            else if(radioButtonselect == R.id.codeand_voter){
                scanNow()
            }
            else if(radioButtonselect == R.id.pan_dl) {
                bundle = intent.extras
                bundle?.let {
                    val  leadAppNum = bundle?.getString(Constants.KEY_LEAD_APP_NUM)
                    leadAppNum?.let {
                        //showKycDialog(leadAppNum)
                        leadIDnumber= leadAppNum
                        //encodedStringScanned = ""
                        //kycPresenter.callNetwork(ConstantsApi.CALL_KYC_MOBILE_PREPARE , dmiConnector = KYCMobileApiCall(leadIDnumber,"PAN_DL_REQUEST","","false"))

                    }
                    encodedStringScanned = ""
                    kycPresenter.callNetwork(ConstantsApi.CALL_KYC_PREPARE , dmiConnector = KYCidApiCall(leadAppNum,"PAN_DL_REQUEST"))
                }
            }
            else if(radioButtonselect == R.id.selfDeclartion)
            {
                kycOptionDialog?.dismiss()
                showConfiramationDialog()
            }else if(radioButtonselect == R.id.rdbKycDocument)
            {
                dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(this, androidx.lifecycle.Observer { masterDrownDownValues ->
                    masterDrownDownValues?.let {
                        allMasterDropdown = it

                    }
                })
                allMasterDropdown?.let {
                    val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.KYC_DOCUMENT , true) }
                    val bundle = Bundle()
                    bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)
                    bundle.putString(Constants.KEY_TITLE , applicationContext.getString(R.string.kyc_auth_image))
                    bundle.putString(Constants.KEY_APPLICANT_NUMBER , leadAppNum)
                    PerformKycDocumentUploadActivity.startActivity(this@KYCActivity , bundle)
                }
            }
            else
            {
                showToast("Please Select one option")
            }
        }
    }
    private fun showConfiramationDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialoge_self_declaration)
        val checkBoxConfirm = dialog.findViewById(R.id.checkboxConfirmation) as CheckBox
        val yesBtn = dialog.findViewById(R.id.buttonOk) as Button
        val btnClose = dialog.findViewById(R.id.btnClose) as Button
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(this, androidx.lifecycle.Observer { masterDrownDownValues ->
            masterDrownDownValues?.let {
                allMasterDropdown = it

            }
        })

        yesBtn.setOnClickListener {
            var leadAppNum : String ? = null
            if(checkBoxConfirm.isChecked)
            {
                bundle = intent.extras
                bundle?.let {
                    leadAppNum = bundle?.getString(Constants.KEY_LEAD_APP_NUM)
                    System.out.println("leadAppNum>>>" + leadAppNum)
                }
                    allMasterDropdown?.let {
                        val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.KYC_DOCUMENT , true) }
                        val bundle = Bundle()
                        bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)
                        bundle.putString(Constants.KEY_TITLE , this.getString(R.string.face_auth_image))
                        bundle.putString(Constants.KEY_APPLICANT_NUMBER , leadAppNum)
                        SelfDeclarationUploadDocumentActivity.startActivity(this , bundle)
                        finish()
                    }
                dialog.dismiss()

            }
            else{
                Toast.makeText(this,"Please accept the declaration",Toast.LENGTH_LONG).show()
            }

        }
        btnClose.setOnClickListener{
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    private fun performKycAlert() {
        AlertDialog.Builder(this)
                .setTitle("Perform KYC")
                .setMessage("Do you want to send KYC link to Customer or Face-to-Face?")
                .setCancelable(false)
                .setNegativeButton("Customer") { _,_ ->
                   showKycMobileoption()

                }

                .setPositiveButton("Face-to-Face") { _, _ ->
                   // Show fae to face dialoge
                    proceedFurther()
                }.show()




    }


    inner class KYCApiCall(private val leadAppNum: String) : ViewGeneric<Requests.RequestKYC,
            Response.ResponseKYC>(context = this) {

        override val apiRequest: Requests.RequestKYC
            get() = mRequestKyc

        private val mRequestKyc: Requests.RequestKYC
            get() {
                val leadId = LeadMetaData.getLeadId()
                return Requests.RequestKYC(leadID = leadId, leadApplicantNumber = leadAppNum)
            }

        override fun getApiSuccess(value: Response.ResponseKYC) {
            if (value.responseCode == Constants.SUCCESS) {
                val response = value.responseObj
                response?.let {
                    openWebViewForKYCData(response.kycID)
//                    showToast("Success")
                    finish()
                }
            } else {
                getApiFailure(value.responseMsg)
            }
        }

        private fun openWebViewForKYCData(kycID: String?) {
            kycID?.let {
                //                CustomChromeTab().openUrl(activity = this@KYCActivity, url = (URL_KYC + kycID))
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(resources.getColor(R.color.colorPrimary))
                builder.setShowTitle(false)

                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(context , Uri.parse(Constants.API.URL.URL_KYC + kycID + "&qrCode=true"))
            }
        }

        override fun getApiFailure(msg: String) {
            showToast(msg)
        }
    }


    inner class KYCMobileApiCall(private val leadAppNum: String?,val kycType : String,val mobileNo : String,val isSmsSend : String,val leadId : String) : ViewGeneric<Requests.RequestKYCOnMobileId ,
            Response.ResponseKYC>(context = this) {
        val qrCodeString = ""
        override val apiRequest: Requests.RequestKYCOnMobileId
            get() = mRequestKyc

        private val mRequestKyc: Requests.RequestKYCOnMobileId
            get() {
                //val leadId = LeadMetaData.getLeadId()
                System.out.println("LeadId>>>>"+leadId)
                //QRCODE_PAN_REQUEST
                return Requests.RequestKYCOnMobileId(leadID = leadId , leadApplicantNumber = leadAppNum , qrCodeData = encodedStringScanned , kycType = kycType,mobileNumber = mobileNo,isSmsSend = isSmsSend)

            }

        override fun getApiSuccess(value: Response.ResponseKYC) {
            if (value.responseCode == Constants.SUCCESS) {
                showToast(value.responseMsg)
                finish()
            } else {
                getApiFailure(value.responseMsg)
            }
        }

        private fun openWebViewForPAN(kycID: String?) {
            kycID?.let {
                //                CustomChromeTab().openUrl(activity = this@KYCActivity, url = (URL_KYC + kycID))
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(resources.getColor(R.color.colorPrimary))
                builder.setShowTitle(false)

                val customTabsIntent = builder.build()
                //customTabsIntent.launchUrl(context , Uri.parse(Constants.API.URL.URL_KYC + kycID + "&qrCode=true"))
                customTabsIntent.launchUrl(context , Uri.parse(Constants.API.URL.URL_KYC + kycID))
            }
        }


        override fun getApiFailure(msg: String) {
            showToast(msg)
        }
    }

    inner class KYCidApiCall(private val leadAppNum: String?,val kycType : String) : ViewGeneric<Requests.RequestKYCID ,
            Response.ResponseKYC>(context = this) {
        val qrCodeString = ""
        var splitLeadId = "0"
        override val apiRequest: Requests.RequestKYCID
            get() = mRequestKyc

        private val mRequestKyc: Requests.RequestKYCID
            get() {
                System.out.println("leadAppNum>>>>"+leadAppNum)
                bundle = intent.extras
                bundle?.let {
                    val leadAppNum = bundle?.getString(Constants.KEY_LEAD_APP_NUM)
                    System.out.println("LeadApp Number>>>" + leadAppNum)
                    leadAppNum?.let {

                        leadIDnumber = leadAppNum
                        System.out.println("Leadidumber>>>" + leadIDnumber)
                        splitLeadId = leadIDnumber!!.split("_".toRegex())[0]

                        System.out.println("SplitleadId>>>" + splitLeadId)


                    }
                }
                //val leadId = LeadMetaData.getLeadId()
                //QRCODE_PAN_REQUEST

                return Requests.RequestKYCID(leadID = splitLeadId.toInt() , leadApplicantNumber = leadAppNum , qrCodeData = encodedStringScanned , kycType = kycType,latitude = latitude.toString(),longitude = longitude.toString())

                //return Requests.RequestKYCOnMobileId(leadID = splitLeadId , leadApplicantNumber = leadAppNum , qrCodeData = encodedStringScanned , kycType = kycType,mobileNumber = "",isSmsSend = "false")
            }

        override fun getApiSuccess(value: Response.ResponseKYC) {
            if (value.responseCode == Constants.SUCCESS) {
                val response = value.responseObj
                response?.let {

                    openWebViewForPAN(response.kycID)
                    kycOptionDialog!!.dismiss()
                    finish()

                }
            } else {
                getApiFailure(value.responseMsg)
            }
        }

        private fun openWebViewForPAN(kycID: String?) {
            kycID?.let {
                //                CustomChromeTab().openUrl(activity = this@KYCActivity, url = (URL_KYC + kycID))
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(resources.getColor(R.color.colorPrimary))
                builder.setShowTitle(false)

                val customTabsIntent = builder.build()
                //customTabsIntent.launchUrl(context , Uri.parse(Constants.API.URL.URL_KYC + kycID + "&qrCode=true"))
                customTabsIntent.launchUrl(context , Uri.parse(Constants.API.URL.URL_KYC + kycID))
            }
        }


        override fun getApiFailure(msg: String) {
            showToast(msg)
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }
}



