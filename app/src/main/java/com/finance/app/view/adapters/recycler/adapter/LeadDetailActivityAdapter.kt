package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.finance.app.R
import com.finance.app.databinding.LeadDetailActivitiesItemBinding
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.FollowUpResponse
import com.finance.app.view.utils.getDisplayText
import java.util.*

class LeadDetailActivityAdapter(private val mContext: Context, private val followUp: ArrayList<FollowUpResponse>, allMasterDropDown: AllMasterDropDown?) : RecyclerView.Adapter<LeadDetailActivityAdapter.LeadDetailActivityHolder>() {
    private lateinit var binding: LeadDetailActivitiesItemBinding
    private val name = arrayOf("ChamanLal", "Sanat bhagel", " bhagel", "Sanat ")
    private val timeStamp = arrayOf(6541313L, 54684981L, 3216546L, 6461313L)
    private var allMasterDropDown: AllMasterDropDown? = allMasterDropDown
    override fun onBindViewHolder(holder: LeadDetailActivityHolder, position: Int) {
        holder.bindItems(position, followUp[position])
    }
    override fun getItemCount() = followUp.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeadDetailActivityHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.lead_detail_activities_item,
                parent, false)
        return LeadDetailActivityHolder(binding, mContext)
    }
    inner class LeadDetailActivityHolder(val binding: LeadDetailActivitiesItemBinding, val c: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(position: Int, followup: FollowUpResponse) {
            followup.customerFollowUpStatusTypeDetailId?.let { status ->
                binding.tvActivityText.text = allMasterDropDown?.CustomerFollowUpStatus?.getDisplayText(status)
                followup.leadTypeDetailId?.let { leadTypeDetailId ->
                    binding.linaerLeadType.visibility = View.VISIBLE
                    binding.linearTimingMeetingDate.visibility = View.VISIBLE
                    binding.linearMessage.visibility = View.VISIBLE
                    binding.linearLeadCloseReason.visibility = View.GONE
                    binding.tvLeadTypeText.text = allMasterDropDown?.LeadType?.getDisplayText(leadTypeDetailId)
                    followup.meetingDate?.let { meetingDate ->
                        binding.tvFollowUpTimingText.text = followup?.meetingDate
                    }
                    followup.messageShared?.let { message ->
                        binding.tvMessageText.text = followup.messageShared
                    }
                } ?: kotlin.run { binding.linaerLeadType.visibility = View.GONE
                                   binding.linearTimingMeetingDate.visibility = View.GONE
                                   binding.linearMessage.visibility = View.GONE

                     followup.leadRejectionReasonTypeDetailId?.let { leadRejectionTypeId->
                         binding.linearLeadCloseReason.visibility = View.VISIBLE
                        binding.tvLeadCloseText.text = allMasterDropDown?.LeadRejectionReason?.getDisplayText(leadRejectionTypeId)
                    } ?: kotlin.run {
                        binding.linaerLeadType.visibility = View.VISIBLE
                        binding.linearTimingMeetingDate.visibility = View.VISIBLE
                        binding.linearMessage.visibility = View.VISIBLE
                        binding.linearLeadCloseReason.visibility = View.GONE
                        binding.tvLeadTypelabel.text = "Notification type :"
                         followup.notificationTypeDetailId?.let {notificationTypeDetailId->
                             binding.tvLeadTypeText.text = allMasterDropDown?.LeadNotificationType?.getDisplayText(notificationTypeDetailId)
                         }
                         binding.tvFollowUpTimingLabel.text="Meeting Date :"
                         followup.meetingDate?.let { meetingDate ->
                             binding.tvFollowUpTimingText.text = followup?.meetingDate
                         }
                         followup.messageShared?.let { message ->
                             binding.tvMessageText.text = followup.messageShared
                         }


                    }
                }



            }

            //convertToHumanFormat(timeStamp)

            //allMasterDropDown?.CustomerFollowUpStatus?.let { list -> followup.customerFollowUpStatusTypeDetailId.let { id -> binding.tvActivity.text =setSelectionFromList(list, id) } }


        }

        private fun convertToHumanFormat(timeStamp: Array<Long>) {

            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timeStamp[position] * 1000L
            val date = DateFormat.format("dd MMM", cal).toString()
            //binding.tvActivityDate.text = date


        }
    }

}
