package com.finance.app.view.activity

import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.finance.app.R
import com.finance.app.databinding.ActivityPreviewBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.persistence.model.ApplicantionSubmitModel
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.adapters.recycler.adapter.PreviewPagerAdapter
import com.finance.app.view.fragment.loanApplicationFragments.FragmentPreview
import kotlinx.android.synthetic.main.activity_preview.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import javax.inject.Inject

class PreviewActivity : BaseAppCompatActivity() {

    private val binding: ActivityPreviewBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_preview
    )

    @Inject
    lateinit var dataBase: DataBaseUtil
    private var pagerAdapter: PreviewPagerAdapter? = null
    private val presenter = Presenter()
      companion object {
        var master: HashMap<AppEnums.DropdownMasterType, ArrayList<DropdownMaster>?>? = null
        fun start(context: Context) {
            val intent = Intent(context, PreviewActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun init() {
        System.out.println("time test1>>>")
        ArchitectureApp.instance.component.inject(this)
        hideToolbar()
        hideSecondaryToolbar()
        getLead()
        setOnClickListeners()
        System.out.println("time test2>>>")

    }

    private fun setOnClickListeners() {


        binding.backbttn.setOnClickListener() {
            this.finish()
        }

        binding.layoutSubmitlead.btnCancel.setOnClickListener(){
            this.finish()
        }
        binding.layoutSubmitlead.btnSubmit.setOnClickListener(){

            validationBeforeSubmit()
            val lead: AllLeadMaster? = LeadMetaData.getLeadData()
               // checkAndStartLoanApplicationActivity(lead)
        }
    }


    private fun checkAndStartLoanApplicationActivity(lead: AllLeadMaster?) {

        val isLeadOfflineDataSync = lead!!.isDetailAlreadySync

            when (isLeadOfflineDataSync) {
                true -> presenter.callNetwork(ConstantsApi.CALL_FINAL_SUBMIT, CallFinalSubmit())
                false -> Toast.makeText(this, "Please wait lead is syncronise with server", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getLead() {
        val leadData = LeadMetaData.getLeadData()
        leadData?.let {
            fillLeadData(leadData)
            setUpLeadFragments()
        }
    }

    private fun fillLeadData(leadMaster: AllLeadMaster) {
        val leadName =
                leadMaster.applicantFirstName + " " + leadMaster.applicantMiddleName + " " + leadMaster.applicantLastName
        binding.tvLeadNumber.text = leadMaster.leadNumber
        binding.tvApplicantName.text = leadName
        binding.tvAmount.text = (leadMaster.loanData?.loanAmountRequest ?: "").toString()
    }

    private fun setUpLeadFragments() {
        pagerAdapter = PreviewPagerAdapter(supportFragmentManager)
        pagerAdapter!!.addFragment(
                FragmentPreview.newInstance(AppEnums.ScreenLoanApp.LOAN_INFORMATION),
                AppEnums.ScreenLoanApp.LOAN_INFORMATION.screenName
        )
        pagerAdapter!!.addFragment(
                FragmentPreview.newInstance(AppEnums.ScreenLoanApp.PERSONAL),
                AppEnums.ScreenLoanApp.PERSONAL.screenName
        )
        pagerAdapter!!.addFragment(
                FragmentPreview.newInstance(AppEnums.ScreenLoanApp.EMPLOYMENT),
                AppEnums.ScreenLoanApp.EMPLOYMENT.screenName
        )
        pagerAdapter!!.addFragment(
                FragmentPreview.newInstance(AppEnums.ScreenLoanApp.BANK_DETAIL),
                AppEnums.ScreenLoanApp.BANK_DETAIL.screenName
        )
        pagerAdapter!!.addFragment(
                FragmentPreview.newInstance(AppEnums.ScreenLoanApp.LIABILITY_AND_ASSET),
                AppEnums.ScreenLoanApp.LIABILITY_AND_ASSET.screenName
        )
        pagerAdapter!!.addFragment(
                FragmentPreview.newInstance(AppEnums.ScreenLoanApp.PROPERTY),
                AppEnums.ScreenLoanApp.PROPERTY.screenName
        )
        pagerAdapter!!.addFragment(
                FragmentPreview.newInstance(AppEnums.ScreenLoanApp.REFERENCE),
                AppEnums.ScreenLoanApp.REFERENCE.screenName
        )
        pagerAdapter!!.addFragment(
                FragmentPreview.newInstance(AppEnums.ScreenLoanApp.DOCUMENT_CHECKLIST),
                AppEnums.ScreenLoanApp.DOCUMENT_CHECKLIST.screenName
        )
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 8
        binding.pageIndicator.attachTo(binding.viewPager)

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.btnNext.setOnClickListener {
            binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1, true)
            checkCurrentPage()
        }

        binding.btnPrevious.setOnClickListener {
            binding.viewPager.setCurrentItem(binding.viewPager.currentItem - 1, true)
        }

        binding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {
                binding.tvFormPage.text = pagerAdapter?.getPageTitle(position)
                checkCurrentPage()
                binding.viewPager.adapter?.let { adapter ->
                    shouldShowSubmitButton(adapter.count - 1 == position)
                }
            }

            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }


    private fun checkCurrentPage() {
        when (binding.viewPager.currentItem) {
            in 1..6 -> bothButtonVisible()
            0 -> binding.btnPrevious.visibility = View.GONE
            else -> binding.btnNext.visibility = View.GONE
        }
    }

    private fun shouldShowSubmitButton(visible: Boolean) {
        binding.layoutSubmitlead.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun bothButtonVisible() {
        binding.btnPrevious.visibility = View.VISIBLE
        binding.btnNext.visibility = View.VISIBLE
    }



    inner class CallFinalSubmit : ViewGeneric<Requests.RequestFinalSubmit, Response.ResponseFinalSubmit>(context = this) {
        override val apiRequest: Requests.RequestFinalSubmit?
            get() = getCallUpdateRequest()
        override fun getApiSuccess(value: Response.ResponseFinalSubmit) {
            if (value.responseCode == Constants.SUCCESS) {
                //Toast.makeText(context, "Submitted Successfully.", Toast.LENGTH_SHORT).show()
                binding.progressBar!!.visibility = View.GONE
                 val submitLoanResponse: ApplicantionSubmitModel? = value.responseObj
                if (value.responseObj != null) {
                    //Update the status of lead
                    val leadId = LeadMetaData.getLeadId()
                    LeadMetaData().updateLeadStatusIntoDB("Submitted",leadId)
                    val intent = Intent(this@PreviewActivity, LoanSubmitStatusActivity::class.java)
                    intent.putExtra("SubmitResponse", submitLoanResponse)
                    startActivity(intent)
                    this@PreviewActivity.finish()

                } else {

                    showToast(value.responseMsg)
                }


            } else {
                showToast(value.responseMsg)
                binding.progressBar!!.visibility = View.GONE
            }

        }
        override fun getApiFailure(msg: String) {

            if (msg.exIsNotEmptyOrNullOrBlank()) {
                super.getApiFailure(msg)
                binding.progressBar!!.visibility = View.GONE
            } else {
                super.getApiFailure("Time out Error")
                binding.progressBar!!.visibility = View.GONE
                  }

        }
        private fun getCallUpdateRequest(): Requests.RequestFinalSubmit? {
            binding.progressBar!!.visibility =View.VISIBLE
            val leadId = LeadMetaData.getLeadId()
            return Requests.RequestFinalSubmit(leadID = leadId!!)

        }
    }
    // validation code before submit the lead
    private fun validationBeforeSubmit() {
        var errorCount = 0
       val lead = LeadMetaData.getLeadData()
        var isPropertySelected:Boolean?= false // if  property selected  check propertyData otherwise not
        var isIncomeConsidered:Boolean?=false
        var isMainApplicant: Boolean?=false
        var checkEmpBankDetail_other:Boolean?=false // if
        //var iskycdata : Int = 0
        var isIncomeConsideredMaindatory:Boolean? = false
        var maindateIncomeFlag: Boolean? =false


        if(lead?.loanData?.applicationNumber!=null )
        {
            isPropertySelected = lead?.loanData?.isPropertySelected

        }else{

            Toast.makeText(this@PreviewActivity,"Please check Loan Detail",Toast.LENGTH_SHORT).show()
            errorCount++
        }
        if(lead?.personalData?.applicantDetails!!.size>0 ){
            //condition  for applicant list size > 1 than isIncomeConsider is maindatory for at least one applicant
            if(lead?.personalData?.applicantDetails!!.size>1){
                for (i in 0 until lead?.personalData?.applicantDetails.size) {
                    isIncomeConsideredMaindatory = lead?.personalData?.applicantDetails[i].incomeConsidered
                    if(isIncomeConsideredMaindatory == true){
                        maindateIncomeFlag = true
                    }
                }
            }


            if(lead?.personalData?.applicantDetails!!.size == 1){
                for (i in 0 until lead?.personalData?.applicantDetails.size) {
                    isIncomeConsideredMaindatory = lead?.personalData?.applicantDetails[i].incomeConsidered
                    if(isIncomeConsideredMaindatory == false){
                        errorCount++
                        Toast.makeText(this@PreviewActivity,"Income Consider is mandatory ",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            //other conditions
            for (i in 0 until lead?.personalData?.applicantDetails.size) {
                isIncomeConsidered= lead?.personalData?.applicantDetails[i].incomeConsidered
                isMainApplicant= lead?.personalData?.applicantDetails[i].isMainApplicant
                //iskycdata =lead?.personalData?.applicantDetails[i].applicantKycList!!.size

                /*if(lead?.personalData?.applicantDetails[i].applicantKycList!!.size>0){}else{
                    errorCount++
                    Toast.makeText(this@PreviewActivity,"Please complete your KYC",Toast.LENGTH_SHORT).show()
                }*/
                if(isIncomeConsidered == true ){
                    checkEmpBankDetail_other= true
                    if(lead?.employmentData?.applicantDetails!!.size >0 &&  lead?.employmentData?.applicantDetails[i].profileSegmentTypeDetailID !=null){


                    } else {
                        Toast.makeText(this@PreviewActivity,"Please check Employment Detail",Toast.LENGTH_SHORT).show()
                        errorCount++
                    }
                    if(lead.bankData.bankDetailList.size >0 && lead.bankData.bankDetailList[i].applicantBankDetailsBean.size >0){}else{
                        Toast.makeText(this@PreviewActivity,"Please check Bank Detail",Toast.LENGTH_SHORT).show()
                        errorCount++
                    }

                }
                /*else if(isIncomeConsidered == false && iskycdata >0){
                    checkEmpBankDetail_other= false
                }else if(isIncomeConsidered == false && iskycdata ==0){
                    errorCount++
                    Toast.makeText(this@PreviewActivity,"Please complete your KYC",Toast.LENGTH_SHORT).show()
                }else if(isIncomeConsidered == false && iskycdata ==0){
                    errorCount++
                    Toast.makeText(this@PreviewActivity,"Please complete your KYC",Toast.LENGTH_SHORT).show()
                }*/
                else if(isIncomeConsidered==true && lead?.employmentData?.applicantDetails.size==0){
                    errorCount++
                    Toast.makeText(this@PreviewActivity,"Please check Employement Details",Toast.LENGTH_SHORT).show()
                }else if(isIncomeConsidered==true && lead.bankData.bankDetailList.size==0){
                    errorCount++
                    Toast.makeText(this@PreviewActivity,"Please check Bank Details",Toast.LENGTH_SHORT).show()
                }

            }

        }else{
            Toast.makeText(this@PreviewActivity,"Please check Personal Detail",Toast.LENGTH_SHORT).show()
            errorCount++
        }

        if(lead.referenceData.loanApplicationObj.size > 0){}else{
                Toast.makeText(this@PreviewActivity,"Please check Reference  Detail",Toast.LENGTH_SHORT).show()
                errorCount++
            }

        if(isPropertySelected==true){
            if(lead.propertyData.propertyTypeDetailID !=null){

            }else{
                if(isPropertySelected==true)
                Toast.makeText(this@PreviewActivity,"Please check Property Detail",Toast.LENGTH_SHORT).show()
                errorCount++
            }
        }

        if (lead?.personalData?.applicantDetails!!.size>1){
            if(maindateIncomeFlag==true){}else{
                errorCount++
                Toast.makeText(this@PreviewActivity,"Select income consider in Employment at least for one applicant.",Toast.LENGTH_SHORT).show()
            }

        }
        if(errorCount==0){
            val lead: AllLeadMaster? = LeadMetaData.getLeadData()
             checkAndStartLoanApplicationActivity(lead)
        }else{
            Toast.makeText(this@PreviewActivity,"Please fill mandatory fields.",Toast.LENGTH_SHORT).show()
        }
    }

}
