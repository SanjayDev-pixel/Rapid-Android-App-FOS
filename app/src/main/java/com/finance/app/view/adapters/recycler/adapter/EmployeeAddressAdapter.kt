package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.persistence.model.EmployeeByManager
import com.finance.app.persistence.model.FAQ
import com.finance.app.persistence.model.LocationHistory
import com.finance.app.utility.ConvertDate
import com.github.mikephil.charting.charts.Chart
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class EmployeeAddressAdapter ( val context: Context, private  val arrayListLatlng :ArrayList<LocationHistory>) : RecyclerView.Adapter<EmployeeAddressAdapter.ViewHolder>(){
    var address1 = ""
    var address2 = ""
    var city = ""
    var state = ""
    var country = ""
    var county = ""
    var PIN = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeAddressAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.bottom_sheet_address_item, parent, false)
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        System.out.println("Size of Array>>>>"+arrayListLatlng.size)
        return arrayListLatlng.size

    }

    override fun onBindViewHolder(holder: EmployeeAddressAdapter.ViewHolder , position: Int) {
        holder.bindItems(arrayListLatlng[position])
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(locationHistory: LocationHistory) {
            val address_tv = itemView.findViewById<TextView>(R.id.txtAddress)
            val time_tv=itemView.findViewById<TextView>(R.id.txtTime)
            val progressBar= itemView.findViewById<ProgressBar>(R.id.progress_Bar) as ProgressBar
            System.out.println("latitude>>>"+locationHistory.latitude+" longitude>>>"+locationHistory.longitude)
                //var address=convertLatLongtoAddress(locationHistory.latitude,locationHistory.longitude)
                 //   System.out.println("qwerty"+address +"latitude>>>"+locationHistory.latitude)
            LoadingAddress(address_tv,locationHistory,progressBar).execute()
                    time_tv.text=ConvertDate().convertDate(locationHistory.timeStamp)





        }
    }
    private fun convertLatLongtoAddress(latitude : Double, longitude : Double):String?{

        var strAdd = ""
        val geocoder = Geocoder(context, Locale.getDefault())

        try {

            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
            val strAddress = addresses[0].getAddressLine(0)
            //val strAddress ="Dummy Address"
            val strCity = addresses[0].locality
            var strState = addresses[0].adminArea
            var strCountry = addresses[0].countryName
            var strPincode = addresses[0].postalCode

            strAdd = strAddress + " " + strCountry + " " + strState + " " + strCity + " " + strPincode
            System.out.println("Geocoder address>>>>"+strAdd)
           /* if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.getMaxAddressLineIndex()) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                System.out.println("Geocoder address>>>>"+strAdd)}*/


        } catch (e: Exception) {
            e.printStackTrace()

        }

        return strAdd

    }
    inner class LoadingAddress(private val addressTv:TextView,private  val locationHistory: LocationHistory,private val progressBar:ProgressBar) : AsyncTask<Void, Void, String>() {
        var name :String  ? = null
        override fun onPreExecute() {
            progressBar!!.visibility=View.VISIBLE

        }

        override fun doInBackground(vararg p0: Void?): String? {
            var listofAddress : ArrayList<String> = ArrayList()
            var stringAddress : String ? = null
           // for (i in  arrayListLatlng.indices) {

               // System.out.println("showddress>>>>"+arrayListLatlng[i].latitude)
                stringAddress= retrieveData(locationHistory.latitude,locationHistory.longitude)
               // listofAddress.add(stringAddress!!)

           // }
            return stringAddress
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
           // for(i in result?.indices!!) {
             //   Log.i("Full Address" ,result[i])
               // System.out.println("Full Address of latLong " + result[i]+"size of Array>>>>"+result.size)
            progressBar!!.visibility=View.GONE
                addressTv.text= result

            //}

        }
    }
    private fun getResponseFromHttpUrl(url: URL) : String{
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        return try {
            val instream: InputStream = urlConnection.getInputStream()
            val scanner = Scanner(instream)
            scanner.useDelimiter("\\A")
            if (scanner.hasNext()) {
                return scanner.next()
            } else {
                return "null"
            }
        } finally {
            urlConnection.disconnect()
        }
    }
    private fun init(){

    }
    private fun createUrl(latitude: Double, longitude: Double): String {
        //init()
        return "https://maps.googleapis.com/maps/api/geocode/json?" + "latlng=" + latitude + "," + longitude + "&key=" + context.getResources().getString(R.string.map_key)
    }
    private fun buildUrl(latitude: Double, longitude: Double): URL? {
        try {
            Log.w("EmployeeTracking", "buildUrl: " + createUrl(latitude, longitude))
            return URL(createUrl(latitude, longitude))
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            Log.e(Chart.LOG_TAG, "can't construct location object")
            return null
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }
    //Retrive Address
    private fun retrieveData(latitude: Double, longitude: Double): String? {
        var strings: String ? = null
        try {
            val responseFromHttpUrl: String = buildUrl(latitude, longitude)?.let { getResponseFromHttpUrl(it) }!!
            val jsonResponse = JSONObject(responseFromHttpUrl)
            val status = jsonResponse.getString("status")
            if (status.equals("OK", ignoreCase = true)) {
                val results = jsonResponse.getJSONArray("results")
                val zero = results.getJSONObject(0)
                val addressComponents = zero.getJSONArray("address_components")
                val formatadd = zero.getString("formatted_address")
                for (i in 0 until addressComponents.length()) {
                    val zero2 = addressComponents.getJSONObject(i)
                    val longName = zero2.getString("long_name")
                    val types = zero2.getJSONArray("types")
                    val type = types.getString(0)
                    if (!TextUtils.isEmpty(longName)) {
                        if (type.equals("street_number", ignoreCase = true)) {
                            address1 = "$longName "
                        } else if (type.equals("route", ignoreCase = true)) {
                            address1 = address1.toString() + longName
                        } else if (type.equals("sublocality", ignoreCase = true)) {
                            address2 = longName
                        } else if (type.equals("locality", ignoreCase = true)) { // address2 = address2 + longName + ", ";
                            city = longName
                        } else if (type.equals("administrative_area_level_2", ignoreCase = true)) {
                            county = longName
                        } else if (type.equals("administrative_area_level_1", ignoreCase = true)) {
                            state = longName
                        } else if (type.equals("country", ignoreCase = true)) {
                            country = longName
                        } else if (type.equals("postal_code", ignoreCase = true)) {
                            PIN = longName
                        }
                    }
                }
                strings = address1+" "+address2+" "+city+" "+county+" "+state +" "+PIN

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strings
    }
}