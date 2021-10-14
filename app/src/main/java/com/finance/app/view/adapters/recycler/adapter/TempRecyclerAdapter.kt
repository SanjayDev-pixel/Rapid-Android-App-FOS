package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import com.finance.app.R
import com.finance.app.view.adapters.recycler.holder.EmptyCommonListHolder
import com.finance.app.view.adapters.recycler.holder.TempHolder
import com.finance.app.databinding.EmptyCommonListBinding
import com.finance.app.databinding.ItemCommonTempBinding
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.development.interfaces.DataBaseUtil
import javax.inject.Inject

/**
 * Created by motobeans on 1/3/2018.
 */
class TempRecyclerAdapter(val activity: androidx.fragment.app.FragmentActivity, val items: List<String>?, val isCountToShow: Boolean = false) :
    androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    @Inject
    lateinit var dataBase: DataBaseUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    val context: Context = activity

    private val DEFAULT_VIEW = -1
    private val EMPTY_VIEW = 0
    private val MAIN_ITEM_VISUAL = 1

    private fun getLayoutInflater(parent: ViewGroup?): LayoutInflater {
        return LayoutInflater.from(parent?.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            EMPTY_VIEW -> (holder as EmptyCommonListHolder).handleCard(position = position,
                emptyText = "No Item Found.")
            MAIN_ITEM_VISUAL -> (holder as TempHolder).handleCard(position = position,
                item = items!!.get(position))
            DEFAULT_VIEW -> (holder as TempHolder).handleCard(position = position,
                item = items!!.get(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        var viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder? = null
        when (viewType) {
            EMPTY_VIEW -> {
                val binding: EmptyCommonListBinding = DataBindingUtil.inflate(
                    getLayoutInflater(parent), R.layout.empty_common_list, parent, false)
                viewHolder = EmptyCommonListHolder(
                    context = activity, binding = binding)
            }
            MAIN_ITEM_VISUAL -> {
                val binding: ItemCommonTempBinding = DataBindingUtil.inflate(
                    getLayoutInflater(parent),
                    R.layout.item_common_temp, parent, false)
                viewHolder = TempHolder(
                    context = activity, binding = binding)
            }
            else -> {
                val binding: ItemCommonTempBinding = DataBindingUtil.inflate(
                    getLayoutInflater(parent),
                    R.layout.item_common_temp, parent, false)
                viewHolder = TempHolder(
                    context = activity, binding = binding)
            }
        }
        return viewHolder
    }

    private var isEmpty = false
    override fun getItemCount(): Int {

        var size = items?.size ?: 0
        if (size == 0) {
            size = 1
            isEmpty = true
        } else {
            isEmpty = false
        }
        return size
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 && isEmpty) {
            return EMPTY_VIEW
        } else {
            return MAIN_ITEM_VISUAL
        }
    }
}
