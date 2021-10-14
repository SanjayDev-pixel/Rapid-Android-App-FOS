package com.finance.app.view.activity

import android.Manifest
import android.Manifest.permission.ACCESS_WIFI_STATE
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.finance.app.R
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import javax.inject.Inject


@SuppressLint("ByteOrderMark")
class SplashScreen : BaseAppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil

    companion object {
        private const val SPLASH_SCREEN_TIME_OUT = 2000L
        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    }

    private val TAG = "PermissionDemo"
    private val RECORD_REQUEST_CODE = 101
    var perms = arrayOf("android.permission.INTERNET","android.permission.READ_PHONE_STATE","android.permission.ACCESS_WIFI_STATE","android.permission.CHANGE_WIFI_STATE","android.permission.CALL_PHONE","android.permission.CAMERA","android.permission.ACCESS_FINE_LOCATION","android.permission.WRITE_EXTERNAL_STORAGE")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)



    }

    override fun init() {
        ArchitectureApp.instance.component.inject(this)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { }
        if (checkAndRequestPermissions()) {
            Handler().postDelayed({

                if (sharedPreferences.isLogin()) {


                    if (sharedPreferences.getPasswordChangeRequired() == true) {
                        ResetPasswordActivity.start(this)
                    } else {
                        DashboardActivity.start(this)
                        //ResetPasswordActivity.start(this)
                    }

                } else {
                    val nextActivity = Intent(
                            this ,
                            LoginActivity::class.java
                    )
                    startActivity(nextActivity)
                }
                finish()
            } , SPLASH_SCREEN_TIME_OUT)
        }
    }



    private fun checkAndRequestPermissions(): Boolean {
        val camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val writepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val permissionReadPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
        val changeWifiState = ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
        val accessWifiState = ContextCompat.checkSelfPermission(this, ACCESS_WIFI_STATE)
        val call_Phone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)




        val listPermissionsNeeded = ArrayList<String>()

        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        }
        if(accessWifiState != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.ACCESS_WIFI_STATE)
        }
        if(call_Phone != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.CHANGE_WIFI_STATE)
        }
        if(changeWifiState != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        Log.d(TAG, "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_PHONE_STATE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_WIFI_STATE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.CHANGE_WIFI_STATE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.CALL_PHONE] = PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.size > 0) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.READ_PHONE_STATE] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.ACCESS_WIFI_STATE] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.CHANGE_WIFI_STATE] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.CALL_PHONE] == PackageManager.PERMISSION_GRANTED


                            ) {
                        Log.d(TAG, " permission granted")
                        // process the normal flow

                        Handler().postDelayed({

                            if (sharedPreferences.isLogin()) {

                                if (sharedPreferences.getPasswordChangeRequired() == true) {
                                    ResetPasswordActivity.start(this)
                                } else {
                                    DashboardActivity.start(this)
                                }

                            } else {
                                val nextActivity = Intent(
                                        this ,
                                        LoginActivity::class.java
                                )
                                startActivity(nextActivity)
                            }
                            finish()
                        } , SPLASH_SCREEN_TIME_OUT)


                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ")
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_WIFI_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CHANGE_WIFI_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)

                        ) {
                            showDialogOK("Service Permissions are required for this app",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                            DialogInterface.BUTTON_NEGATIVE ->
                                                // proceed with logic by disabling the related features or quit the app.
                                                finish()
                                        }
                                    })
                        } else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }//permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                    }
                }
            }
        }

    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun explain(msg: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(msg)
                .setPositiveButton("Yes") { paramDialogInterface, paramInt ->
                    //  permissionsclass.requestPermission(type,code);
                    startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("com.finance.app")))
                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> finish() }
        dialog.show()
    }

}