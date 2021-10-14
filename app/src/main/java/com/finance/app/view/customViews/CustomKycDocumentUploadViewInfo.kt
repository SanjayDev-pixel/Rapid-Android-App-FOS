package com.finance.app.view.customViews

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.databinding.LayoutCustomViewKycDocumentUploadBinding
import com.finance.app.databinding.LayoutCustomViewPersonalBinding
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.persistence.model.KycListModel
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.utility.LeadMetaData
import com.finance.app.utility.SetPersonalMandatoryField
import com.finance.app.view.activity.PerformKycDocumentUploadActivity
import com.finance.app.view.activity.SelfDeclarationUploadDocumentActivity
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.AppUtilExtensions
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import javax.inject.Inject

class CustomKycDocumentUploadViewInfo @JvmOverloads constructor(context: Context , attrs: AttributeSet? = null) : LinearLayout(context , attrs) {
    private lateinit var mContext: Context
    private lateinit var binding: LayoutCustomViewKycDocumentUploadBinding
    private lateinit var activity: FragmentActivity
    private var index: Int = 0
    private val presenter = Presenter()
    var selectedApplicantNumber: String ? = null
    private var allMasterDropdown: AllMasterDropDown? = null
    @Inject
    lateinit var dataBase: DataBaseUtil
    fun attachView(activity: FragmentActivity , index: Int , applicant: PersonalApplicantsModel , leadId: Int?) {
        mContext = context!!
        this.activity = activity
        this.index = index
        binding = AppUtilExtensions.initCustomViewBinding(context = context , layoutId = R.layout.layout_custom_view_kyc_document_upload , container = this)
        initializeViews(applicant , leadId)

    }
    private fun initializeViews(applicant: PersonalApplicantsModel , leadId: Int?) {
        System.out.println("Initilise Biew Called")
        //SetPersonalMandatoryField(binding)
        getLeadApplicantId(applicant)
        if(applicant.incomeConsidered == true) {
            callApiKycList(leadId)
        }
        else{
            binding.txtKycScore.text ="Income is not consider for this applicant.So you can perform self declaration for KYC"
            binding.manualGuide.visibility = View.GONE
        }

        binding.btnUploadNow.setOnClickListener {

            allMasterDropdown?.let {
                val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.KYC_DOCUMENT , true) }
                val bundle = Bundle()
                bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)
                bundle.putString(Constants.KEY_TITLE , context.getString(R.string.face_auth_image))
                bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                PerformKycDocumentUploadActivity.startActivity(context , bundle)


            }
        }



    }
    private fun callApiKycList(leadId: Int?) {

        presenter.callNetwork(ConstantsApi.CALL_KYC_DETAIL , CallKYCDetail())
        //binding.dottedProgressBar!!.visibility = View.VISIBLE
    }
    inner class CallKYCDetail : ViewGeneric<Requests.RequestKycDetail , Response.ResponseKycDetail>(context = context) {
        override val apiRequest: Requests.RequestKycDetail
            get() = getKycDetail()

        override fun getApiSuccess(value: Response.ResponseKycDetail) {
            if (value.responseCode == Constants.SUCCESS) {
               // binding.dottedProgressBar!!.visibility = View.GONE

                if (value.responseObj.kycApplicantDetailsList.size > 0) {
                    val kycDetailResponse: KycListModel = value.responseObj

                    // open Fragment Dilaog here
                    //showKYCDetailDialog(kycDetailResponse)
                    setMatchPercentage(kycDetailResponse)
                } else {
                    showToast("KYC is not available now.")  //value.responseMsg
                    //binding.dottedProgressBar!!.visibility = View.GONE
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

        private fun getKycDetail(): Requests.RequestKycDetail {
            val leadId: Int? = LeadMetaData.getLeadId()
            val leadApplicantNumber: String = selectedApplicantNumber!!

            return Requests.RequestKycDetail(leadID = leadId!! , leadApplicantNumber = leadApplicantNumber) //return Requests.RequestKycDetail(leadID = 2,leadApplicantNumber= "2001")

        }
    }
    private fun getLeadApplicantId(applicant: PersonalApplicantsModel) {
        selectedApplicantNumber = applicant.leadApplicantNumber!!
        ArchitectureApp.instance.component.inject(this)
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(activity ,
                Observer { allMasterDropdown ->
                    allMasterDropdown?.let {
                        this@CustomKycDocumentUploadViewInfo.allMasterDropdown = allMasterDropdown
                        //setMasterDropDownValue(allMasterDropdown , applicant)
                    }
                })
    }
    private fun setMatchPercentage(kycDetailResponse: KycListModel){
        var matchPercentage: String? = ""
        var name : String ? = ""
        for (i in 0 until kycDetailResponse.kycApplicantDetailsList.size) {

            if(kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList.size>0) {
                for (j in 0 until kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList.size) {
                    if(j ==0) {
                       /* pincode = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].pinCode
                        name = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].name
                        genderValue = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].gender
                        dob = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].dob
                        address = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].address
                        careOf = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].careOf*/
                        matchPercentage = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].faceAuthScore
                        name = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].name
                        binding.txtKycScore.text = "Your KYC's FaceAuthScore is :$matchPercentage"
                        binding.txtName.visibility = View.VISIBLE
                        binding.txtName.text = "Name :$name"

                       /* faceAuthStatus = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].faceAuthStatus
                        bindingDialog.tvName.text = name
                        bindingDialog.tvcareof.text = careOf
                        bindingDialog.tvGender.text = if (genderValue.equals("M")) "Male" else if (genderValue.equals("F")) "Female" else "TransGender"
                        bindingDialog.tvAddress.text = address
                        bindingDialog.tvdob.text = ConvertDate().convertToAppFormatNew(dob)
                        dob = bindingDialog.tvdob.text.toString()
                        bindingDialog.matchpercentage.text = matchPercentage
                        bindingDialog.faceAuthStatus.text = faceAuthStatus*/
                    }
                }
            }
            else  if(kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList.size>0) {
                for (j in 0 until kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList.size) {
                    if(j ==0) {
                       /* pincode = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].pincode
                        name = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].name
                        genderValue = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].gender
                        dob = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].dob
                        address = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].address
                        careOf = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].careOf*/
                        name = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].name
                        matchPercentage = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].faceAuthScore
                        binding.txtKycScore.text = "Your KYC's FaceAuthScore is :$matchPercentage"
                        binding.txtName.visibility = View.VISIBLE
                        binding.txtName.text = "Name :$name"
                        /*faceAuthStatus = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].faceAuthStatus

                        System.out.println("Name>>>>>>" + name)
                        bindingDialog.tvName.text = name
                        bindingDialog.tvcareof.text = careOf
                        bindingDialog.tvGender.text = if (genderValue.equals("male")) "Male" else if (genderValue.equals("female")) "Female" else "TransGender"
                        bindingDialog.tvAddress.text = address
                        bindingDialog.tvdob.text = ConvertDate().convertToAppFormatNew(dob)
                        dob = bindingDialog.tvdob.text.toString()
                        bindingDialog.matchpercentage.text = matchPercentage
                        bindingDialog.faceAuthStatus.text = faceAuthStatus*/
                    }
                }
            }
            else if(kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList.size>0) {
                for (j in 0 until kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList.size) {
                    if(j ==0) {
                       /* pincode = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].pincode
                        name = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].name
                        genderValue = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].gender
                        dob = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].dob
                        address = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].address
                        careOf = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].careOf*/
                        matchPercentage = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].faceAuthScore
                        name = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].name
                        binding.txtKycScore.text = "Your KYC's FaceAuthScore is :$matchPercentage"
                        binding.txtName.visibility = View.VISIBLE
                        binding.txtName.text = "Name :$name"
                       /* faceAuthStatus = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].faceAuthStatus
                        System.out.println("Name>>>>>>" + name)
                        bindingDialog.tvName.text = name
                        bindingDialog.tvcareof.text = careOf
                        bindingDialog.tvGender.text = if (genderValue.equals("male")) "Male" else if (genderValue.equals("female")) "Female" else "TransGender"
                        bindingDialog.tvAddress.text = address
                        bindingDialog.tvdob.text = ConvertDate().convertToAppFormatNew(dob)
                        dob = bindingDialog.tvdob.text.toString()
                        bindingDialog.matchpercentage.text = matchPercentage
                        bindingDialog.faceAuthStatus.text = faceAuthStatus*/
                    }


                }

            }

        }
    }

}
