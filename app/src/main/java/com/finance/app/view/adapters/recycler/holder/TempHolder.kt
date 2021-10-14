package com.finance.app.view.adapters.recycler.holder

import android.content.Context
import com.finance.app.databinding.ItemCommonTempBinding
import motobeans.architecture.application.ArchitectureApp


/**
 * Created by motobeans on 1/3/2018.
 */
class TempHolder(val context: Context, val binding: ItemCommonTempBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

  init {
    ArchitectureApp.instance.component.inject(this)
  }

  fun handleCard(position: Int, item: String) {
  }
}