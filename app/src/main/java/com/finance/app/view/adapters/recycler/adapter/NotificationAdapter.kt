package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemNotificationBinding

class NotificationAdapter(private val c: Context) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private lateinit var binding: ItemNotificationBinding

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bindItems(position)
    }

    override fun getItemCount() = 6

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_notification, parent, false)
        return NotificationViewHolder(binding, c)
    }

    inner class NotificationViewHolder(val binding: ItemNotificationBinding, val c: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(position: Int) {
        }
    }
}
