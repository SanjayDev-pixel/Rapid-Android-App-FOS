package com.finance.app.view.activity

import android.content.Context
import android.content.Intent
import com.finance.app.R
import com.finance.app.databinding.ActivityAddTaskBinding
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate

class AddTaskActivity : BaseAppCompatActivity() {
    // used to bind element of layout to activity
    private val binding: ActivityAddTaskBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_add_task)

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AddTaskActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun init() {
//        hideToolbar()
//        hideSecondaryToolbar()
        binding.btnCreate.setOnClickListener {
        }
    }
}
