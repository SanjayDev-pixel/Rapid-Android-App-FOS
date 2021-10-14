package com.finance.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.app.databinding.ActivityReportBinding
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.view.activity.CreateLeadActivity
import com.finance.app.view.adapters.recycler.adapter.DashboardChartAdapter
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate

class Report : BaseAppCompatActivity(){
    private var adapter: ReportAdapter?=null
    private val binding:ActivityReportBinding by ActivityBindingProviderDelegate(this,R.layout.activity_report)
   // private var data= arrayListOf(Response.ResponseApplicationReport("xyz","123","delhi","active","nik"),Response.ResponseApplicationReport("abc","456","pune","inActive","nikki"))
    private  var data:ArrayList<ResponseReportData>? =null
    private val reportPresenter = Presenter()


    override fun init() {
         ArchitectureApp.instance.component.inject(this)
        hideSecondaryToolbar()
        //initReportAdapter(data)
        binding.btnGo.setOnClickListener{
            //Here you will connect the Api
            if(binding.EdReportApplicationNum.text.toString().equals(""))
            {
                Toast.makeText(applicationContext,"Application number can not be left blank",Toast.LENGTH_LONG).show()
            }else{
               //Progressbar
binding.progressBar.visibility=View.VISIBLE
                reportPresenter.callNetwork(ConstantsApi.CALL_REPORT,dmiConnector = ReportApiCall())
                //Now run the application see the log
            }

        }




    }
    inner class  ReportApiCall : ViewGeneric<Requests.RequestReport,Response.ResponseApplicationReport>(context = this){
        override val apiRequest: Requests.RequestReport?
            get() = mRequestReport
         private  val mRequestReport:Requests.RequestReport
       get() {
            val ScreenName="LoanInformation"
           val SearchKey=binding.EdReportApplicationNum.text.toString()
            return Requests.RequestReport(screenName = ScreenName,searchKey = SearchKey)
        }

        override fun getApiSuccess(value: Response.ResponseApplicationReport) {
            if(value.responseCode==Constants.SUCCESS) {
               binding.progressBar.visibility=View.GONE
                if (value.responseObj.size > 0) {
                    data = value.responseObj
                    initReportAdapter(data!!)
                    System.out.println("Api called >>>>" + value.responseObj)
                    System.out.println("Api called >>>>" + value.responseMsg.toString())
                }
                else{
                    showAlert()
                }
            }
            else
            {
                showToast("Not called")
            }
        }

    }

    private fun showAlert() {
        AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("You have entered wrong GG Number!")
                .setCancelable(false)
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("ok") { _, _ ->
                   binding.EdReportApplicationNum.setText("")
                }.show()
    }


    private fun initReportAdapter(data:ArrayList<ResponseReportData>) {
        //  data.add( Response.ResponseApplicationReport("xyz","123","delhi","active","nik"))


        adapter = ReportAdapter(mContext = this,data=data)

        binding.recyclerApplicationReport.addItemDecoration(DividerItemDecoration(this , LinearLayoutManager.VERTICAL))
        binding.recyclerApplicationReport.adapter = adapter

    }



}
