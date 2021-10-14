package com.finance.app.camera

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.finance.app.R
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.DocumentTypeModel
import com.finance.app.persistence.model.KycDocumentModel
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.activity.PerformKycDocumentUploadActivity
import com.finance.app.view.activity.SelfDeclarationUploadDocumentActivity
import com.finance.app.view.utils.getImageUriForImagePicker
import com.finance.app.workers.document.UploadDocumentWorker
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.otaliastudios.cameraview.FileCallback
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.size.AspectRatio
import kotlinx.android.synthetic.main.activity_picture_preview.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.development.interfaces.DataBaseUtil
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import javax.inject.Inject

class PicturePreviewActivity : AppCompatActivity() {
    @Inject
    lateinit var dataBase: DataBaseUtil
    private var documentID : Int ? = null
    private var documentName : String ? = null
    private var screenTitle: String? = ""
    private var moduleName : String ? =""
    private var docCodeId: Int? = null
    private var applicantNumber: String? = null
    private var applicationDocumentID: String? = null
    private var selectedDocumentUri: Uri? = null
    private var allMasterDropdown: AllMasterDropDown? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL = (2 * 1000).toLong()  /* 10 secs */
    private var latitude : Double ? = null
    private var longitude : Double ? = null
    companion object {
        var pictureResult: PictureResult? = null
        const val DOCUMENT_UPLOAD_BUNDLEPREVIEW = "document_upload_bundle"
        fun startActivity(context: Context, bundle: Bundle) {
            val intent = Intent(context , PicturePreviewActivity::class.java)
            intent.putExtra(DOCUMENT_UPLOAD_BUNDLEPREVIEW , bundle)
            context.startActivity(intent)
        }
    }
    private fun startLocationClient() {
        fusedLocationProviderClient?.requestLocationUpdates(locationRequest , locationCallback , null)
        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener { location -> Log.i("TAG" , "Latitude:- ${location?.latitude} and Longitude:- ${location?.longitude}") }
        fusedLocationProviderClient?.lastLocation?.addOnFailureListener { exception -> Log.i("TAG" , "Exception while getting the location ${exception.message}") }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_preview)
        ArchitectureApp.instance.component.inject(this)
        //init fused api client.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //init location request based on need.
        locationRequest = LocationRequest.create()
        locationRequest?.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY //will return only when if GPS or network is  active...
        locationRequest?.interval = UPDATE_INTERVAL
        getBundleData()
        showLocationPrompt()
        val result = pictureResult ?: run {
            finish()
            return
        }
        System.out.println("Size>>>>"+result.data.size +"2snd"+result.facing+"3rd>>>"+result.location)
        val imageView = findViewById<ImageView>(R.id.image)


        //val captureResolution = findViewById<MessageView>(R.id.nativeCaptureResolution)
        //val captureLatency = findViewById<MessageView>(R.id.captureLatency)
        //val exifRotation = findViewById<MessageView>(R.id.exifRotation)

        //val delay = intent.getLongExtra("delay", 0)
        val ratio = AspectRatio.of(result.size)
        System.out.println("Ratio is>>>>"+ratio)
        //captureLatency.setTitleAndMessage("Approx. latency", "$delay milliseconds")
        //captureResolution.setTitleAndMessage("Resolution", "${result.size} ($ratio)")
        //exifRotation.setTitleAndMessage("EXIF rotation", result.rotation.toString())

       /* try {
            result.toBitmap(1000, 1000) { bitmap -> imageView.setImageBitmap(bitmap) }
        } catch (e: UnsupportedOperationException) {
            imageView.setImageDrawable(ColorDrawable(Color.GREEN))
            Toast.makeText(this, "Can't preview this format: " + result.getFormat(), Toast.LENGTH_LONG).show()
        }*/
        if (result.isSnapshot) {
            // Log the real size for debugging reason.
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(result.data, 0, result.data.size, options)
            result.toBitmap(result.size.width,result.size.height){bitmap ->
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                    if (android.os.Build.VERSION.SDK_INT >= 29) {
                        val values = contentValues()
                        values.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/DMIRAPIDKYC")
                        values.put(MediaStore.Images.Media.IS_PENDING, true)
                        // RELATIVE_PATH and IS_PENDING are introduced in API 29.

                        val uri: Uri? = applicationContext.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                        selectedDocumentUri = uri
                        Log.i("PicturePreviewSanjayURI", "The picture full size is ${result.size.height}x${result.size.width}"+"URI<>>"+selectedDocumentUri)
                        if (uri != null) {
                            saveImageToStream(bitmap, applicationContext.contentResolver.openOutputStream(uri))
                            values.put(MediaStore.Images.Media.IS_PENDING, false)
                            applicationContext.contentResolver.update(uri, values, null, null)
                        }
                    }else {
                        val directory = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()+File.separator +"DMIRAPIDKYC")
                        System.out.println("Directory>>>>>"+directory)
                        //val directory = File(Environment.getExternalStorageDirectory().toString() + File.separator + "DMIRAPIDKYC")
                        // getExternalStorageDirectory is deprecated in API 29

                        if (!directory.exists()) {
                            directory.mkdirs()
                        }
                        val fileName = System.currentTimeMillis().toString() + ".jpg"
                        val file = File(directory, fileName)
                        Log.i("PicturePreviewSanjay", "The picture full size is ${result.size.height}x${result.size.width}")
                        result.toFile(file) { file ->
                            MediaScannerConnection.scanFile(this, arrayOf(file?.absolutePath), null
                            )
                            { path, uri ->
                                Log.i("onScanCompleted", uri.path)
                                selectedDocumentUri = uri
                            }


                        }
                    }
                    //saveImage(bitmap,this,"DMIRAPID")
                }
            }
        }
        btn_use.setOnClickListener {
            saveKycDocumentIntoDatabase()
        }
        btn_retake.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.KEY_DOC_ID , docCodeId!!)
            bundle.putString(Constants.KEY_TITLE , this.getString(R.string.face_auth_image))
            bundle.putString(Constants.KEY_APPLICANT_NUMBER , applicantNumber)
            bundle.putString(Constants.KEY_DOCUMENTID , documentID.toString())
            bundle.putString(Constants.KEY_DOCUMENTNAME , documentName)
            CameraActivity.startActivity(this,bundle)
            finish()
        }
        //Location Callback....
        startLocationClient()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LocationRequest.PRIORITY_HIGH_ACCURACY -> {
                if (resultCode == Activity.RESULT_OK) {
                    Log.e("Status: ","On"+"value>>>")
                } else {
                    Log.e("Status: ","Off")
                }
            }
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
    /// @param folderName can be your app's name
    private fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, folderName)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            selectedDocumentUri = uri
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            selectedDocumentUri = this@PicturePreviewActivity.getImageUriForImagePicker("doc_image_${Date().time}")
            //val values = ContentValues()
            //values.put
            /*val directory = File(Environment.getExternalStorageDirectory().toString() + File.separator + folderName)
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString()
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            if (file.absolutePath != null) {
                val values = ContentValues()
                //values.put(MediaStore.EXTRA_OUTPUT,Uri.parse(file.absolutePath))
                values.put(MediaStore.Images.Media.TITLE, fileName)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
            selectedDocumentUri = this@PicturePreviewActivity.getImageUriForImagePicker(fileName)*/
            //selectedDocumentUri = Uri.parse(file.absolutePath)
            System.out.println("selectedDocumentUri>>>"+selectedDocumentUri)
        }
    }
    private fun saveKycDocumentIntoDatabase() {
        GlobalScope.launch {
            dataBase.provideDataBaseSource().kycDocumentDao().truncate()
        }
        val kycDocumentModel = KycDocumentModel()
        kycDocumentModel.leadID = LeadMetaData.getLeadId()
        kycDocumentModel.leadApplicantNumber = applicantNumber
        kycDocumentModel.applicationDocumentID = applicationDocumentID
        kycDocumentModel.documentID = documentID
        kycDocumentModel.documentName = documentName
        kycDocumentModel.document = selectedDocumentUri.toString()
        System.out.println("SelctedDocumntURI"+selectedDocumentUri.toString())
        //kycDocumentModel.Active = 1
        kycDocumentModel.moduleName =Constants.SELF_DECLARATION
        kycDocumentModel.latitude = latitude.toString()
        kycDocumentModel.longitude = longitude.toString()
        //Now Save data into database,then start worker...
        val handler = Handler(Looper.getMainLooper())
        GlobalScope.launch {
            dataBase.provideDataBaseSource().kycDocumentDao().add(kycDocumentModel)
            startDocumentWorkerTask()
            handler.post {
                Toast.makeText(this@PicturePreviewActivity , "Document Saved Successfully" , Toast.LENGTH_LONG).show()
            }
        }
        System.out.println("ScreenTitle>>>>"+screenTitle)
        if(moduleName == "KYCDocument"){
            val bundle = Bundle()
            bundle.putInt(Constants.KEY_DOC_ID , docCodeId!!)
            bundle.putString(Constants.KEY_TITLE , this.getString(R.string.kyc_auth_image))
            bundle.putString(Constants.KEY_APPLICANT_NUMBER , applicantNumber)
            PerformKycDocumentUploadActivity.startActivity(this , bundle)
            finish()
        }else {
            val bundle = Bundle()
            bundle.putInt(Constants.KEY_DOC_ID, docCodeId!!)
            bundle.putString(Constants.KEY_TITLE, this.getString(R.string.face_auth_image))
            bundle.putString(Constants.KEY_APPLICANT_NUMBER, applicantNumber)
            SelfDeclarationUploadDocumentActivity.startActivity(this, bundle)
            finish()
        }

    }
    private fun startDocumentWorkerTask() {

        val mWorkManager = WorkManager.getInstance()
        val mRequest = OneTimeWorkRequest.Builder(UploadDocumentWorker::class.java).build()
        val data = Data.Builder()

        mWorkManager.enqueue(mRequest)
    }

    private fun contentValues() : ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun getBundleData() {
        val bundle = intent.getBundleExtra(DOCUMENT_UPLOAD_BUNDLEPREVIEW)
        bundle?.let {

            screenTitle = it.getString(Constants.KEY_TITLE)
            moduleName = it.getString(Constants.KEY_MODULE_NAME)
            documentID = it.getString(Constants.KEY_DOCUMENTID)?.toInt()
            documentName = it.getString(Constants.KEY_DOCUMENTNAME)
            tv_title.text = documentName
            docCodeId = it.getInt(Constants.KEY_DOC_ID)
            applicantNumber = it.getString(Constants.KEY_APPLICANT_NUMBER)
            if (it.containsKey(Constants.KEY_FORM_ID)) {
                applicationDocumentID = it.getString(Constants.KEY_FORM_ID)
            }
        }
        System.out.println("documentID>>>"+documentID+"documentName>>"+documentName+"applicantNumber>>>"+applicantNumber+"applicationDocumentID>>>"+applicationDocumentID)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            pictureResult = null
        }
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }



}