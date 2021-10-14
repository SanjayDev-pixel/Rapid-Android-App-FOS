package com.finance.app.view.activity


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.persistence.model.EmployeeByManager
import com.finance.app.persistence.model.LocationHistory
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.view.adapters.recycler.adapter.EmployeeAddressAdapter
import com.finance.app.view.adapters.recycler.adapter.EmployeeTrackAdapter
import com.github.mikephil.charting.charts.Chart.LOG_TAG
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_employee_tracking.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import org.json.JSONObject
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class EmployeeTrackingActivity : FragmentActivity(), OnMapReadyCallback {
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    private val presenter = Presenter()
    var Employee = ArrayList<EmployeeByManager>()/* arrayOf("Nikita","Sanjay","Devendra","Vivek", "Other")*/
    private var progressBar: ProgressBar? = null
   // var rcvAddress:RecyclerVemployeeLatLongListiew?=null
    var employeeLatLongList : ArrayList<LatLng> = ArrayList()
    private lateinit var mMap : GoogleMap
    var responseLocationHistory :ArrayList<LocationHistory> = ArrayList()
    var responseTempLatlong : ArrayList<LatLng> = ArrayList()

    var userID : Int ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_tracking) // is layout me recycler view kaha hai
        responseTempLatlong.add(LatLng(25.1933895, 66.5949836))
        responseTempLatlong.add(LatLng(26.862863,82.1455639))
        responseTempLatlong.add(LatLng(15.961329, 48.438083))

        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(this@EmployeeTrackingActivity)
        //fetchLocation()
        var btAddress= findViewById<FloatingActionButton>(R.id.fab)
        // rcvAddress= findViewById<RecyclerView>(R.id.rvEmplyeeAddress)
        progressBar = findViewById<ProgressBar>(R.id.progress_Bar) as ProgressBar
        progressBar!!.visibility=View.VISIBLE
        presenter.callNetwork(ConstantsApi.CALL_EMPLOYEE_BY_MANAGER,callEmployeeByManager())


        btAddress.setOnClickListener {

            /*var loadingProducts = object : LoadingProducts() {
                override fun onPostExecute(result: String?) {
                    super.onPostExecute(result)

                    Log.e("Result", result)
                }
            }

            loadingProducts.execute()*/

            showBottomSheetDialog()
           /* for(i in 0 until responseTempLatlong.size) {
                var address=convertLatLongtoAddress(responseLocationHistory[i].latitude, responseLocationHistory[i].longitude)
                System.out.println("address check>>>"+address)
            }*/
        }


    }
    private fun zoomRouteonMap(map: GoogleMap,lstLatLng : ArrayList<LatLng>){
        if(map == null || lstLatLng == null || lstLatLng.isEmpty())
        {
            return
        }
        var boundBuilder = LatLngBounds.builder()
        for(latlngPoint : LatLng in lstLatLng){
            boundBuilder.include(latlngPoint)
        }
         var routePadding = 100
        var latLngBounce = boundBuilder.build()
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounce,routePadding))
    }
    private fun drawPolyLine(map: GoogleMap,lstLatLng : ArrayList<LatLng>){
        val polyOptions = PolylineOptions()
        polyOptions.color(Color.GRAY)
        polyOptions.width(5.0f)
        polyOptions.addAll(lstLatLng)

       // map.clear()
        map.addPolyline(polyOptions)

        val builder= LatLngBounds.Builder()
        for (latLng in lstLatLng) {
            builder.include(latLng)
        }

        val bounds = builder.build()

        //BOUND_PADDING is an int to specify padding of bound.. try 100.

        //BOUND_PADDING is an int to specify padding of bound.. try 100.
        val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
        map.animateCamera(cu)
    }


    private fun showBottomSheetDialog() {
       /* for(i in responseTempLatlong.indices) {
            GeoCoderUtils().execute(this,responseTempLatlong[i].latitude.toString(),responseTempLatlong[i].longitude.toString(),object:
            LoadDataCallback<LocationModel>{
                override fun onDataLoaded(response: LocationModel) {
                    System.out.println("Response Data>>>>"+response.locationAddress)

                }
                override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                    System.out.println("Response Data else>>>"+errorCode)
                }
            })
        }*/
        val bottomSheetDialog = BottomSheetDialog(this)
        val view =
                View.inflate(this, R.layout.bottom_sheet_address, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
        val rcvAddress : RecyclerView = view?.findViewById(R.id.rvEmplyeeAddress)!!
        System.out.println("btnddress CAlled>>>>"+responseLocationHistory.size)
        val employeeAddressAdapter= EmployeeAddressAdapter(applicationContext,responseLocationHistory)
        rcvAddress.adapter=employeeAddressAdapter
    }
    //


    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager  .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                Toast.makeText(applicationContext, currentLocation.latitude.toString() + "" +
                        currentLocation.longitude, Toast.LENGTH_SHORT).show()
                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.myMap) as
                        SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this@EmployeeTrackingActivity)
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap?) {
        if(googleMap != null){
            mMap = googleMap
        }
        googleMap?.clear()

        System.out.println("EmployeeArrayListSDize>>>>"+employeeLatLongList.size)
        for( i in employeeLatLongList.indices){
            googleMap?.addMarker(MarkerOptions()
                    .position(employeeLatLongList[i])
                    .icon(vectorToBitmapConvertor(this,R.drawable.ic_location_red)))
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(employeeLatLongList[i],20.0F))


        }
        //zoomRouteonMap(googleMap!!,employeeLatLongList)
        if(employeeLatLongList.size  > 0) {
            drawPolyLine(googleMap!!, employeeLatLongList)
        }//click listner kaha lagayi ho
      /*  val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("I am here!")
        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
        googleMap?.addMarker(markerOptions)*/
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        when (requestCode) {
            permissionCode ->if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
            //fetchLocation()
        }
        }
    }


    //Convert vector image o bitmap
    private fun vectorToBitmapConvertor(context: Context,vectorResId : Int): BitmapDescriptor?{
        return ContextCompat.getDrawable(context,vectorResId)?.run {
            setBounds(0,0,intrinsicWidth,intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth,intrinsicHeight,Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
    inner class callEmployeeByManager : ViewGeneric<String?, Response.ResponseEmployeeByManager>(context = this) {
        override val apiRequest: String?
            get() = null

        override fun getApiSuccess(value: Response.ResponseEmployeeByManager) {
            if (value.responseCode == Constants.SUCCESS) {
                System.out.println("Responseee>>>>" + value.responseObj.size)
                //Employee.clear() // ye kis lue kiye ho
                //Employee.addAll(value.responseObj)
                setEmployeeRecycler(value.responseObj)
                progressBar!!.visibility=View.GONE

            } else {
                showToast("Try after some time!!")
                progressBar!!.visibility=View.GONE
            }
        }

    }
    private fun setEmployeeRecycler(employee:ArrayList<EmployeeByManager>){
        val adapter = ArrayAdapter<EmployeeByManager>(this,android.R.layout.simple_list_item_1,employee)
        /*val adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, employee)*/
        autoComplte_employee.threshold = 1
        autoComplte_employee.setAdapter(adapter)
        autoComplte_employee.setOnItemClickListener{parent,view,position,id ->
            userID = (parent.getItemAtPosition(position) as EmployeeByManager).userID
            System.out.println("Sanjay User Id>>>>"+userID)
            presenter.callNetwork(ConstantsApi.CALL_LOCATION_HISTORY,callLocationHistory(userID!!))
        }


    }



    inner class callLocationHistory(private val userId: Int) : ViewGeneric<Requests.RequestLocationHistory, Response.ResponseLocationHistory>(context = this) {
        override val apiRequest: Requests.RequestLocationHistory?
            get() = mRequestLocationHistory
        private  val mRequestLocationHistory:Requests.RequestLocationHistory
            get() {
                val UserId=userId
                return Requests.RequestLocationHistory(userID = UserId)
            }

        override fun getApiSuccess(value: Response.ResponseLocationHistory) {
            if (value.responseCode == Constants.SUCCESS) {
                System.out.println("Response location history>>>>" + value.responseObj.size)
                progressBar!!.visibility=View.GONE
               /* var googleMap: GoogleMap?=null*/
                employeeLatLongList.clear()
                responseLocationHistory.clear()
                for( i in value.responseObj.indices){
                    value.responseObj[i].latitude?.let { value.responseObj[i].longitude?.let { it1 -> LatLng(it,it1) } }?.let {
                        employeeLatLongList.add(it)
                    }
                }
                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.myMap) as
                        SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this@EmployeeTrackingActivity)
                responseLocationHistory.addAll(value.responseObj)

                if(value.responseObj.size==0)
                {
                    showPopWindow()
                }


            } else {
                showToast("not called")
            }
        }

    }

    private fun showPopWindow() {
        val window = PopupWindow(this)
        val view = layoutInflater.inflate(R.layout.popup_employee_track, null)
        window.contentView = view
        window.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        window.setFocusable(true);
        window.setOutsideTouchable(false);
        window.showAtLocation(view, Gravity.CENTER, 0, 0);
    }








}