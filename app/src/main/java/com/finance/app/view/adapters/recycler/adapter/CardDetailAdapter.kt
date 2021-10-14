package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemCreditCardBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.CardDetail
import com.finance.app.utility.LeadMetaData
import motobeans.architecture.util.DateUtil

class CardDetailAdapter(private val c: Context , private val cards: ArrayList<CardDetail> , allMasterDropDown: AllMasterDropDown?) : RecyclerView.Adapter<CardDetailAdapter.CreditCardViewHolder>() {
    private lateinit var binding: ItemCreditCardBinding
    private var mOnCardClickListener: CardClickListener? = null
    private var allMasterDropDown1: AllMasterDropDown? = allMasterDropDown

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): CreditCardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater , R.layout.item_credit_card , parent , false)
        return CreditCardViewHolder(binding , c)
    }

    override fun getItemCount() = cards.size

    fun setOnCardClickListener(listener: CardClickListener) {
        mOnCardClickListener = listener
    }

    interface CardClickListener {
        fun onCardDetailDeleteClicked(position: Int)
        fun onCardDetailEditClicked(position: Int , card: CardDetail)
    }

    fun getItemList(): ArrayList<CardDetail> {
        return cards
    }

    fun addItem(position: Int = 0 , cardDetail: CardDetail) {
        cards?.let {
            it.add(position , cardDetail)
            notifyDataSetChanged()
        }
    }

    fun updateItem(position: Int , cardDetail: CardDetail) {
        cards?.let {
            if (position >= 0 && position <= it.size) {
                it[position] = cardDetail

            }
            notifyDataSetChanged()
        }
    }

    fun deleteItem(position: Int) {
        cards?.let {
            cards.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: CreditCardViewHolder , position: Int) {
        holder.bindItems(position , cards[position])
    }

    inner class CreditCardViewHolder(val binding: ItemCreditCardBinding , val c: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(position: Int , card: CardDetail) {
            val dataUtil = DateUtil()
            card.cardLimit?.let { binding.tvCardLimit.text = it.toString() }
            card.currentUtilization?.let { binding.tvCurrentUtilization.text = it.toString() }
            binding.tvLastPaymentDate.text = dataUtil.getFormattedDate(DateUtil.dateFormattingType.TYPE_API_REQUEST_2 , DateUtil.dateFormattingType.TYPE_NORMAL_1 , card.lastPaymentDate.toString())

            for (i in 0 until allMasterDropDown1?.BankName!!.size) {

                if (card.bankNameTypeDetailID == allMasterDropDown1!!.BankName?.get(i)?.typeDetailID) {
                    binding.tvBankName.setText(allMasterDropDown1!!.BankName?.get(i)?.typeDetailCode)

                }
            }
            for (i in 0 until allMasterDropDown1?.Obligate!!.size) {

                if (card.obligateTypeDetail == allMasterDropDown1!!.Obligate?.get(i)?.typeDetailID) {
                    binding.tvObligate.setText(allMasterDropDown1!!.CreditCardObligation?.get(i)?.typeDetailCode)

                }
            }

            addClickListener(position , card)
            LeadMetaData.getLeadData()?.let {
                if (it.status.equals(AppEnums.LEAD_TYPE.SUBMITTED.type , true)) {
                    binding.btnDelete.visibility = View.GONE
                    binding.btnEdit.visibility = View.GONE
                } else {
                    binding.btnDelete.visibility = View.VISIBLE
                    binding.btnEdit.visibility = View.VISIBLE
                }
            }

        }

        private fun addClickListener(position: Int , card: CardDetail) {
            binding.btnDelete.setOnClickListener {
                mOnCardClickListener!!.onCardDetailDeleteClicked(position)
            }

            binding.btnEdit.setOnClickListener {
                mOnCardClickListener!!.onCardDetailEditClicked(position , card)
            }
        }
    }
}
