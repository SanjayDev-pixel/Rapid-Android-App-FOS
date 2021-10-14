package com.finance.app.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.databinding.ActivityFaqBinding
import com.finance.app.databinding.ActivityReportBinding
import com.finance.app.persistence.model.FAQ
import com.finance.app.persistence.model.LoanProductMaster
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.view.adapters.recycler.adapter.FAQDetailAdapter
import com.finance.app.viewModel.AppDataViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import motobeans.architecture.appDelegates.ViewModelType
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate

import javax.inject.Inject

class FaqActivity : BaseAppCompatActivity() {
    private val binding: ActivityFaqBinding by ActivityBindingProviderDelegate(this, R.layout.activity_faq)
    private val presenter = Presenter()
    private var listFaqDB : ArrayList<FAQ> = ArrayList()

    @Inject
    lateinit var dataBase: DataBaseUtil

    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        hideSecondaryToolbar()
       // presenter.callNetwork(ConstantsApi.CALL_FAQ, CallFAQ())
        getFAQFromDB()

    }
// data is inserted into db  when dashboard is loaded in syncdataviewmodel
   /* inner class CallFAQ : ViewGeneric<String?, Response.ResponseFAQ>(context = this) {
        override val apiRequest: String?
            get() = null

        override fun getApiSuccess(value: Response.ResponseFAQ) {
            if (value.responseCode == Constants.SUCCESS) {
                System.out.println("Response>>>>" + value.responseObj.size)
                saveFAQIntoDatabse(value)
                getFAQFromDB()

            } else {
                showToast("Not called")
            }
        }

    }

    private fun saveFAQIntoDatabse(value: Response.ResponseFAQ) {
        GlobalScope.launch {
            dataBase.provideDataBaseSource().faqDao().deleteFAQ()
            dataBase.provideDataBaseSource().faqDao().insertFAQ(value.responseObj)
        }
    }*/

    private fun getFAQFromDB() {
        dataBase.provideDataBaseSource().faqDao().getAllFaq().observe(this, Observer {
            it?.let {
                listFaqDB.clear()
                listFaqDB.addAll(it)
                System.out.println("ListFaqDBSize>>>"+listFaqDB.size)
                val faqAdapter = FAQDetailAdapter(this@FaqActivity, listFaqDB)
                binding.rcvFaq.adapter = faqAdapter
            }
        })

    }
}