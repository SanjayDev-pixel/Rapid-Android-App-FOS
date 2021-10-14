package com.finance.app.view.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.databinding.LayoutChannelPartnerBinding
import com.finance.app.persistence.model.*
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.customViews.interfaces.IspinnerMainView
import kotlinx.android.synthetic.main.fragment_loan_information.view.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.Constants.APP.DIRECT
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.AppUtilExtensions
import javax.inject.Inject

class CustomChannelPartnerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : LinearLayout(context, attrs) {

    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil

    private lateinit var binding: LayoutChannelPartnerBinding

    private val presenter = Presenter()

    private var mBranchId: String? = null
    private var mChannelTypeId: String? = null
    private var empId: String? = null
    private lateinit var activity: FragmentActivity
    private var sourcingPartner: CustomSpinnerView<DropdownMaster>? = null
    private var partnerName: CustomSpinnerView<ChannelPartnerName>? = null

    fun attachActivity(activity: FragmentActivity, loanData: LoanInfoModel?) {
        this.activity = activity
        binding = AppUtilExtensions.initCustomViewBinding(context = context,
                layoutId = R.layout.layout_channel_partner, container = this)
        proceedFurther(loanData)
    }

    private fun proceedFurther(loanData: LoanInfoModel?) {
        ArchitectureApp.instance.component.inject(this)
        getDropDownsFromDB(loanData)
    }

    private fun getDropDownsFromDB(loanData: LoanInfoModel?) {
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue()
                .observe(activity, Observer { allDroDownValue ->

                    allDroDownValue?.let {
                        initializeSourcingPartner(allDroDownValue, loanData)
            }
        })
    }

    private fun initializeSourcingPartner(masterDropDown: AllMasterDropDown, loanData: LoanInfoModel?) {
        partnerName = CustomSpinnerView(mContext = context, isMandatory = true,
                dropDowns = ArrayList(), label = "Channel Partner Name *")
        binding.layoutPartnerName.addView(partnerName)

        sourcingPartner = CustomSpinnerView(mContext = context,
                dropDowns = masterDropDown.SourcingChannelPartner!!, label = "Sourcing Channel Partner *",
                iSpinnerMainView = object : IspinnerMainView<DropdownMaster> {

                    override fun getSelectedValue(value: DropdownMaster) {
                        val sPartner = value.getCompareValue()
                        getPartnerNameFromApi(sPartner, loanData)
            }
        })
        binding.layoutSourcingPartner.addView(sourcingPartner)
        setSourcingPartner(loanData)

    }

    private fun getPartnerNameFromApi(channelId: String, loanData: LoanInfoModel?) {
        mChannelTypeId = channelId
        mBranchId = LeadMetaData.getLeadData()?.branchID
        empId = sharedPreferences.getEmpId()
        var leadStatus= LeadMetaData.getLeadData()?.status

        if ((mChannelTypeId?.toInt() ?: 0) == DIRECT) {
            presenter.callNetwork(ConstantsApi.CALL_SOURCE_CHANNEL_PARTNER_NAME, CallSourcingPartnerName(loanData))
            binding.layoutPartnerName.visibility = View.GONE
        } else {
            presenter.callNetwork(ConstantsApi.CALL_SOURCE_CHANNEL_PARTNER_NAME, CallSourcingPartnerName(loanData))
            binding.layoutPartnerName.visibility = View.VISIBLE
        }
    }

    private fun setSourcingPartner(loanData: LoanInfoModel?) {
        var leadStatus= LeadMetaData.getLeadData()?.status
        if(loanData?.sourcingChannelPartnerTypeDetailID == null) {
            val sourcingCPTDid = LeadMetaData.getLeadData()?.sourcingChannelPartnerTypeDetailID
            sourcingPartner!!.setSelection(sourcingCPTDid.toString())
        }

        loanData?.let {
            sourcingPartner!!.setSelection(loanData.sourcingChannelPartnerTypeDetailID.toString())
            if (leadStatus=="Submitted"){
                sourcingPartner?.disableSelf()
            }
        }
    }

    fun getSourcingPartner(): DropdownMaster? {
        return sourcingPartner?.getSelectedValue()
    }

    fun getPartnerName(): ChannelPartnerName? {
        return partnerName?.getSelectedValue()
    }

    fun disableSelf() {
       sourcingPartner?.disableSelf()
        partnerName?.disableSelf()
    }

    fun enableSelf() {
        sourcingPartner?.enableSelf()
        partnerName?.enableSelf()

    }

    inner class CallSourcingPartnerName(val loanData: LoanInfoModel?) : ViewGeneric<ArrayList<String?>,
            Response.ResponseSourceChannelPartnerName>(context = context) {

        override val apiRequest: ArrayList<String?>
            get() = arrayListOf(mBranchId, mChannelTypeId, empId)

        override fun getApiSuccess(value: Response.ResponseSourceChannelPartnerName) {
            if (value.responseCode == Constants.SUCCESS) {
                binding.layoutPartnerName.removeAllViews()
                setChannelPartnerNameDropDown(value.responseObj)
            }
        }

        private fun setChannelPartnerNameDropDown(channelPartners: ArrayList<ChannelPartnerName>?) {
            partnerName = CustomSpinnerView(mContext = context , dropDowns = channelPartners , label = "Channel Partner Name")
            if (!channelPartners.isNullOrEmpty() && channelPartners.size > 0) {
                binding.layoutPartnerName.addView(partnerName)
                var leadStatus = LeadMetaData.getLeadData()?.status

                if (loanData?.channelPartnerDsaID == null) {
                    val channelPartnerIDid = LeadMetaData.getLeadData()?.dsaID

                    LeadMetaData.getLeadData()?.dsaID?.let {
                        partnerName?.setSelection(LeadMetaData.getLeadData()?.dsaID)
                    }
                }



                loanData?.channelPartnerDsaID?.let {
                    partnerName?.setSelection(loanData?.channelPartnerDsaID.toString())

                    if (leadStatus == "Submitted") {
                        partnerName?.disableSelf()
                    }

                }
            }
        }
    }
}