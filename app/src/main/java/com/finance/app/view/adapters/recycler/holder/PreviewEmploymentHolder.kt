package com.finance.app.view.adapters.recycler.holder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.databinding.PreviewLayoutEmploymentBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.EmploymentApplicantsModel
import com.finance.app.utility.LeadMetaData
import com.finance.app.viewModel.AppDataViewModel
import kotlinx.android.synthetic.main.obligation_item_dialog.*
import kotlinx.android.synthetic.main.preview_layout_salary.view.*
import kotlinx.android.synthetic.main.preview_layout_senp.view.*
import kotlinx.android.synthetic.main.preview_layout_senp.view.tvAddress
import kotlinx.android.synthetic.main.preview_layout_senp.view.tvContact
import kotlinx.android.synthetic.main.preview_layout_senp.view.tvIndustry
import kotlinx.android.synthetic.main.preview_layout_senp.view.tvPinCode
import kotlinx.android.synthetic.main.preview_layout_senp.view.tvState
import motobeans.architecture.constants.Constants

class PreviewEmploymentHolder(val binding: PreviewLayoutEmploymentBinding, val mContext: Context)
    : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(list: ArrayList<EmploymentApplicantsModel>?, pos: Int, viewModel: AppDataViewModel) {

        if (!list.isNullOrEmpty()) {
            try {
                setValueInCard(list[pos], viewModel)
            }catch(e:IndexOutOfBoundsException){e.printStackTrace()}
            handleCollapse()


            var applicantType :String = ""
            if(LeadMetaData.getLeadData()?.personalData!!.applicantDetails[pos].isMainApplicant==true){
              applicantType =  "Applicant"
            }else{ applicantType="Co-Applicant"}

            //binding.tvCoApplicant.setText(LeadMetaData.getLeadData()?.personalData!!.applicantDetails[pos].firstName?.plus("   ").plus(applicantType))
            binding.tvCoApplicant.setText(applicantType.plus("   : ").plus(LeadMetaData.getLeadData()?.personalData!!.applicantDetails[pos].firstName))


        }
}

private fun handleCollapse() {
binding.collapseForm.setOnClickListener {
binding.cardApplicant.visibility = View.GONE
binding.collapseForm.visibility = View.GONE
binding.expandForm.visibility = View.VISIBLE
}

binding.expandForm.setOnClickListener {
binding.cardApplicant.visibility = View.VISIBLE
binding.collapseForm.visibility = View.VISIBLE
binding.expandForm.visibility = View.GONE
}
}

private fun setValueInCard(data: EmploymentApplicantsModel, viewModel: AppDataViewModel) {

viewModel.getMasterDropdownNameFromId(data.profileSegmentTypeDetailID, AppEnums.DropdownMasterType.ProfileSegment,
    binding.tvProfile)

viewModel.getMasterDropdownNameFromId(data.subProfileTypeDetailID, AppEnums.DropdownMasterType.SubProfileSegment,
    binding.tvSubProfile)

fillSalaryData(data, viewModel)
fillSenpData(data, viewModel)

showDataBasedOnUserSelection(data.subProfileTypeDetailID)


}

private fun showDataBasedOnUserSelection(id: Int?) {
when (id) {
Constants.CASH_SALARY, Constants.BANK_SALARY -> {
    binding.previewLayoutSalary.llSalaryPreview.visibility = View.VISIBLE
    binding.previewLayoutSenp.llSenpPreview.visibility = View.GONE
}
Constants.ITR, Constants.ASSESED_INCOME -> {
    binding.previewLayoutSalary.llSalaryPreview.visibility = View.GONE
    binding.previewLayoutSenp.llSenpPreview.visibility = View.VISIBLE
}
}
}

private fun fillSalaryData(data: EmploymentApplicantsModel, viewModel: AppDataViewModel) {

data.addressBean?.let {
binding.previewLayoutSalary.tvAddress.text = data.addressBean!!.address1
binding.previewLayoutSalary.tvPinCode.text = data.addressBean?.zip
binding.previewLayoutSalary.tvContact.text = data.addressBean?.contactNum
viewModel.getStateNameFromId(data.addressBean?.stateID, binding.previewLayoutSalary.tvState)
binding.previewLayoutSalary.tvCity.text=data.addressBean?.cityName
binding.previewLayoutSalary.tvDistrict.text=data.addressBean?.districtName


}

data.incomeDetail?.let {
binding.previewLayoutSalary.tvGrossIncome.text = data.incomeDetail?.salariedGrossIncome.toString()
binding.previewLayoutSalary.tvDeduction.text = data.incomeDetail?.salariedDeduction.toString()
binding.previewLayoutSalary.tvNetIncome.text = data.incomeDetail?.salariedNetIncome.toString()
}

binding.previewLayoutSalary.tvCompanyName.text = data.companyName
binding.previewLayoutSalary.tvDesignation.text = data.designation
binding.previewLayoutSalary.tvOfficialMail.text = data.officialMailID
binding.previewLayoutSalary.tvEmployId.text = data.employeeID
binding.previewLayoutSalary.tvJoiningDate.text = data.dateOfJoining
binding.previewLayoutSalary.tvTotalExp.text = data.totalExperience
binding.previewLayoutSalary.tvRetirementAge.text = data.retirementAge.toString()
binding.previewLayoutSalary.tvContact.text = data.employerContactNumber

viewModel.getMasterDropdownNameFromId(data.sectorTypeDetailID, AppEnums.DropdownMasterType.Sector,
    binding.previewLayoutSalary.tvSector)
viewModel.getMasterDropdownNameFromId(data.industryTypeDetailID, AppEnums.DropdownMasterType.Industry,
    binding.previewLayoutSalary.tvIndustry)
viewModel.getMasterDropdownNameFromId(data.employmentTypeDetailID, AppEnums.DropdownMasterType.EmploymentType,
    binding.previewLayoutSalary.tvEmploymentType)

/* if(data.isMainApplicant==true){
binding.tvCoApplicant.text="Applicant"
}else{
binding.tvCoApplicant.text="Co-Applicant"
}
val leadId: Int? = LeadMetaData.getLeadId()
leadId?.let {
viewModel.getCoapplicantsList(it)

}
*/



}

private fun fillSenpData(data: EmploymentApplicantsModel, viewModel: AppDataViewModel) {
data.incomeDetail?.let {
binding.previewLayoutSenp.tvLastYearIncome.text = data.incomeDetail!!.itrLastYearIncome.toString()
binding.previewLayoutSenp.tvCurrentYearIncome.text = data.incomeDetail!!.itrCurrentYearIncome.toString()
binding.previewLayoutSenp.tvAverageMonthlyIncome.text = data.incomeDetail!!.itrAverageMonthlyIncome.toString()
binding.previewLayoutSenp.tvMonthlyIncome.text = data.incomeDetail!!.assessedMonthlyIncome.toString()
}

data.addressBean?.let {
binding.previewLayoutSenp.tvAddress.text = data.addressBean!!.address1
binding.previewLayoutSenp.tvPinCode.text = data.addressBean!!.zip
binding.previewLayoutSenp.tvContact.text = data.addressBean!!.contactNum
viewModel.getStateNameFromId(data.addressBean!!.stateID, binding.previewLayoutSenp.tvState)
binding.previewLayoutSenp.tvCity.text=data.addressBean?.cityName
binding.previewLayoutSenp.tvDistrict.text=data.addressBean?.districtName
}

binding.previewLayoutSenp.tvBusinessName.text = data.companyName
binding.previewLayoutSenp.tvGst.text = data.gstRegistration
binding.previewLayoutSenp.tvIncorporationDate.text = data.dateOfIncorporation
binding.previewLayoutSenp.tvBusinessVintage.text = data.businessVinatgeInYear.toString()
binding.previewLayoutSenp.tvBusinessName.text = data.companyName
binding.previewLayoutSenp.tvBusinessName.text = data.companyName

viewModel.getMasterDropdownNameFromId(data.constitutionTypeDetailID, AppEnums.DropdownMasterType.Constitution,
    binding.previewLayoutSenp.tvConstitution)
viewModel.getMasterDropdownNameFromId(data.industryTypeDetailID, AppEnums.DropdownMasterType.Industry,
    binding.previewLayoutSenp.tvIndustry)
viewModel.getMasterDropdownNameFromId(data.businessSetupTypeDetailID, AppEnums.DropdownMasterType.BusinessSetupType,
    binding.previewLayoutSenp.tvBusinessSetUpType)

/*
if(data.isMainApplicant==true){
binding.tvCoApplicant.text="Applicant"
}else{
binding.tvCoApplicant.text="Co-Applicant"
}*/


}

}