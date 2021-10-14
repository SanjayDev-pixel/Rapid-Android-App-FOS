package com.finance.app.view.adapters.recycler.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemDashboardChartDataBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import motobeans.architecture.retrofit.response.Response

class DashboardChartItemsAdapter(private val mActivity: FragmentActivity , private val chartDataItems: ArrayList<Response.ChartData>) :
        RecyclerView.Adapter<DashboardChartItemsAdapter.DashboardChartItemsHolder>() {

    private lateinit var binding: ItemDashboardChartDataBinding

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): DashboardChartItemsHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater , R.layout.item_dashboard_chart_data , parent , false)
        return DashboardChartItemsHolder(binding)
    }

    override fun getItemCount(): Int = chartDataItems.size

    override fun onBindViewHolder(holder: DashboardChartItemsHolder , position: Int) {
        holder.bindItems(chartDataItems[position])
    }

    inner class DashboardChartItemsHolder(val binding: ItemDashboardChartDataBinding) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var itemChartData: Response.ChartData
        fun bindItems(itemChartData: Response.ChartData) {
            this.itemChartData = itemChartData
            binding.tvChartTitle.text = itemChartData.title
//            binding.tvChartDescription.text = itemChartData.total.toString()

            initChartData()
        }


        private fun initChartData() {
            binding.chartData.setBackgroundColor(Color.WHITE)
            //moveOffScreen()
            binding.chartData.setUsePercentValues(false)
            binding.chartData.description.isEnabled = false
            binding.chartData.setCenterTextTypeface(Typeface.SANS_SERIF)
            //binding.chartData.setCenterText(generateCenterSpannableText())
//            binding.chartData.isDrawHoleEnabled = true
//            binding.chartData.setHoleColor(Color.WHITE)
//            binding.chartData.setTransparentCircleColor(Color.WHITE)
//            binding.chartData.setTransparentCircleAlpha(110)
            binding.chartData.holeRadius = 50f
            binding.chartData.transparentCircleRadius = 60f
            binding.chartData.setDrawCenterText(false)
            binding.chartData.isRotationEnabled = false
            binding.chartData.isHighlightPerTapEnabled = true
//            binding.chartData.maxAngle = 180f // HALF CHART
            binding.chartData.rotationAngle = 180f
//            binding.chartData.setCenterTextOffset(0f, -20f)
            binding.chartData.setDrawEntryLabels(false)
            setData()
            binding.chartData.animateY(1400 , Easing.EaseInOutQuad)
            binding.chartData.legend.textSize = 15f
            val l: Legend = binding.chartData.legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            l.orientation = Legend.LegendOrientation.HORIZONTAL
            l.setDrawInside(false)
            l.xEntrySpace = 7f
            l.yEntrySpace = 0f
            l.yOffset = 0f
            binding.chartData.setEntryLabelColor(Color.WHITE)
            //binding.chartData.setEntryLabelTypeface(tfRegular)
            binding.chartData.setEntryLabelTextSize(13f)
        }

        private fun setData() {
            val values = ArrayList<PieEntry>()

            itemChartData.data.forEach { chartData ->
                values.add(PieEntry(chartData.value , chartData.label))
            }
            val dataSet = PieDataSet(values , "")
            dataSet.sliceSpace = 3f
            dataSet.selectionShift = 5f
            dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
            //dataSet.setSelectionShift(0f);
            val data = PieData(dataSet)
//            data.setValueFormatter(LargeValueFormatter())
            data.setValueTextSize(13f)
            data.setValueTextColor(Color.BLACK)
            data.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}"
                }
            })
            //data.setValueTypeface(tfLight)
            binding.chartData.data = data
            binding.chartData.invalidate()
        }

        private fun generateCenterSpannableText(): SpannableString? {
            val s = SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda")
            s.setSpan(RelativeSizeSpan(1.7f) , 0 , 14 , 0)
            s.setSpan(StyleSpan(Typeface.NORMAL) , 14 , s.length - 15 , 0)
            s.setSpan(ForegroundColorSpan(Color.GRAY) , 14 , s.length - 15 , 0)
            s.setSpan(RelativeSizeSpan(.8f) , 14 , s.length - 15 , 0)
            s.setSpan(StyleSpan(Typeface.ITALIC) , s.length - 14 , s.length , 0)
            s.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()) , s.length - 14 , s.length , 0)
            return s
        }

        /*private fun moveOffScreen() {
            val displayMetrics = DisplayMetrics()
            mActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels
            val offset = (height * 0.65).toInt() *//* percent to move *//*
            val rlParams = binding.chartData.getLayoutParams() as RelativeLayout.LayoutParams
            rlParams.setMargins(0, 0, 0, -offset)
            binding.chartData.layoutParams = (rlParams)
        }*/

    }
}
