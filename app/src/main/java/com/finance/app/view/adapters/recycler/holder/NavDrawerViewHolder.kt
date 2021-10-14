package com.finance.app.view.adapters.recycler.holder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemNavBinding
import com.finance.app.others.AppEnums
import com.finance.app.view.adapters.recycler.adapter.NavMenuConnector

class NavDrawerViewHolder(val binding: ItemNavBinding, val mContext: Context, private val navMenuConnector: NavMenuConnector) : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(position: Int, navItem: AppEnums.ScreenLoanApp) {
        val navIcon = navItem.icon
        val title = if (navMenuConnector.isMenuExpanded()) navItem.screenName else ""

        binding.tvNavItem.text = title
        binding.iconNavItem.setImageResource(navIcon)

       /* binding.root.setOnClickListener {
            navMenuConnector.rootViewClicked(position, navItem)
        }*/
        changeColorBasedOnSelection(navItem = navItem)
    }

    private fun changeColorBasedOnSelection(navItem: AppEnums.ScreenLoanApp) {

        val selectedScreenName = navMenuConnector.getSelectionScreenName()

        if (navItem.screenName.equals(selectedScreenName, ignoreCase = true)) {
            binding.iconNavItem.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary),
                    android.graphics.PorterDuff.Mode.MULTIPLY)
            binding.tvNavItem.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            binding.parent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))

        } else {
            binding.iconNavItem.setColorFilter(ContextCompat.getColor(mContext, R.color.white),
                    android.graphics.PorterDuff.Mode.MULTIPLY)
            binding.tvNavItem.setTextColor(ContextCompat.getColor(mContext, R.color.white))
            binding.parent.setBackgroundResource(R.drawable.drawer_gradient_color)
        }
    }
}
