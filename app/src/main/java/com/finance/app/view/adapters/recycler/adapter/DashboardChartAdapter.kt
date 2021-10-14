package com.finance.app.view.adapters.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemDashboardRowDataBinding
import motobeans.architecture.retrofit.response.Response

class DashboardChartAdapter(private val mActivity: FragmentActivity, private val dashboardChartData: Response.ResponseDashboard) : RecyclerView.Adapter<DashboardChartAdapter.DashboardChartHolder>() {

    private lateinit var binding: ItemDashboardRowDataBinding
    private var items = ArrayList<Response.DashboardChildrens>()

    init {
        items.addAll(dashboardChartData?.responseObj?.dashboardChildrens?: ArrayList())
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardChartHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_dashboard_row_data, parent, false)
        return DashboardChartHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DashboardChartHolder, position: Int) {
        holder.bindItems(items[position])
    }

    inner class DashboardChartHolder(val binding: ItemDashboardRowDataBinding) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var itemChartData: Response.DashboardChildrens
        fun bindItems(itemChartData: Response.DashboardChildrens) {
            this.itemChartData = itemChartData
            binding.tvChartTitle.text = itemChartData.heading
            binding.tvChartDescription.text = itemChartData.description

            initChartItemsAdapter()
        }

        private fun initChartItemsAdapter() {
            val layoutManager = LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false)
            binding.rvChartItems.layoutManager = layoutManager

            val adapterChartItems = DashboardChartItemsAdapter(mActivity = mActivity, chartDataItems = itemChartData.chartData)
            binding.rvChartItems.adapter = adapterChartItems
        }
    }
}
