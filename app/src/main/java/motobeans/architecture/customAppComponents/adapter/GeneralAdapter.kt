package motobeans.architecture.customAppComponents.adapter

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList

open abstract class GeneralAdapter<T : ViewDataBinding, L>(
    private val mContext: Context,
    private val mList: ArrayList<L>?,
    private val mLayout: Int) : androidx.recyclerview.widget.RecyclerView.Adapter<GeneralAdapter<T, L>.MyHolder>() {
    private val mFullList = ArrayList<L>()

    private var isMultiSelAllowed = false
    private var isMultiSelEnable = false
    private val multiSelList = ArrayList<L>()

    init {
        if (mList != null) {
            mFullList.addAll(mList)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<T>(inflater, mLayout, parent, false)
        return MyHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: GeneralAdapter<T, L>.MyHolder, position: Int) {
        setUpMultiSelection(holder.binding as T, position, mList)
        customFun(holder.binding as T, position, mList)
    }

    abstract fun customFun(binding: T, position: Int, mList: List<L>?)
    fun rootClick(binding: T, position: Int, mList: List<L>?) {}

    fun isListEmpty(isEmpty: Boolean) {}
    fun onMultiSelection(binding: T?, position: Int, item: L?, mList: List<L>) {}

    override fun getItemCount(): Int {
        if (mList != null) {
            isListEmpty(if (mList.size > 0) false else true)
            return mList.size
        } else {
            isListEmpty(true)
            return 0
        }
    }

    inner class MyHolder(itemView: View, var binding: T) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)

    fun filter(done: (L) -> Boolean/*FilterFunction<L, Boolean>*/) {
        if (mFullList.size == 0) {
            if (mList != null) {
                mFullList.addAll(mList)
            }
        }
        mList!!.clear()
        for (value in mFullList) {
            if (done(value)) {
                mList.add(value)
            }
        }
        notifyDataSetChanged()
    }

    fun disableFilter() {
        mList!!.clear()
        mList.addAll(mFullList)
        notifyDataSetChanged()
    }

    private fun setUpMultiSelection(binding: T, position: Int, value: List<L>?) {
        binding.root.setOnClickListener { v ->
            if (isMultiSelEnable) {
                if (binding.root.tag as Boolean) {
                    multiSelList.add(value!![position])
                } else {
                    multiSelList.remove(value!![position])
                }
                onMultiSelection(binding, position, value[position], multiSelList)
                if (multiSelList.size == 0) {
                    isMultiSelEnable = false
                }
            } else {
                rootClick(binding, position, value)
            }
        }

        binding.root.setOnLongClickListener { v ->
            if (isMultiSelAllowed) {
                if (!isMultiSelEnable) {
                    multiSelList.clear()
                    isMultiSelEnable = true
                }
                if (binding.root.tag as Boolean) {
                    multiSelList.add(value!![position])
                } else {
                    multiSelList.remove(value!![position])
                }
                onMultiSelection(binding, position, value[position], multiSelList)
                if (multiSelList.size == 0) {
                    isMultiSelEnable = false
                }
            }
            false
        }
    }

    fun enableMultiSel(enable: Boolean) {
        isMultiSelAllowed = enable
        if (!isMultiSelAllowed) {
            multiSelList.clear()
            onMultiSelection(null, 0, null, multiSelList)
        }
    }

}

interface FilterFunction<T, U> {
    fun done(t: T): U
}
