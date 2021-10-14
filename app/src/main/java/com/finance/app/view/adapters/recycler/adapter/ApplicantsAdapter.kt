package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemApplicantBinding
import com.finance.app.persistence.model.CoApplicantsList
import com.finance.app.persistence.model.PersonalApplicantsModel
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import javax.inject.Inject

class ApplicantsAdapter(private val mContext: Context, private val applicants: ArrayList<PersonalApplicantsModel>)
    : RecyclerView.Adapter<ApplicantsAdapter.ApplicantsViewHolder>() {

    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    private lateinit var binding: ItemApplicantBinding
    private var mClickListener: ItemClickListener? = null
    private var mLongClickListener: ItemLongClickListener? = null
    private var selectedPosition = 0

    fun setOnItemClickListener(listener: ItemClickListener) {
        mClickListener = listener
    }

    fun setOnLongClickListener(longListener: ItemLongClickListener) {
        mLongClickListener = longListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicantsViewHolder {
        val layoutInflater = LayoutInflater.from(mContext)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_applicant, parent, false)
        return ApplicantsViewHolder(binding, mContext)
    }

    override fun getItemCount() = applicants.size

    override fun onBindViewHolder(holder: ApplicantsViewHolder, position: Int) {
        holder.bindItems(position, applicants[position])
    }

    interface ItemClickListener {
        fun onApplicantClick(position: Int, coApplicant: PersonalApplicantsModel)
    }

    interface ItemLongClickListener {
        fun onApplicantLongClick(position: Int)
    }

    fun onClickItem(position: Int, coApplicant: PersonalApplicantsModel) {
        selectedPosition = position
        mClickListener?.onApplicantClick(position, coApplicant)
    }


    fun onLongClickItem(position: Int) {
        selectedPosition = position
        mLongClickListener?.onApplicantLongClick(position)
        selectedPosition = position - 1
    }

    inner class ApplicantsViewHolder(val binding: ItemApplicantBinding, val mContext: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(position: Int, coApplicant: PersonalApplicantsModel) {
            if (position==0){

                binding.tvApplicants.text = "Applicant"
            }else{
                binding.tvApplicants.text="Co-Applicant".plus(position)
            }
           // binding.tvApplicants.text = coApplicant.firstName
            binding.tvApplicants.setOnClickListener { onClickItem(position, coApplicant)
            }
            itemView.setOnLongClickListener {
                onLongClickItem(position)
                false
            }

            if (selectedPosition == adapterPosition) {
                binding.tvApplicants.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                binding.tvApplicants.setBackgroundResource(R.drawable.selected_applicant_tab)
            } else {
                binding.tvApplicants.setTextColor(ContextCompat.getColor(mContext, R.color.black))
                binding.tvApplicants.setBackgroundResource(R.drawable.unselected_applicant_tab)
            }
        }
    }
}
