package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemDocumentToCollectBinding

class TaskDetailAdapter(private val c: Context) : RecyclerView.Adapter<TaskDetailAdapter.TaskDetailViewHolder>() {

    private lateinit var binding: ItemDocumentToCollectBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskDetailViewHolder {
        val layoutInflater = LayoutInflater.from(c)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_document_to_collect, parent, false)
        return TaskDetailViewHolder(binding, c)
    }

    override fun getItemCount() = 3

    override fun onBindViewHolder(holder: TaskDetailViewHolder, position: Int) {
        holder.bindItems( position)
    }

    inner class TaskDetailViewHolder(val binding: ItemDocumentToCollectBinding, val c: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems( position: Int) {
            binding.iconTask.setImageResource(R.drawable.sucess)
        }
    }
}
