package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.activity.KYCActivity
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank


class ApplicantKycListAdapter(private val mContext : Context , private val applicantDetail : ArrayList<PersonalApplicantsModel> , val leadId : String) : RecyclerView.Adapter<ApplicantKycListAdapter.ViewHolder>() {
    private var mOnCardClickListener: CardClickListener? = null
    private val presenter = Presenter()
    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ApplicantKycListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_applicant_kyc_list, parent, false)
        return ViewHolder(v,mContext,leadId)
    }
    interface CardClickListener {
        fun onCardFetchKycClicked(position: Int,leadId: String,leadApplicantNumber : String,callType : Int)

    }
    fun setOnCardClickListener(listener: CardClickListener) {
        mOnCardClickListener = listener
    }
    override fun onBindViewHolder(holder: ApplicantKycListAdapter.ViewHolder , position: Int) {
               holder.bindItems(applicantDetail,position)
    }




    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return applicantDetail.size
    }
    //the class is hodling the list view
    inner class ViewHolder(itemView: View,val c: Context,val leadId : String) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(applicantDetail: ArrayList<PersonalApplicantsModel> , position: Int) {
            val tvfirstName = itemView.findViewById(R.id.tvfirstName) as TextView
            val tvILastName = itemView.findViewById(R.id.tvILastName) as TextView
            val tvLeadId = itemView.findViewById(R.id.tvLeadId) as TextView
            val btnPerformKyc = itemView.findViewById(R.id.btnPerformKyc) as Button
            val btnFetchKyc = itemView.findViewById(R.id.btnFetchKyc) as Button
            val btnUploadKycDocument = itemView.findViewById(R.id.btnUploadKycDocument) as Button
            tvfirstName.text = applicantDetail[position].firstName
            tvILastName.text = applicantDetail[position].lastName
            tvLeadId.text = applicantDetail[position].leadApplicantNumber
            System.out.println("inComeConsider>>>>" + applicantDetail[position].incomeConsidered)
            if(applicantDetail[position].incomeConsidered == true)
            {
                btnUploadKycDocument.visibility = View.VISIBLE
            }
            else{
                btnUploadKycDocument.visibility = View.GONE
            }

            btnPerformKyc.setOnClickListener {

                System.out.println("lead Id DataBase>>>>" + leadId)
                //presenter.callNetwork(ConstantsApi.CALL_KYC_ATTEMPT,dmiConnector = CallKYCAttempt(applicantDetail[position].leadApplicantNumber!!,applicantDetail[position].incomeConsidered!!))
                if (applicantDetail[position].incomeConsidered == true) {
                    KYCActivity.start(c , applicantDetail[position].leadApplicantNumber , 0 , 1,"","","")


                } else {
                    KYCActivity.start(c , applicantDetail[position].leadApplicantNumber , 1 , 1,"","","")


                }

            }
            btnFetchKyc.setOnClickListener {
                mOnCardClickListener!!.onCardFetchKycClicked(position , leadId , applicantDetail[position].leadApplicantNumber!! , 0)
            }
            btnUploadKycDocument.setOnClickListener {
                mOnCardClickListener!!.onCardFetchKycClicked(position , leadId , applicantDetail[position].leadApplicantNumber!! , 1)
            }
        }
    }
    inner class CallKYCAttempt(val applicantLeadNumber: String,val isIncomeConsider : Boolean) : ViewGeneric<Requests.RequestKycAttempt, Response.ResponseKYCAttempt>(context = mContext) {
        override val apiRequest: Requests.RequestKycAttempt
            get() = getKycDetail()

        override fun getApiSuccess(value: Response.ResponseKYCAttempt) {
            if (value.responseCode == Constants.SUCCESS) {
                // binding.dottedProgressBar!!.visibility = View.GONE

                val isKycAttempt = value.responseObj.isKycAttempt
                val isKycByPassAllowed ="true"
                        //value.responseObj.isKycByPassAllowed
                val kycStatus ="pending"
                        //value.responseObj.kycStatus
                System.out.println("IsKycAttempt>>>>"+isKycAttempt+"applicantLeadNumber>>>"+applicantLeadNumber)
                if(isIncomeConsider) {
                    KYCActivity.start(context , applicantLeadNumber,0,0,isKycAttempt!!,kycStatus!!,isKycByPassAllowed!!)
                    System.out.println("isIncomeConsider>>>>$isIncomeConsider")
                }
                else
                {
                    KYCActivity.start(context , applicantLeadNumber,1,0,isKycAttempt!!,kycStatus!!,isKycByPassAllowed!!)
                }

            } else {
                showToast(value.responseMsg)
                //binding.dottedProgressBar!!.visibility = View.GONE
            }
        }

        override fun getApiFailure(msg: String) {
            System.out.println("Api Failure>>>>"+msg)
            if (msg.exIsNotEmptyOrNullOrBlank()) {
                super.getApiFailure(msg)
                //binding.dottedProgressBar!!.visibility = View.GONE
            } else {
                super.getApiFailure("Time out Error")
                //binding.dottedProgressBar!!.visibility = View.GONE
            }

        }

        private fun getKycDetail(): Requests.RequestKycAttempt {
            val leadId: Int? = LeadMetaData.getLeadId()
            val leadApplicantNumber: String = applicantLeadNumber!!

            return Requests.RequestKycAttempt(leadID = leadId!! , leadApplicantNumber = leadApplicantNumber) //return Requests.RequestKycDetail(leadID = 2,leadApplicantNumber= "2001")

        }
    }


}
