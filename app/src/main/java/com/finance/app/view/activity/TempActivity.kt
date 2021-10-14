package com.finance.app.view.activity

import android.content.Context
import android.content.Intent
import com.finance.app.R
import com.finance.app.databinding.ActivityTempBinding
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate

class TempActivity : BaseAppCompatActivity() {
    // used to bind element of layout to activity
    private val binding: ActivityTempBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_temp)

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TempActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun init() {
        binding.btnTemp1.setOnClickListener { }
//        binding.cvPersonalInfo.attachView()
        //binding.cvPersonalInfo.isMandatory(true)
    }
}
