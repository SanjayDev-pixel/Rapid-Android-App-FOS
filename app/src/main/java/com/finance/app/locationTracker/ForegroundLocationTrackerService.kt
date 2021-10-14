package com.finance.app.locationTracker

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.finance.app.R
import com.finance.app.persistence.model.LocationTrackerModel
import com.finance.app.view.activity.SplashScreen
import com.finance.app.workers.location.UploadLocationWorker
import com.google.android.gms.location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import java.util.*
import javax.inject.Inject

/**
 * @author Sanjay Kumar
 */
class ForegroundLocationTrackerService : Service() { //TODO develop start and stop functionality for this service....
    private val TAG = ForegroundLocationTrackerService::class.java.name

    private val LOCATION_UPDATE_INTERVAL = 1000L * 60L * 30L // 15 Minutes...

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null

    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    var action : String ? = null
     var leadId : Int ? = null
    var oneTimeCounter : Int ? = 0

    //Location Callback....
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            if (locationAvailability.isLocationAvailable) Log.i(TAG , "Location is available")
            else Log.i(TAG , "Location is un-available"+sharedPreferences.getUserId())
        }

        override fun onLocationResult(locationResult: LocationResult) {
            Log.i(TAG , "Latitude:- ${locationResult.lastLocation?.latitude} and Longitude:- ${sharedPreferences.getUserId()}")
                if(oneTimeCounter == 0)
                {
                    leadId =null
                    action =""
                }
            //Now insert location into database...
            locationResult.lastLocation?.let { lastLocation ->
                val locationTrackerModel = LocationTrackerModel().apply {
                    userId = sharedPreferences.getUserId()
                    leadID = leadId.toString()
                    latitude = lastLocation.latitude.toString()
                    longitude = lastLocation.longitude.toString()
                    event = action
                    timestamp = Date().time.toString()
                    oneTimeCounter = 0
                }

                GlobalScope.launch {
                    dataBase.provideDataBaseSource().locationTrackerDao().add(locationTrackerModel)
                    startLocationWorkerTask()
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        ArchitectureApp.instance.component.inject(this)

        //init fused api client.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //init location request based on need.
        locationRequest = LocationRequest.create()
        locationRequest?.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY //will return only when if GPS or network is  active...
        locationRequest?.interval = LOCATION_UPDATE_INTERVAL

    }

    override fun onStartCommand(intent: Intent? , flags: Int , startId: Int): Int {
        createNotification()
        startLocationClient()
        action = intent?.action
        leadId = intent?.getIntExtra("LEADID",0)
        oneTimeCounter = 1
        System.out.println("LeadId>>>>>"+oneTimeCounter)

        return START_STICKY
    }

    private fun createNotification() {
        val intent = Intent(this , SplashScreen::class.java)
        val pendingIntent = PendingIntent.getActivity(this , 0 , intent , 0) //TODO set clear stack flag...

        val notification = NotificationCompat.Builder(this , ArchitectureApp.LOCATION_NOTIFICATION_CHANNEL_ID)
                .setContentTitle("REAL-TIME AGILE PRODUCTIVE IMPROVISED DIGITAL")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()

        startForeground(1 , notification)
    }

    private fun startLocationClient() {
        fusedLocationProviderClient?.requestLocationUpdates(locationRequest , locationCallback , null)
        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener { location -> Log.i(TAG , "Latitude:- ${location?.latitude} and Longitude:- ${location?.longitude}") }
        fusedLocationProviderClient?.lastLocation?.addOnFailureListener { exception -> Log.i(TAG , "Exception while getting the location ${exception.message}") }
    }

    private fun startLocationWorkerTask() {
        val mWorkManager = WorkManager.getInstance()
        val mRequest = OneTimeWorkRequest.Builder(UploadLocationWorker::class.java).build()
        mWorkManager.enqueue(mRequest)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
        stopForeground(true)
    }

}