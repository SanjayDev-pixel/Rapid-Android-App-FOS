package com.finance.app.view.activity

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.app.R
import com.finance.app.databinding.ActivityTaskDetailBinding
import com.finance.app.view.adapters.recycler.adapter.TaskDetailAdapter
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate

class TaskDetailActivity : BaseAppCompatActivity() {

    private val binding: ActivityTaskDetailBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_task_detail)

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TaskDetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun init() {
        hideToolbar()
        hideSecondaryToolbar()
        binding.rcTasks.layoutManager = LinearLayoutManager(this)
        binding.rcTasks.adapter = TaskDetailAdapter(this)
    }
}
