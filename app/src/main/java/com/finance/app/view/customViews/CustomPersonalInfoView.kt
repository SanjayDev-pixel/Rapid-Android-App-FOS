package com.finance.app.view.customViews

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.finance.app.R
import com.finance.app.databinding.DialogKycDetailBinding
import com.finance.app.databinding.LayoutCustomViewPersonalBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.*
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.utility.*
import com.finance.app.view.activity.DocumentUploadingActivity
import com.finance.app.view.activity.KYCActivity
import com.finance.app.view.activity.PerformKycDocumentUploadActivity
import com.finance.app.view.customViews.interfaces.IspinnerMainView
import kotlinx.android.synthetic.main.layout_zip_address.view.*
import kotlinx.android.synthetic.main.pop_up_verify_otp.*
import kotlinx.android.synthetic.main.pop_up_verify_otp.view.*
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.Constants.APP.ADDRESS_PROOF
import motobeans.architecture.constants.Constants.APP.CURRENT_ADDRESS
import motobeans.architecture.constants.Constants.APP.DOB
import motobeans.architecture.constants.Constants.APP.PERMANENT_ADDRESS
import motobeans.architecture.constants.Constants.APP.PERSONAL
import motobeans.architecture.constants.Constants.APP.RENTED
import motobeans.architecture.constants.Constants.APP.SELF
import motobeans.architecture.constants.Constants.APP.SINGLE
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.AppUtilExtensions
import motobeans.architecture.util.exGone
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import motobeans.architecture.util.exVisible
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class CustomPersonalInfoView @JvmOverloads constructor(context: Context , attrs: AttributeSet? = null) : LinearLayout(context , attrs) {

    @Inject
    lateinit var dataBase: DataBaseUtil

    @Inject
    lateinit var formValidation: FormValidation
    private lateinit var binding: LayoutCustomViewPersonalBinding
    private lateinit var activity: FragmentActivity
    private var index: Int = 0
    private var otp: Int? = 0
    private val presenter = Presenter()
    private lateinit var verifyOTPDialog: Dialog
    private lateinit var verifyOTPDialogView: View
    private lateinit var gender: CustomSpinnerView<DropdownMaster>
    private lateinit var nationality: CustomSpinnerView<DropdownMaster>
    private lateinit var dobProof: CustomSpinnerView<DropdownMaster>
    private lateinit var livingStandard: CustomSpinnerView<DropdownMaster>
    private lateinit var detailQualification: CustomSpinnerView<DropdownMaster>
    private lateinit var qualification: CustomSpinnerView<DropdownMaster>
    private lateinit var caste: CustomSpinnerView<DropdownMaster>
    private lateinit var religion: CustomSpinnerView<DropdownMaster>
    private lateinit var relationship: CustomSpinnerView<DropdownMaster>
    private lateinit var currentAddressProof: CustomSpinnerView<DropdownMaster>
    private lateinit var permanentAddressProof: CustomSpinnerView<DropdownMaster>
    private lateinit var maritalStatus: CustomSpinnerView<DropdownMaster>
    private lateinit var permanentResidenceType: CustomSpinnerView<DropdownMaster>
    private lateinit var currentResidenceType: CustomSpinnerView<DropdownMaster>
    private var spinnerDMList: ArrayList<CustomSpinnerView<DropdownMaster>> = ArrayList()
    private var detailKycDialog: Dialog? = null
    private var allMasterDropdown: AllMasterDropDown? = null
    private var kycOptionDialog: Dialog? = null
    private val kycPresenter = Presenter()
    private lateinit var mContext: Context
    private var isKycAttempt : String ? = null
    private var kycStatus : String ? = null
    private var isKycByPassAllowed : String ? = null


    //This id is generated at client side so make sure this id must be created before any operation...
    private lateinit var selectedApplicantNumber: String


    fun attachView(activity: FragmentActivity , index: Int , applicant: PersonalApplicantsModel , leadId: Int?) {
        mContext = context!!
        this.activity = activity
        this.index = index
        binding = AppUtilExtensions.initCustomViewBinding(context = context , layoutId = R.layout.layout_custom_view_personal , container = this)
        initializeViews(applicant , leadId)

    }

    private fun initializeViews(applicant: PersonalApplicantsModel , leadId: Int?) {
        SetPersonalMandatoryField(binding)
        setDatePicker()
        setClickListeners(leadId , applicant)
        setUpCustomViews()
        proceedFurther(applicant)
        System.out.println("Applicant numbr>>>>"+selectedApplicantNumber)

        if (applicant.isMainApplicant == true) {
            binding.basicInfoLayout.btnUploadProfileImage.setText("Applicant Pic")
            //binding.btnDeleteCoApplicant.visibility = View.GONE
        } else {
            binding.basicInfoLayout.btnUploadProfileImage.setText("CoApplicant Pic")
            //binding.btnDeleteCoApplicant.visibility = View.VISIBLE
        }

    }

    private fun setDatePicker() {
        binding.basicInfoLayout.etDOB.setOnClickListener {
            SelectDOB(context , binding.basicInfoLayout.etDOB , binding.basicInfoLayout.etAge)
        }
    }

    private fun setClickListeners(leadId: Int? , applicant: PersonalApplicantsModel) {
         binding.btnAddKYC.setOnClickListener {
             //Check KyC Attempt for Income earner
             if(applicant.incomeConsidered == true) {
                 presenter.callNetwork(ConstantsApi.CALL_KYC_ATTEMPT, dmiConnector = CallKYCAttempt(applicant))
             }else
             {
                 KYCActivity.start(context , applicant.leadApplicantNumber,1,0,"","","")
             }

         }
        binding.basicInfoLayout.btnGetOTP.setOnClickListener {
            if (binding.basicInfoLayout.etMobile.text.toString() != "" && binding.basicInfoLayout.etMobile.text?.length == 10) {
                val leadMaster = LeadMetaData.getLeadData()
                leadMaster?.let {
                    presenter.callNetwork(ConstantsApi.CALL_SEND_OTP , CallSendOTP(leadMaster , applicant))
                }
            } else {
                Toast.makeText(context , "Please enter mobile number" , Toast.LENGTH_SHORT).show()
            }
        }

        binding.basicInfoLayout.btnUploadProfileImage.setOnClickListener {
            allMasterDropdown?.let {
                val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(PERSONAL , true) }
                val bundle = Bundle()
                bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)//Hardcoded for Profile proof...
                bundle.putString(Constants.KEY_TITLE , context.getString(R.string.profile_img))
                bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                DocumentUploadingActivity.startActivity(context , bundle)
            }

        }

        binding.basicInfoLayout.btnUploadDob.setOnClickListener {
            allMasterDropdown?.let {
                val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(DOB , true) }
                val bundle = Bundle()
                bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)//Hardcoded for DOB proof...
                bundle.putString(Constants.KEY_TITLE , context.getString(R.string.dob))
                bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                DocumentUploadingActivity.startActivity(context , bundle)
            }
        }


        binding.personalAddressLayout.btnUploadAddress.setOnClickListener {
            allMasterDropdown?.let {
                val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(ADDRESS_PROOF , true) }
                val bundle = Bundle()
                bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)//Hardcoded for Address proof...
                bundle.putString(Constants.KEY_TITLE , context.getString(R.string.address))
                bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                DocumentUploadingActivity.startActivity(context , bundle)
            }
        }
        binding.personalAddressLayout.btnUploadPermanentAddress.setOnClickListener {
            allMasterDropdown?.let {
                val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(ADDRESS_PROOF , true) }
                val bundle = Bundle()
                bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)//Hardcoded for Address proof...
                bundle.putString(Constants.KEY_TITLE , context.getString(R.string.address))
                bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                DocumentUploadingActivity.startActivity(context , bundle)
            }
        }

        binding.personalAddressLayout.cbSameAsCurrent.setOnCheckedChangeListener { buttonView , isChecked ->
            if (isChecked) binding.personalAddressLayout.llPermanentAddress.visibility = View.GONE
            else binding.personalAddressLayout.llPermanentAddress.visibility = View.VISIBLE
        }

        binding.btnKyclist.setOnClickListener() {
            callApiKycList(leadId)
        }
        CurrencyConversion().convertToCurrencyType(binding.personalAddressLayout.etPermanentRentAmount)
        CurrencyConversion().convertToCurrencyType(binding.personalAddressLayout.etCurrentRentAmount)

        if(applicant.incomeConsidered == true)
        {
            binding.btnUploadKycDocument.visibility = View.VISIBLE
        }
        else
        {
            binding.btnUploadKycDocument.visibility = View.GONE
        }
        binding.btnUploadKycDocument.setOnClickListener {
            callApiKycStatus(leadId)
        }
    }


    private fun callApiKycList(leadId: Int?) {

        presenter.callNetwork(ConstantsApi.CALL_KYC_DETAIL , CallKYCDetail())
        binding.progressBar!!.visibility = View.VISIBLE
    }

    private fun setUpCustomViews() {
        binding.personalAddressLayout.customCurrentZipAddressView.attachActivity(activity = activity)
        binding.personalAddressLayout.customPermanentZipAddressView.attachActivity(activity = activity)
    }

    private fun proceedFurther(applicant: PersonalApplicantsModel) {
        ArchitectureApp.instance.component.inject(this)
        generateLeadApplicantId(applicant)
        getDropDownsFromDB(applicant)

    }
    inner class CallKYCAttempt(val applicant: PersonalApplicantsModel) : ViewGeneric<Requests.RequestKycAttempt , Response.ResponseKYCAttempt>(context = context) {
        override val apiRequest: Requests.RequestKycAttempt
            get() = getKycDetail()

        override fun getApiSuccess(value: Response.ResponseKYCAttempt) {
            if (value.responseCode == Constants.SUCCESS) {
                // binding.dottedProgressBar!!.visibility = View.GONE

                isKycAttempt = value.responseObj.isKycAttempt
                isKycByPassAllowed = value.responseObj.isKycByPassAllowed
                        //
                kycStatus = value.responseObj.kycStatus
                        //
                System.out.println("IsKycAttempt>>>>"+isKycAttempt)
                if(applicant.incomeConsidered == true) {
                    KYCActivity.start(context , applicant.leadApplicantNumber,0,0,isKycAttempt!!,kycStatus!!,isKycByPassAllowed!!)
                    System.out.println("isIncomeConsider>>>>"+applicant.incomeConsidered)
                }
                else
                {
                    KYCActivity.start(context , applicant.leadApplicantNumber,1,0,isKycAttempt!!,kycStatus!!,isKycByPassAllowed!!)
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
            val leadApplicantNumber: String = selectedApplicantNumber!!

            return Requests.RequestKycAttempt(leadID = leadId!! , leadApplicantNumber = leadApplicantNumber) //return Requests.RequestKycDetail(leadID = 2,leadApplicantNumber= "2001")

        }
    }

    private fun generateLeadApplicantId(applicant: PersonalApplicantsModel) {
        if (applicant.leadApplicantNumber.isNullOrEmpty()) //if applicant id is not generated...
            //applicant.leadApplicantNumber = LeadMetaData.getLeadData()?.leadNumber?.let { LeadAndLoanDetail().getLeadApplicantNumber(LeadMetaData.getLeadId().toString(), it ,this.index) }
            applicant.leadApplicantNumber = LeadAndLoanDetail().getLeadApplicantNum(LeadMetaData.getLeadId().toString() , index)
        //To use same lead applicant number for later...
        selectedApplicantNumber = applicant.leadApplicantNumber!! //will always have a value
    }

    private fun getDropDownsFromDB(applicant: PersonalApplicantsModel) {
        dataBase.provideDataBaseSource().allMasterDropDownDao().getMasterDropdownValue().observe(activity ,
                Observer { allMasterDropdown ->
                    allMasterDropdown?.let {
                        this@CustomPersonalInfoView.allMasterDropdown = allMasterDropdown
                        setMasterDropDownValue(allMasterDropdown , applicant)
                    }
                })
    }

    private fun getRelationship(relationship: ArrayList<DropdownMaster>?): ArrayList<DropdownMaster> {
        val relationshipList: ArrayList<DropdownMaster> = ArrayList()
        relationship?.let {

            for (dropdown in relationship) {
                if (dropdown.typeDetailID != SELF) {
                    relationshipList.add(dropdown)
                }
            }
        }
        return relationshipList
    }

    private fun setMasterDropDownValue(dropDown: AllMasterDropDown , applicant: PersonalApplicantsModel) {
        setCustomSpinner(dropDown , applicant)
        fillValueInMasterDropDown(applicant)
        applicant.applicantKycList?.let { binding.kycApplicant.bindApplicantKycDetails(activity , selectedApplicantNumber , it) }
    }

    private fun setUpRelationshipValue(allMasterDropDown: AllMasterDropDown , applicant: PersonalApplicantsModel) {
        if (index == 0) {
            relationship = CustomSpinnerView(mContext = context , isMandatory = true , dropDowns = allMasterDropDown.Relationship!! , label = "Relationship *")
            binding.basicInfoLayout.layoutRelationShip.addView(relationship)

            relationship.setSelection(SELF.toString())
            relationship.disableSelf()
        } else {
            relationship = CustomSpinnerView(mContext = context , isMandatory = true , dropDowns = getRelationship(allMasterDropDown.Relationship) , label = "Relationship With Applicant *")
            binding.basicInfoLayout.layoutRelationShip.addView(relationship)

            relationship.setSelection(applicant.relationshipTypeDetailId?.toString())
        }
    }

    private fun setCustomSpinner(allMasterDropDown: AllMasterDropDown , applicant: PersonalApplicantsModel) {
        dobProof = CustomSpinnerView(mContext = context , isMandatory = true , dropDowns = allMasterDropDown.DOBProof!! , label = "DOB Proof *")
        binding.basicInfoLayout.layoutDobProof.addView(dobProof)

        livingStandard = CustomSpinnerView(mContext = context , isMandatory = true , dropDowns = allMasterDropDown.LivingStandardIndicators!! , label = "Living Standard *")
        binding.basicInfoLayout.layoutLivingStandard.addView(livingStandard)
        detailQualification = CustomSpinnerView(mContext = context , isMandatory = true , dropDowns = allMasterDropDown.DetailQualification!! , label = "Detail Qualification *")
        binding.basicInfoLayout.layoutDetailQualification.addView(detailQualification)
        qualification = CustomSpinnerView(mContext = context , isMandatory = true , dropDowns = allMasterDropDown.Qualification!! , label = "Qualification *")
        binding.basicInfoLayout.layoutQualification.addView(qualification)
        caste = CustomSpinnerView(mContext = context , isMandatory = true , dropDowns = allMasterDropDown.Caste!! , label = "Caste *")
        binding.basicInfoLayout.layoutCaste.addView(caste)
        religion = CustomSpinnerView(mContext = context , dropDowns = allMasterDropDown.Religion!! , label = "Religion *")
        binding.basicInfoLayout.layoutReligion.addView(religion)
        nationality = CustomSpinnerView(mContext = context , isMandatory = true , dropDowns = allMasterDropDown.Nationality!! , label = "Nationality *")
        binding.basicInfoLayout.layoutNationality.addView(nationality)
        gender = CustomSpinnerView(mContext = context , isMandatory = true , dropDowns = allMasterDropDown.Gender!! , label = "Gender *")
        binding.basicInfoLayout.layoutGender.addView(gender)
        permanentAddressProof = CustomSpinnerView(mContext = context , isMandatory = true , dropDowns = allMasterDropDown.AddressProof!! , label = "Address Proof *")
        binding.personalAddressLayout.layoutPermanentAddressProof.addView(permanentAddressProof)
        currentAddressProof = CustomSpinnerView(mContext = context , isMandatory = true , dropDowns = allMasterDropDown.AddressProof!! , label = "Address Proof *")
        binding.personalAddressLayout.layoutCurrentAddressProof.addView(currentAddressProof)
        setUpRelationshipValue(allMasterDropDown , applicant)
        setCustomSpinnerWithCondition(allMasterDropDown)
        LeadMetaData.getLeadData()?.let {
            if (it.status.equals(AppEnums.LEAD_TYPE.SUBMITTED.type , true))
                DisablePersonalForm(binding , dobProof , livingStandard , detailQualification , qualification , caste , religion , gender , permanentAddressProof , currentAddressProof , nationality , maritalStatus , currentResidenceType , permanentResidenceType)
        }

    }

    private fun setCustomSpinnerWithCondition(allMasterDropDown: AllMasterDropDown) {
        maritalStatus = CustomSpinnerView(mContext = context , isMandatory = true ,
                dropDowns = allMasterDropDown.MaritalStatus!! , label = "Marital Status *" ,
                iSpinnerMainView = object : IspinnerMainView<DropdownMaster> {
                    override fun getSelectedValue(value: DropdownMaster) {
//                binding.basicInfoLayout.layoutMaritalStatus.removeAllViews()
                        if (value.typeDetailID == SINGLE) {
                            binding.basicInfoLayout.llSpouse.visibility = View.GONE
                        } else {
                            binding.basicInfoLayout.llSpouse.visibility = View.VISIBLE
                        }
                    }
                })

        binding.basicInfoLayout.layoutMaritalStatus.addView(maritalStatus)

        permanentResidenceType = CustomSpinnerView(mContext = context , isMandatory = true , dropDowns = allMasterDropDown.ResidenceType!! , label = "Residence Type *" , iSpinnerMainView = object : IspinnerMainView<DropdownMaster> {
            override fun getSelectedValue(value: DropdownMaster) {
                if (value.typeDetailID == RENTED) {
                    binding.personalAddressLayout.inputLayoutPermanentRentAmount.visibility = View.VISIBLE
                } else {
                    binding.personalAddressLayout.inputLayoutPermanentRentAmount.visibility = View.GONE
                }
            }
        })

        binding.personalAddressLayout.layoutPermanentResidenceType.addView(permanentResidenceType)
        currentResidenceType = CustomSpinnerView(mContext = context , isMandatory = true , dropDowns = allMasterDropDown.ResidenceType!! , label = "Residence Type *" , iSpinnerMainView = object : IspinnerMainView<DropdownMaster> {
            override fun getSelectedValue(value: DropdownMaster) {
                if (value.typeDetailID == RENTED) {
                    binding.personalAddressLayout.inputLayoutCurrentRentAmount.visibility = View.VISIBLE
                } else {
                    binding.personalAddressLayout.inputLayoutCurrentRentAmount.visibility = View.GONE
                }
            }
        })
        binding.personalAddressLayout.layoutCurrentResidenceType.addView(currentResidenceType)

        setDropDownList()
    }

    private fun setDropDownList() {
        spinnerDMList = arrayListOf(
                dobProof , livingStandard , maritalStatus , gender ,
                nationality , religion , caste , qualification , detailQualification , livingStandard ,
                relationship , currentResidenceType , currentAddressProof
        )
    }

    private fun fillValueInMasterDropDown(currentApplicant: PersonalApplicantsModel) {
        gender.setSelection(currentApplicant.genderTypeDetailID?.toString())
        nationality.setSelection(currentApplicant.nationalityTypeDetailID?.toString())
        religion.setSelection(currentApplicant.religionTypeDetailID?.toString())
        caste.setSelection(currentApplicant.casteTypeDetailID?.toString())
        dobProof.setSelection(currentApplicant.dobProofTypeDetailID?.toString())
        qualification.setSelection(currentApplicant.qualificationTypeDetailID?.toString())
        detailQualification.setSelection(currentApplicant.detailQualificationTypeDetailID?.toString())
        livingStandard.setSelection(currentApplicant.livingStandardTypeDetailId?.toString())
        relationship.setSelection(currentApplicant.relationshipTypeDetailId?.toString())
        maritalStatus.setSelection(currentApplicant.maritialStatusTypeDetailID?.toString())

        fillFormWithCurrentApplicant(currentApplicant)
        if (!currentApplicant.addressDetailList.isNullOrEmpty()) {
            fillAddressInfo(currentApplicant.addressDetailList!!)
        }
    }

    private fun fillFormWithCurrentApplicant(currentApplicant: PersonalApplicantsModel) {
        LeadMetaData.getLeadData()?.let { leadDetails ->
            //First Use the default name as pre-filled at lead creation screen
            if (index == 0) { //also check if this is a main applicant...
                binding.basicInfoLayout.etFirstName.setText(leadDetails.applicantFirstName)
                binding.basicInfoLayout.etMiddleName.setText(leadDetails.applicantMiddleName)
                binding.basicInfoLayout.etLastName.setText(leadDetails.applicantLastName)
                binding.basicInfoLayout.btnUploadProfileImage.setText("Applicant Pic")
            }
        }

        currentApplicant.contactDetail?.let {
            binding.basicInfoLayout.etEmail.setText(currentApplicant.contactDetail?.email)
            binding.basicInfoLayout.etMobile.setText(currentApplicant.contactDetail?.mobile)
            binding.basicInfoLayout.mobileverifiedStatus.setText(currentApplicant.contactDetail?.isMobileVerified.toString())
            if (currentApplicant.contactDetail!!.isMobileVerified!!) {
                binding.basicInfoLayout.etMobile.isEnabled = false
                binding.basicInfoLayout.btnGetOTP.visibility = View.GONE
                binding.basicInfoLayout.ivVerifiedStatus.visibility = View.VISIBLE
            }
        }

        binding.basicInfoLayout.etDOB.setText(ConvertDate().convertToAppFormat(currentApplicant.dateOfBirth))
        currentApplicant.incomeConsidered?.let { binding.basicInfoLayout.cbIncomeConsidered.isChecked = it }
        binding.basicInfoLayout.etFatherLastName.setText(currentApplicant.fatherLastName)
        binding.basicInfoLayout.etFatherMiddleName.setText(currentApplicant.fatherMiddleName)
        binding.basicInfoLayout.etFatherFirstName.setText(currentApplicant.fatherFirstName)
        //Checking these values for not overriding values, if set before in empty case...
        if (currentApplicant.firstName.exIsNotEmptyOrNullOrBlank()) binding.basicInfoLayout.etFirstName.setText(currentApplicant.firstName)
        if (currentApplicant.middleName.exIsNotEmptyOrNullOrBlank()) binding.basicInfoLayout.etMiddleName.setText(currentApplicant.middleName)
        if (currentApplicant.lastName.exIsNotEmptyOrNullOrBlank()) binding.basicInfoLayout.etLastName.setText(currentApplicant.lastName)
        currentApplicant.numberOfDependents?.let { binding.basicInfoLayout.etNumOfDependent.setText(it.toString()) }
        //binding.basicInfoLayout.etNumOfDependent.setText(currentApplicant.numberOfDependents.toString())

        currentApplicant.numberOfEarningMembers?.let { binding.basicInfoLayout.etNumOfEarningMember.setText(it.toString()) }
        currentApplicant.numberOfFamilyMembersOthers?.let { binding.basicInfoLayout.etNoOffamilymembers.setText(it.toString()) }
        //binding.basicInfoLayout.etLastName.setText(currentApplicant.lastName)
        currentApplicant.age?.let { binding.basicInfoLayout.etAge.setText(it.toString()) }
        currentApplicant.alternateContact?.let { binding.basicInfoLayout.etAlternateNum.setText(it) }
        if (currentApplicant.maritialStatusTypeDetailID != SINGLE) {
            binding.basicInfoLayout.etSpouseMiddleName.setText(currentApplicant.spouseMiddleName)
            binding.basicInfoLayout.etSpouseFirstName.setText(currentApplicant.spouseFirstName)
            binding.basicInfoLayout.etSpouseLastName.setText(currentApplicant.spouseLastName)
        }
    }

    private fun fillAddressInfo(addressDetailList: ArrayList<AddressDetail>) {
        fillPermanentAddressInfo(addressDetailList[1])
        fillCurrentAddressInfo(addressDetailList[0])
    }

    private fun fillCurrentAddressInfo(addressDetail: AddressDetail) {
        binding.personalAddressLayout.cbSameAsCurrent.isChecked = addressDetail.sameAsCurrentAddress

        binding.personalAddressLayout.etCurrentAddress.setText(addressDetail.address1)
        binding.personalAddressLayout.etCurrentLandmark.setText(addressDetail.landmark)
        binding.personalAddressLayout.etCurrentRentAmount.setText(addressDetail.rentAmount)
        binding.personalAddressLayout.etCurrentStaying.setText(addressDetail.stayingInYears?.toString())
        currentAddressProof.setSelection(addressDetail.addressProof?.toString())
        currentResidenceType.setSelection(addressDetail.residenceTypeTypeDetailID?.toString())
        updateCustomZipCode(customZipView = binding.personalAddressLayout.customCurrentZipAddressView , addressDetail = addressDetail)
    }

    private fun fillPermanentAddressInfo(addressDetail: AddressDetail) {
        binding.personalAddressLayout.etPermanentAddress.setText(addressDetail.address1)
        binding.personalAddressLayout.etPermanentLandmark.setText(addressDetail.landmark)
        binding.personalAddressLayout.etPermanentRentAmount.setText(addressDetail.rentAmount)
        binding.personalAddressLayout.etPermanentStaying.setText(addressDetail.stayingInYears?.toString())
        // binding.personalAddressLayout.cbSameAsCurrent.isChecked = addressDetail
        permanentAddressProof.setSelection(addressDetail.addressProof?.toString())
        permanentResidenceType.setSelection(addressDetail.residenceTypeTypeDetailID?.toString())
        updateCustomZipCode(customZipView = binding.personalAddressLayout.customPermanentZipAddressView , addressDetail = addressDetail)
    }

    private fun updateCustomZipCode(customZipView: CustomZipAddressView , addressDetail: AddressDetail) {
        customZipView.updateAddressData(addressDetail = addressDetail)
    }

    private fun getCurrentApplicant(): PersonalApplicantsModel {
        val currentApplicant = PersonalApplicantsModel()
        val casteDD = caste.getSelectedValue()
        val dQualificationDD = detailQualification.getSelectedValue()
        val qDD = qualification.getSelectedValue()
        val dobProofDD = dobProof.getSelectedValue()
        val genderDD = gender.getSelectedValue()
        val nationalityDD = nationality.getSelectedValue()
        val religionDD = religion.getSelectedValue()
        val mStatusDD = maritalStatus.getSelectedValue()
        val livingStandardDD = livingStandard.getSelectedValue()
        val relationshipDD = relationship.getSelectedValue()
        val dependents = binding.basicInfoLayout.etNumOfDependent.text.toString()
        val earningMembers = binding.basicInfoLayout.etNumOfEarningMember.text.toString()
        val pResidenceType = currentResidenceType.getSelectedValue()
        val numberOfFamilyMember = binding.basicInfoLayout.etNoOffamilymembers.text.toString()
        System.out.println("Residence Type>>>>" + pResidenceType)


        //Need to generate some applicant id... based on lead id
//        currentApplicant.leadApplicantNumber = LeadAndLoanDetail().getLeadApplicantNum(LeadMetaData.getLeadId().toString(), index)
        currentApplicant.leadApplicantNumber = selectedApplicantNumber //Lead Applicant number already created above....
        currentApplicant.applicantKycList = binding.kycApplicant.getKycDetailsList()
        currentApplicant.casteTypeDetailID = casteDD?.typeDetailID
        currentApplicant.detailQualificationTypeDetailID = dQualificationDD?.typeDetailID
        currentApplicant.qualificationTypeDetailID = qDD?.typeDetailID
        currentApplicant.dobProofTypeDetailID = dobProofDD?.typeDetailID
        currentApplicant.genderTypeDetailID = genderDD?.typeDetailID
        currentApplicant.nationalityTypeDetailID = nationalityDD?.typeDetailID
        currentApplicant.religionTypeDetailID = religionDD?.typeDetailID
        currentApplicant.relationshipTypeDetailId = relationshipDD?.typeDetailID
        currentApplicant.maritialStatusTypeDetailID = mStatusDD?.typeDetailID
        currentApplicant.isMainApplicant = index == 0
        currentApplicant.livingStandardTypeDetailId = livingStandardDD?.typeDetailID
        currentApplicant.numberOfEarningMembers = if (earningMembers == "") 0 else earningMembers.toInt()
        currentApplicant.numberOfDependents = if (dependents == "") 0 else dependents.toInt()
        currentApplicant.firstName = binding.basicInfoLayout.etFirstName.text.toString()
        currentApplicant.middleName = binding.basicInfoLayout.etMiddleName.text.toString()
        currentApplicant.lastName = binding.basicInfoLayout.etLastName.text.toString()
        currentApplicant.spouseFirstName = binding.basicInfoLayout.etSpouseFirstName.text.toString()
        currentApplicant.spouseMiddleName = binding.basicInfoLayout.etSpouseMiddleName.text.toString()
        currentApplicant.spouseLastName = binding.basicInfoLayout.etSpouseLastName.text.toString()
        currentApplicant.fatherFirstName = binding.basicInfoLayout.etFatherFirstName.text.toString()
        currentApplicant.fatherMiddleName = binding.basicInfoLayout.etFatherMiddleName.text.toString()
        currentApplicant.fatherLastName = binding.basicInfoLayout.etFatherLastName.text.toString()
        currentApplicant.dateOfBirth = ConvertDate().convertToApiFormat(binding.basicInfoLayout.etDOB.text.toString())
        currentApplicant.age = binding.basicInfoLayout.etAge.text.toString().toInt()
        currentApplicant.incomeConsidered = binding.basicInfoLayout.cbIncomeConsidered.isChecked
        currentApplicant.alternateContact = binding.basicInfoLayout.etAlternateNum.text.toString()
        currentApplicant.contactDetail = getContactDetail()
        currentApplicant.addressDetailList = getAddressDetailList(currentApplicant.addressDetailList)
        currentApplicant.presentAccommodationTypeDetailID = pResidenceType?.typeDetailID
        currentApplicant.numberOfFamilyMembersOthers = if (numberOfFamilyMember == "") 0 else numberOfFamilyMember.toInt()

        return currentApplicant
    }

    private fun getContactDetail(): ContactDetail? {
        val contactDetail = ContactDetail()
        contactDetail.email = binding.basicInfoLayout.etEmail.text.toString()
        contactDetail.mobile = binding.basicInfoLayout.etMobile.text.toString()
        contactDetail.isMobileVerified= if(binding.basicInfoLayout.mobileverifiedStatus.text.toString().equals("true")) true else  false
        return contactDetail
    }

    private fun getAddressDetailList(addressDetailList: ArrayList<AddressDetail>?): ArrayList<AddressDetail>? {
        val cAddressDetail = AddressDetail()
        val cResidenceType = currentResidenceType.getSelectedValue()
        val cAddressProof = currentAddressProof.getSelectedValue()

        cAddressDetail.rentAmount = CurrencyConversion().convertToNormalValue(binding.personalAddressLayout.etCurrentRentAmount.text.toString())
        cAddressDetail.stayingInYears = binding.personalAddressLayout.etCurrentStaying.text.toString().toFloat()
        cAddressDetail.address1 = binding.personalAddressLayout.etCurrentAddress.text.toString()
        cAddressDetail.landmark = binding.personalAddressLayout.etCurrentLandmark.text.toString()
        cAddressDetail.zip = binding.personalAddressLayout.customCurrentZipAddressView.pinCode
        cAddressDetail.stateID = binding.personalAddressLayout.customCurrentZipAddressView.getStateId()
        cAddressDetail.stateName = binding.personalAddressLayout.customCurrentZipAddressView.getStateName()
        cAddressDetail.districtID = binding.personalAddressLayout.customCurrentZipAddressView.getDistrictId()
        cAddressDetail.districtName = binding.personalAddressLayout.customCurrentZipAddressView.getDistrictName()
        cAddressDetail.cityID = binding.personalAddressLayout.customCurrentZipAddressView.getCityId()
        cAddressDetail.cityName = binding.personalAddressLayout.customCurrentZipAddressView.getCityName()
        cAddressDetail.residenceTypeTypeDetailID = cResidenceType?.typeDetailID

        cAddressDetail.addressProof = cAddressProof?.typeDetailID
        cAddressDetail.sameAsCurrentAddress = binding.personalAddressLayout.cbSameAsCurrent.isChecked
        var pAddressDetail = AddressDetail()

        if (binding.personalAddressLayout.cbSameAsCurrent.isChecked) {
            pAddressDetail = cAddressDetail.clone() as AddressDetail
            spinnerDMList.add(permanentResidenceType)
            spinnerDMList.add(permanentAddressProof)

        } else {
            val pResidenceType = permanentResidenceType.getSelectedValue()
            val pAddressProof = permanentAddressProof.getSelectedValue()

            pAddressDetail.rentAmount = CurrencyConversion().convertToNormalValue(binding.personalAddressLayout.etPermanentRentAmount.text.toString())
            pAddressDetail.stayingInYears = binding.personalAddressLayout.etPermanentStaying.text.toString().toFloat()
            pAddressDetail.address1 = binding.personalAddressLayout.etPermanentAddress.text.toString()
            pAddressDetail.landmark = binding.personalAddressLayout.etPermanentLandmark.text.toString()
            pAddressDetail.zip = binding.personalAddressLayout.customPermanentZipAddressView.pinCode
            pAddressDetail.residenceTypeTypeDetailID = pResidenceType?.typeDetailID

            pAddressDetail.addressProof = pAddressProof?.typeDetailID
            pAddressDetail.stateID = binding.personalAddressLayout.customPermanentZipAddressView.getStateId()
            pAddressDetail.districtID = binding.personalAddressLayout.customPermanentZipAddressView.getDistrictId()
            pAddressDetail.cityID = binding.personalAddressLayout.customPermanentZipAddressView.getCityId()
            pAddressDetail.cityName = binding.personalAddressLayout.customCurrentZipAddressView.getCityName()
            pAddressDetail.districtName = binding.personalAddressLayout.customCurrentZipAddressView.getDistrictName()
            pAddressDetail.stateName = binding.personalAddressLayout.customCurrentZipAddressView.getStateName()

        }

        //Set this value, separate from above logic
        cAddressDetail.addressTypeDetailID = 83
        cAddressDetail.addressTypeDetail = CURRENT_ADDRESS
        pAddressDetail.addressTypeDetailID = 82
        pAddressDetail.addressTypeDetail = PERMANENT_ADDRESS

        if (addressDetailList.isNullOrEmpty()) {
            addressDetailList?.add(0 , cAddressDetail)
            addressDetailList?.add(1 , pAddressDetail)
        } else {
            addressDetailList[0] = cAddressDetail
            addressDetailList[1] = pAddressDetail
        }
        return addressDetailList
    }

    private fun showVerifyOTPDialog(leadId: Int? , applicant: PersonalApplicantsModel) {
        verifyOTPDialogView = LayoutInflater.from(context).inflate(R.layout.pop_up_verify_otp , null)
        val mBuilder = AlertDialog.Builder(context)
                .setView(verifyOTPDialogView)
                .setCancelable(false)

        verifyOTPDialog = mBuilder.show()
        verifyOTPDialog.tvMobile_no.setText("+91 ".plus(binding.basicInfoLayout.etMobile.text.toString()))
        val pinEntry = verifyOTPDialogView.etOTP
        pinEntry!!.setOnPinEnteredListener { pin ->
            if (pin.toString().length == 4) {
                otp = pin.toString().toInt()
                presenter.callNetwork(ConstantsApi.CALL_VERIFY_OTP , CallVerifyOTP(leadId , applicant))
            } else {
                pinEntry.text = null
            }
        }

        verifyOTPDialogView.tvResendOTP?.setOnClickListener {
            handleResendOtpEvent(verifyOTPDialogView , applicant)
        }
        verifyOTPDialogView.ivCross?.setOnClickListener { dismissOtpVerificationDialog() }
        //verifyOTPDialogView.tvResendOTP?.callOnClick()
        timerOtpResend.start()
    }

    private fun handleResendOtpEvent(verifyOTPDialogView: View , applicant: PersonalApplicantsModel) {
        System.out.println("Sanjay Sawan Rawat")
        verifyOTPDialogView.lllayout_resend?.exGone()
        verifyOTPDialogView.tvResendOTPTimeLeftInfo?.exVisible()
        timerOtpResend.start()
        val leadMaster = LeadMetaData.getLeadData()
        leadMaster?.let {
            presenter.callNetwork(ConstantsApi.CALL_SEND_OTP , CallSendOTP(leadMaster , applicant))
        }
    }

    private fun handleOtbResendTimerEndEvent() {
        verifyOTPDialogView.lllayout_resend?.exVisible()
        verifyOTPDialogView.tvResendOTPTimeLeftInfo?.exGone()
        timerOtpResend.cancel()
    }

    private fun dismissOtpVerificationDialog() {
        timerOtpResend.cancel()
        verifyOTPDialog.dismiss()
    }

    private val minutes = 1L
    private val seconds = 60L
    private val millisecond = 1000L

    private val timerOtpResend = object : CountDownTimer(minutes * seconds * millisecond , 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val secondsUntilFinish = (millisUntilFinished / millisecond).toInt()
            verifyOTPDialogView.tvResendOTPTimeLeftInfo?.text = "$secondsUntilFinish ${context.getString(R.string.seconds)}"
        }

        override fun onFinish() {
            handleOtbResendTimerEndEvent()
        }
    }

    inner class CallSendOTP(private val leadMaster: AllLeadMaster , val applicant: PersonalApplicantsModel) : ViewGeneric<Requests.RequestSendOTP , Response.ResponseOTP>(context = activity) {
        override val apiRequest: Requests.RequestSendOTP
            get() = otpSendRequest

        private val otpSendRequest: Requests.RequestSendOTP
            get() {
                val leadId = leadMaster.leadID!!.toInt()
                val mobile = binding.basicInfoLayout.etMobile.text.toString()
                return Requests.RequestSendOTP(leadID = leadId , mobile = mobile)
            }

        override fun getApiSuccess(value: Response.ResponseOTP) {
            if(value.responseCode == Constants.SUCCESS){
                 var otpResponse : OtpTypeResponse ? = value.responseObj
                value.responseMsg?.let { showToast(it) }
                if(otpResponse?.isVerified.equals("true"))
                {
                    applicant.contactDetail!!.isMobileVerified = true
                    binding.basicInfoLayout.etMobile.isEnabled = false
                    binding.basicInfoLayout.btnGetOTP.visibility = View.GONE
                    binding.basicInfoLayout.ivVerifiedStatus.visibility = View.VISIBLE
                    binding.basicInfoLayout.mobileverifiedStatus.setText("true")
                }
                else
                {
                    value.responseMsg?.let { showToast(it) }
                    val leadId = leadMaster.leadID!!.toInt()
                    showVerifyOTPDialog(leadId , applicant)
                }
            }
           /* value.responseMsg?.let {
                showToast(value.responseMsg)
            }*/
        }
    }

    inner class CallVerifyOTP(private val leadId: Int? , val applicant: PersonalApplicantsModel) : ViewGeneric<Requests.RequestVerifyOTP , Response.ResponseVerifyOTP>(context = activity) {
        override val apiRequest: Requests.RequestVerifyOTP
            get() = otpVerifyRequest

        private val otpVerifyRequest: Requests.RequestVerifyOTP
            get() {
                val leadId = leadId
                val mobile = binding.basicInfoLayout.etMobile.text.toString()
                return Requests.RequestVerifyOTP(leadID = leadId , mobile = mobile , otpValue = otp!!)
            }

        override fun getApiSuccess(value: Response.ResponseVerifyOTP) {
            if (value.responseCode == Constants.SUCCESS) {
                dismissOtpVerificationDialog()
                applicant.contactDetail!!.isMobileVerified = true
                binding.basicInfoLayout.etMobile.isEnabled = false
                binding.basicInfoLayout.btnGetOTP.visibility = View.GONE
                binding.basicInfoLayout.ivVerifiedStatus.visibility = View.VISIBLE
                binding.basicInfoLayout.mobileverifiedStatus.setText("true")
            }
        }
    }

    inner class CallKYCDetail : ViewGeneric<Requests.RequestKycDetail , Response.ResponseKycDetail>(context = context) {
        override val apiRequest: Requests.RequestKycDetail
            get() = getKycDetail()

        override fun getApiSuccess(value: Response.ResponseKycDetail) {
            if (value.responseCode == Constants.SUCCESS) {
                binding.progressBar!!.visibility = View.GONE

                if (value.responseObj.kycApplicantDetailsList.size > 0) {
                    val kycDetailResponse: KycListModel = value.responseObj

                    // open Fragment Dilaog here
                    showKYCDetailDialog(kycDetailResponse)
                } else {
                    showToast("KYC is not available now.")  //value.responseMsg
                    binding.progressBar!!.visibility = View.GONE
                }
            } else {
                showToast(value.responseMsg)
                binding.progressBar!!.visibility = View.GONE
            }
        }

        override fun getApiFailure(msg: String) {
                System.out.println("Api Failure>>>>"+msg)
            if (msg.exIsNotEmptyOrNullOrBlank()) {
                super.getApiFailure(msg)
                binding.progressBar!!.visibility = View.GONE
            } else {
                super.getApiFailure("Time out Error")
                binding.progressBar!!.visibility = View.GONE
            }

        }

        private fun getKycDetail(): Requests.RequestKycDetail {
            val leadId: Int? = LeadMetaData.getLeadId()
            val leadApplicantNumber: String = selectedApplicantNumber

            return Requests.RequestKycDetail(leadID = leadId!! , leadApplicantNumber = leadApplicantNumber) //return Requests.RequestKycDetail(leadID = 2,leadApplicantNumber= "2001")

        }
    }


    fun isApplicantDetailsValid() = formValidation.validatePersonalInfo(binding , spinnerDMList , religion)

    fun getApplicant(): PersonalApplicantsModel {
        return getCurrentApplicant()
    }

    private fun showKYCDetailDialog(kycDetailResponse: KycListModel) {

        val bindingDialog = DataBindingUtil.inflate<DialogKycDetailBinding>(LayoutInflater.from(context) , R.layout.dialog_kyc_detail , null , false)
        val mBuilder = AlertDialog.Builder(context)
                .setView(bindingDialog.root)
                .setCancelable(false)

        detailKycDialog = mBuilder.show()

        var name: String? = ""
        var pincode: String? = ""
        var genderValue: String? = ""
        var dob: String? = ""
        var address: String? = ""
        var careOf: String? = ""
        var addressNew: String? = ""
        var matchPercentage: String? = ""
        var faceAuthStatus : String ? = ""

        for (i in 0 until kycDetailResponse.kycApplicantDetailsList.size) {

              if(kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList.size>0 && kycDetailResponse.kycApplicantDetailsList[i].latestKycObj == "kycAadharZipInlineDataList") {
                  for (j in 0 until kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList.size) {
                      if(j ==0) {
                          pincode = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].pinCode
                          name = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].name
                          genderValue = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].gender
                          dob = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].dob
                          address = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].address
                          careOf = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].careOf
                          matchPercentage = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].faceAuthScore
                          faceAuthStatus = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].faceAuthStatus
                          bindingDialog.tvName.text = name
                          bindingDialog.tvcareof.text = careOf
                          bindingDialog.tvGender.text = if (genderValue.equals("M")) "Male" else if (genderValue.equals("F")) "Female" else "TransGender"
                          bindingDialog.tvAddress.text = address
                          bindingDialog.tvdob.text = ConvertDate().convertToAppFormatNew(dob)
                          dob = bindingDialog.tvdob.text.toString()
                          bindingDialog.matchpercentage.text = matchPercentage
                          bindingDialog.faceAuthStatus.text = faceAuthStatus
                      }
                  }
              }
            else  if(kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList.size>0 && kycDetailResponse.kycApplicantDetailsList[i].latestKycObj == "kycPanQrCodeDataList") {
                  for (j in 0 until kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList.size) {
                      if(j ==0) {
                          pincode = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].pincode
                          name = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].name
                          genderValue = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].gender
                          dob = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].dob
                          address = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].address
                          careOf = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].careOf
                          matchPercentage = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].faceAuthScore
                          faceAuthStatus = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].faceAuthStatus

                          System.out.println("Name>>>>>>" + name)
                          bindingDialog.tvName.text = name
                          bindingDialog.tvcareof.text = careOf
                          bindingDialog.tvGender.text = if (genderValue.equals("male")) "Male" else if (genderValue.equals("female")) "Female" else "TransGender"
                          bindingDialog.tvAddress.text = address
                          bindingDialog.tvdob.text = ConvertDate().convertToAppFormatNew(dob)
                          dob = bindingDialog.tvdob.text.toString()
                          bindingDialog.matchpercentage.text = matchPercentage
                          bindingDialog.faceAuthStatus.text = faceAuthStatus
                      }
                  }
              }
            else if(kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList.size>0 && kycDetailResponse.kycApplicantDetailsList[i].latestKycObj == "kycDLQrCodeDataList") {
                  for (j in 0 until kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList.size) {
                      if(j ==0) {
                          pincode = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].pincode
                          name = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].name
                          genderValue = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].gender
                          dob = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].dob
                          address = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].address
                          careOf = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].careOf
                          matchPercentage = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].faceAuthScore
                          faceAuthStatus = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].faceAuthStatus
                          System.out.println("Name>>>>>>" + name)
                          bindingDialog.tvName.text = name
                          bindingDialog.tvcareof.text = careOf
                          bindingDialog.tvGender.text = if (genderValue.equals("male")) "Male" else if (genderValue.equals("female")) "Female" else "TransGender"
                          bindingDialog.tvAddress.text = address
                          bindingDialog.tvdob.text = ConvertDate().convertToAppFormatNew(dob)
                          dob = bindingDialog.tvdob.text.toString()
                          bindingDialog.matchpercentage.text = matchPercentage
                          bindingDialog.faceAuthStatus.text = faceAuthStatus
                      }


                  }

              }
            else if(kycDetailResponse.kycApplicantDetailsList[i].kycVoterCardQrCodeDataList.size > 0 && kycDetailResponse.kycApplicantDetailsList[i].latestKycObj == "kycVoterCardQrCodeDataList"){

                  for (j in 0 until kycDetailResponse.kycApplicantDetailsList[i].kycVoterCardQrCodeDataList.size) {
                      if(j ==0) {
                          //pincode = kycDetailResponse.kycApplicantDetailsList[i].kycVoterCardQrCodeDataList[j].pincode
                          name = kycDetailResponse.kycApplicantDetailsList[i].kycVoterCardQrCodeDataList[j].name
                          genderValue = kycDetailResponse.kycApplicantDetailsList[i].kycVoterCardQrCodeDataList[j].gender
                          dob = kycDetailResponse.kycApplicantDetailsList[i].kycVoterCardQrCodeDataList[j].dob
                          address = kycDetailResponse.kycApplicantDetailsList[i].kycVoterCardQrCodeDataList[j].address
                          //careOf = kycDetailResponse.kycApplicantDetailsList[i].kycVoterCardQrCodeDataList[j].careOf
                          matchPercentage = kycDetailResponse.kycApplicantDetailsList[i].kycVoterCardQrCodeDataList[j].faceAuthScore
                          faceAuthStatus = kycDetailResponse.kycApplicantDetailsList[i].kycVoterCardQrCodeDataList[j].faceAuthStatus
                          System.out.println("Name>>>>>>" + name)
                          bindingDialog.tvName.text = name
                          //bindingDialog.tvcareof.text = careOf
                          bindingDialog.tvGender.text = if (genderValue.equals("male")) "Male" else if (genderValue.equals("female")) "Female" else "TransGender"
                          bindingDialog.tvAddress.text = address
                          bindingDialog.tvdob.text = ConvertDate().convertToPANQRFormat(dob)
                          dob = bindingDialog.tvdob.text.toString()
                          bindingDialog.matchpercentage.text = matchPercentage
                          bindingDialog.faceAuthStatus.text = faceAuthStatus
                      }
                  }
              }

        }

        bindingDialog?.btnClose?.setOnClickListener() {
            detailKycDialog?.dismiss()
        }
        bindingDialog?.btnMove?.setOnClickListener() {

            var delimiter = " "
            val parts = name!!.split(delimiter)
            for (i in 0 until parts.size) {
                if (parts.size == 1) {
                    binding.basicInfoLayout.etFirstName.setText(parts[0])
                }
                if (parts.size == 2) {
                    binding.basicInfoLayout.etFirstName.setText(parts[0])
                    binding.basicInfoLayout.etLastName.setText(parts[1])
                }
                if (parts.size == 3) {
                    binding.basicInfoLayout.etFirstName.setText(parts[0])
                    binding.basicInfoLayout.etMiddleName.setText(parts[1])
                    binding.basicInfoLayout.etLastName.setText(parts[2])
                }
            }

            if (genderValue.equals("M")) {
                gender.setSelection("1")
            } else if (genderValue.equals("F")) {
                gender.setSelection("2")
            } else {
                gender.setSelection("3")
            }

            binding.personalAddressLayout.customCurrentZipAddressView.etCurrentPinCode.setText(pincode.toString())
            binding.basicInfoLayout.etDOB.setText(dob)
            binding.personalAddressLayout.customPermanentZipAddressView.pinCode

            //addressNew = address!!.substring(0 , address.length - 7)
            binding.personalAddressLayout.etCurrentAddress.setText(address)
            if(!(dob.equals(""))) {
                val pattern = "dd-MMM-yyyy"
                val sdf = SimpleDateFormat(pattern , Locale.US)
                val date = sdf.parse(dob)
                setDifferenceInField(date , binding.basicInfoLayout.etAge)
            }
            detailKycDialog?.dismiss()

        }

    }

    private fun setDifferenceInField(date: Date , differenceField: TextView) {
        val todayDate = Date()
        val difference = todayDate.year - date.year
        differenceField.text = difference.toString()
    }
    private fun callApiKycStatus(leadId: Int?) {

        presenter.callNetwork(ConstantsApi.CALL_KYC_DETAIL , CallKYCStatusDetail())
        binding.progressBar!!.visibility = View.VISIBLE
    }
    inner class CallKYCStatusDetail : ViewGeneric<Requests.RequestKycDetail , Response.ResponseKycDetail>(context = context) {
        override val apiRequest: Requests.RequestKycDetail
            get() = getKycDetail()
        override fun getApiSuccess(value: Response.ResponseKycDetail) {
            if (value.responseCode == Constants.SUCCESS) {
                binding.progressBar!!.visibility = View.GONE
                System.out.println("Size of Response>>>>>"+value.responseObj.kycApplicantDetailsList)
                if (value.responseObj.kycApplicantDetailsList.size > 0) {
                    val kycDetailResponse: KycListModel = value.responseObj
                    setMatchPercentage(kycDetailResponse)
                } else {
                    showToast("KYC is not available now.")  //value.responseMsg
                    binding.progressBar!!.visibility = View.GONE
                }
            } else {
                showToast(value.responseMsg)
                binding.progressBar!!.visibility = View.GONE
            }
        }

        override fun getApiFailure(msg: String) {
            System.out.println("Api Failure>>>>"+msg)
            if (msg.exIsNotEmptyOrNullOrBlank()) {
                super.getApiFailure(msg)
                binding.progressBar!!.visibility = View.GONE
            } else {
                super.getApiFailure("Time out Error")
                binding.progressBar!!.visibility = View.GONE
            }

        }

        private fun getKycDetail(): Requests.RequestKycDetail {
            val leadId: Int? = LeadMetaData.getLeadId()
            val leadApplicantNumber: String = selectedApplicantNumber!!

            return Requests.RequestKycDetail(leadID = leadId!! , leadApplicantNumber = leadApplicantNumber) //return Requests.RequestKycDetail(leadID = 2,leadApplicantNumber= "2001")

        }
    }
    private fun setMatchPercentage(kycDetailResponse: KycListModel){
        var matchPercentage: String? = ""
        var name : String ? = ""
        var faceAuthStatus : String ? = ""
        for (i in 0 until kycDetailResponse.kycApplicantDetailsList.size) {
            if(kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList.size>0 && kycDetailResponse.kycApplicantDetailsList[i].latestKycObj == "kycAadharZipInlineDataList") {
                for (j in 0 until kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList.size) {
                    if(j == 0) {
                        matchPercentage = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].faceAuthScore
                        faceAuthStatus = kycDetailResponse.kycApplicantDetailsList[i].kycAadharZipInlineDataList[j].faceAuthStatus
                        var matchPercentageScore = matchPercentage?.replace("%","")?.toInt()
                        System.out.println("Match Percentage>>>>"+matchPercentage)
                        System.out.println("Match Percentage>>>>"+faceAuthStatus)
                        if (matchPercentageScore != null) {
                            if(matchPercentageScore>=90 ) {
                                if(matchPercentageScore == 100)
                                {
                                    //Perform document Upload process
                                    allMasterDropdown?.let {
                                        val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.KYC_DOCUMENT , true) }
                                        val bundle = Bundle()
                                        bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)
                                        bundle.putString(Constants.KEY_TITLE , context.getString(R.string.kyc_auth_image))
                                        bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                                        PerformKycDocumentUploadActivity.startActivity(context , bundle)
                                    }
                                }
                                else{
                                    Toast.makeText(context,"Your Kyc has been completed. No need to upload KYC document",Toast.LENGTH_LONG).show()
                                }

                            } else if(matchPercentageScore<90){
                                //Perform document Upload process
                                allMasterDropdown?.let {
                                    val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.KYC_DOCUMENT , true) }
                                    val bundle = Bundle()
                                    bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)
                                    bundle.putString(Constants.KEY_TITLE , context.getString(R.string.kyc_auth_image))
                                    bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                                    PerformKycDocumentUploadActivity.startActivity(context , bundle)

                                }
                            }

                        }

                    }
                }
            }
            else  if(kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList.size>0 && kycDetailResponse.kycApplicantDetailsList[i].latestKycObj == "kycPanQrCodeDataList") {
                for (j in 0 until kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList.size) {
                    if(j == 0) {
                        name = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].name
                        matchPercentage = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].faceAuthScore
                        faceAuthStatus = kycDetailResponse.kycApplicantDetailsList[i].kycPanQrCodeDataList[j].faceAuthStatus
                        System.out.println("Match Percentage>>>>"+matchPercentage)
                        System.out.println("Match Percentage>>>>"+faceAuthStatus)
                        var matchPercentageScore = matchPercentage?.replace("%","")?.toInt()
                        if (matchPercentageScore != null) {
                            if(matchPercentageScore>=90 ) {
                                if(matchPercentageScore == 100)
                                {
                                    //Perform document Upload process
                                    allMasterDropdown?.let {
                                        val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.KYC_DOCUMENT , true) }
                                        val bundle = Bundle()
                                        bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)
                                        bundle.putString(Constants.KEY_TITLE , context.getString(R.string.kyc_auth_image))
                                        bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                                        PerformKycDocumentUploadActivity.startActivity(context , bundle)
                                    }
                                }
                                else{
                                    Toast.makeText(context,"Your Kyc has been completed. No need to upload KYC document",Toast.LENGTH_LONG).show()
                                }

                            } else if(matchPercentageScore<90){
                                //Perform document Upload process
                                allMasterDropdown?.let {
                                    val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.KYC_DOCUMENT , true) }
                                    val bundle = Bundle()
                                    bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)
                                    bundle.putString(Constants.KEY_TITLE , context.getString(R.string.kyc_auth_image))
                                    bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                                    PerformKycDocumentUploadActivity.startActivity(context , bundle)

                                }
                            }

                        }
                    }
                }
            }
            else if(kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList.size>0 && kycDetailResponse.kycApplicantDetailsList[i].latestKycObj == "kycDLQrCodeDataList") {
                for (j in 0 until kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList.size) {
                    if(j == 0) {
                        matchPercentage = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].faceAuthScore
                        faceAuthStatus = kycDetailResponse.kycApplicantDetailsList[i].kycDLQrCodeDataList[j].faceAuthStatus
                        System.out.println("Match Percentage>>>>"+matchPercentage)
                        System.out.println("Match Percentage>>>>"+faceAuthStatus)
                        var matchPercentageScore = matchPercentage?.replace("%","")?.toInt()
                        if (matchPercentageScore != null) {
                            if(matchPercentageScore>=90 ) {
                                if(matchPercentageScore == 100)
                                {
                                    //Perform document Upload process
                                    allMasterDropdown?.let {
                                        val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.KYC_DOCUMENT , true) }
                                        val bundle = Bundle()
                                        bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)
                                        bundle.putString(Constants.KEY_TITLE , context.getString(R.string.kyc_auth_image))
                                        bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                                        PerformKycDocumentUploadActivity.startActivity(context , bundle)
                                    }
                                }
                                else{
                                    Toast.makeText(context,"Your Kyc has been completed. No need to upload KYC document",Toast.LENGTH_LONG).show()
                                }

                            } else if(matchPercentageScore<90){
                                //Perform document Upload process
                                allMasterDropdown?.let {
                                    val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.KYC_DOCUMENT , true) }
                                    val bundle = Bundle()
                                    bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)
                                    bundle.putString(Constants.KEY_TITLE , context.getString(R.string.kyc_auth_image))
                                    bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                                    PerformKycDocumentUploadActivity.startActivity(context , bundle)

                                }
                            }

                        }
                    }
                }

            }
            else if(kycDetailResponse.kycApplicantDetailsList[i].kycPanDLDataList.size>0 && kycDetailResponse.kycApplicantDetailsList[i].latestKycObj == "kycPanDLDataList") {
                for (j in 0 until kycDetailResponse.kycApplicantDetailsList[i].kycPanDLDataList.size) {
                    if(j == 0) {
                        matchPercentage = kycDetailResponse.kycApplicantDetailsList[i].kycPanDLDataList[j].faceAuthScore
                        faceAuthStatus = kycDetailResponse.kycApplicantDetailsList[i].kycPanDLDataList[j].faceAuthStatus
                        System.out.println("Match Percentage>>>>"+matchPercentage)
                        System.out.println("Match Percentage>>>>"+faceAuthStatus)
                        var matchPercentageScore = matchPercentage?.replace("%","")?.toInt()
                        if (matchPercentageScore != null) {
                            if(matchPercentageScore>=90 ) {
                                if(matchPercentageScore == 100)
                                {
                                    //Perform document Upload process
                                    allMasterDropdown?.let {
                                        val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.KYC_DOCUMENT , true) }
                                        val bundle = Bundle()
                                        bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)
                                        bundle.putString(Constants.KEY_TITLE , context.getString(R.string.kyc_auth_image))
                                        bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                                        PerformKycDocumentUploadActivity.startActivity(context , bundle)
                                    }
                                }
                                else{
                                    Toast.makeText(context,"Your Kyc has been completed. No need to upload KYC document",Toast.LENGTH_LONG).show()
                                }

                            } else if(matchPercentageScore<90){
                                //Perform document Upload process
                                allMasterDropdown?.let {
                                    val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.KYC_DOCUMENT , true) }
                                    val bundle = Bundle()
                                    bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)
                                    bundle.putString(Constants.KEY_TITLE , context.getString(R.string.kyc_auth_image))
                                    bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                                    PerformKycDocumentUploadActivity.startActivity(context , bundle)

                                }
                            }

                        }
                    }
                }

            }
            else if(kycDetailResponse.kycApplicantDetailsList[i].kycVoterCardQrCodeDataList.size>0 && kycDetailResponse.kycApplicantDetailsList[i].latestKycObj == "kycVoterCardQrCodeDataList") {
                for (j in 0 until kycDetailResponse.kycApplicantDetailsList[i].kycVoterCardQrCodeDataList.size) {
                    if(j == 0) {
                        matchPercentage = kycDetailResponse.kycApplicantDetailsList[i].kycVoterCardQrCodeDataList[j].faceAuthScore
                        faceAuthStatus = kycDetailResponse.kycApplicantDetailsList[i].kycVoterCardQrCodeDataList[j].faceAuthStatus
                        System.out.println("Match Percentage>>>>"+matchPercentage)
                        System.out.println("Match Percentage>>>>"+faceAuthStatus)
                        var matchPercentageScore = matchPercentage?.replace("%","")?.toInt()
                        if (matchPercentageScore != null) {
                            if(matchPercentageScore>=90 ) {
                                if(matchPercentageScore == 100)
                                {
                                    //Perform document Upload process
                                    allMasterDropdown?.let {
                                        val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.KYC_DOCUMENT , true) }
                                        val bundle = Bundle()
                                        bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)
                                        bundle.putString(Constants.KEY_TITLE , context.getString(R.string.kyc_auth_image))
                                        bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                                        PerformKycDocumentUploadActivity.startActivity(context , bundle)
                                    }
                                }
                                else{
                                    Toast.makeText(context,"Your Kyc has been completed. No need to upload KYC document",Toast.LENGTH_LONG).show()
                                }

                            } else if(matchPercentageScore<90){
                                //Perform document Upload process
                                allMasterDropdown?.let {
                                    val docCodeID = it.DocumentCode?.find { item -> item.typeDetailCode.equals(Constants.KYC_DOCUMENT , true) }
                                    val bundle = Bundle()
                                    bundle.putInt(Constants.KEY_DOC_ID , docCodeID!!.typeDetailID)
                                    bundle.putString(Constants.KEY_TITLE , context.getString(R.string.kyc_auth_image))
                                    bundle.putString(Constants.KEY_APPLICANT_NUMBER , selectedApplicantNumber)
                                    PerformKycDocumentUploadActivity.startActivity(context , bundle)

                                }
                            }

                        }
                    }
                }

            }

        }
    }

}
