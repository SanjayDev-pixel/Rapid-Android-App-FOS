package com.finance.app.view.activity

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.app.R
import com.finance.app.databinding.ActivityNotificationBinding
import com.finance.app.view.adapters.recycler.adapter.NotificationAdapter
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate

class NotificationActivity : BaseAppCompatActivity(){

    private val binding: ActivityNotificationBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_notification)

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, NotificationActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun init() {
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.rcNotification.layoutManager = LinearLayoutManager(this)
        binding.rcNotification.adapter = NotificationAdapter(this)
    }
}

