package com.finance.app.view.customViews

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.databinding.ChannelpartnernewBinding
import com.finance.app.databinding.LayoutChannelPartnerBinding
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.ChannelPartnerName
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.persistence.model.LoanInfoModel
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.customViews.interfaces.IspinnerMainView
import kotlinx.android.synthetic.main.channelpartnernew.view.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.AppUtilExtensions
import javax.inject.Inject

class ChannelPartnerViewCreateLead @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : LinearLayout(context, attrs) {

    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    private lateinit var preferences: SharedPreferences

    private lateinit var binding: ChannelpartnernewBinding

    private val presenter = Presenter()

    private var mBranchId: String? = null
    private var mChannelTypeId: String? = null
    private var empId: String? = null
    private lateinit var activity: FragmentActivity
    private var sourcingPartner: CustomSpinnerView<DropdownMaster>? = null
    private var partnerName: CustomSpinnerView<ChannelPartnerName>? = null

    fun attachActivity(activity: FragmentActivity,loanData: LoanInfoModel?) {
        this.activity = activity
        binding = AppUtilExtensions.initCustomViewBinding(context = context,
                layoutId = R.layout.channelpartnernew, container = this)



        proceedFurther()
    }

    private fun proceedFurther() {
        val loanData:LoanInfoModel=LoanInfoModel()

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
                        getPartnerNameFromApi(sPartner)
                    }
                })
        binding.layoutSourcingPartner.addView(sourcingPartner)
        setSourcingPartner(loanData)

    }

    private fun getPartnerNameFromApi(channelId: String) {
        preferences=context.getSharedPreferences("dmi_brancnId",0)
        val sharedBranchId = preferences.getString("branchID","")
        mChannelTypeId = channelId
        mBranchId = sharedBranchId
        empId = sharedPreferences.getEmpId()


        if ((mChannelTypeId?.toInt() ?: 0) == Constants.DIRECT) {
            presenter.callNetwork(ConstantsApi.CALL_SOURCE_CHANNEL_PARTNER_NAME, CallSourcingPartnerName(LoanInfoModel()))
            binding.layoutPartnerName.visibility = View.GONE
        } else {
            presenter.callNetwork(ConstantsApi.CALL_SOURCE_CHANNEL_PARTNER_NAME, CallSourcingPartnerName(LoanInfoModel()))

        }
    }

    private fun setSourcingPartner(loanData: LoanInfoModel?) {

        if(loanData?.sourcingChannelPartnerTypeDetailID == null) {
            val sourcingCPTDid = LeadMetaData.getLeadData()?.sourcingChannelPartnerTypeDetailID
            sourcingPartner!!.setSelection(sourcingCPTDid.toString())
        }
        loanData?.let {
            sourcingPartner!!.setSelection(loanData.sourcingChannelPartnerTypeDetailID.toString())
        }
    }

    fun getSourcingPartner(): DropdownMaster? {
        return sourcingPartner?.getSelectedValue()
    }

    fun getPartnerName(): ChannelPartnerName? {
        return partnerName?.getSelectedValue()
    }

    inner class CallSourcingPartnerName(val loanData: LoanInfoModel?) : ViewGeneric<ArrayList<String?>,
            Response.ResponseSourceChannelPartnerName>(context = context) {

        override val apiRequest: ArrayList<String?>
            get() = arrayListOf(mBranchId, mChannelTypeId, empId)

        override fun getApiSuccess(value: Response.ResponseSourceChannelPartnerName) {
            if (value.responseCode == Constants.SUCCESS) {
                binding.layoutPartnerName.removeAllViews()
                if(value.responseObj.size > 0) {
                    setChannelPartnerNameDropDown(value.responseObj)
                    val editor: SharedPreferences.Editor = preferences.edit();
                    editor.remove("branchID")
                    editor.apply()
                }
            }
        }

        private fun setChannelPartnerNameDropDown(channelPartners: ArrayList<ChannelPartnerName>?) {
            partnerName = CustomSpinnerView(mContext = context, dropDowns = channelPartners, label = "Channel Partner Name")
            if(!channelPartners.isNullOrEmpty() && channelPartners.size >0) {
                binding.layoutPartnerName.addView(partnerName)
                if( binding.layoutPartnerName.isAttachedToWindow== true) {

                    if (loanData?.channelPartnerDsaID == null) {
                        val chanelPartnerId = LeadMetaData.getLeadData()?.dsaID
                        partnerName?.setSelection(chanelPartnerId)
                    }
                }


                loanData?.let {
                    partnerName?.setSelection(loanData.channelPartnerDsaID.toString())
                }
            }
        }
    }
}