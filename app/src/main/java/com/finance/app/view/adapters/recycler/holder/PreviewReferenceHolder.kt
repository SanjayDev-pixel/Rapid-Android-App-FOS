package com.finance.app.view.adapters.recycler.holder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.databinding.PreviewLayoutReferenceBinding
import com.finance.app.others.AppEnums
import com.finance.app.persistence.model.ReferenceModel
import com.finance.app.utility.LeadMetaData
import com.finance.app.viewModel.AppDataViewModel

class PreviewReferenceHolder(val binding: PreviewLayoutReferenceBinding, val mContext: Context)
    : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(list: ArrayList<ReferenceModel>?, pos: Int, viewModel: AppDataViewModel) {

        if (!list.isNullOrEmpty()) {
            System.out.print("Sizeee>>>"+list.size)
            try{
            setValueInCard(list[pos], viewModel)
               /* var applicantType :String = ""
                if(LeadMetaData.getLeadData()?.personalData!!.applicantDetails[pos].isMainApplicant==true){
                    applicantType =  "Applicant"
                }else{ applicantType="Co-Applicant"}*/
                //binding.tvCoApplicant.setText(applicantType.plus("   : ").plus(LeadMetaData.getLeadData()?.personalData!!.applicantDetails[pos].firstName))
        }catch(e:IndexOutOfBoundsException){e.printStackTrace()}
            //handleCollapse()

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

    private fun setValueInCard(data: ReferenceModel, viewModel: AppDataViewModel) {

        binding.tvName.text = data.name
        binding.tvKnownSince.text = data.knowSince
        binding.tvContact.text = data.contactNumber

        data.addressBean?.let {
            binding.tvAddress.text = data.addressBean!!.address1
            binding.tvLandmark.text = data.addressBean!!.landmark
            binding.tvPincode.text = data.addressBean!!.zip
            binding.tvState.text = data.addressBean!!.stateName
            binding.tvCity.text= data.addressBean!!.cityName
            binding.tvDistrict.text= data.addressBean!!.districtName
        }

        viewModel.getMasterDropdownNameFromId(data.relationTypeDetailID, AppEnums.DropdownMasterType.ReferenceRelationship,
                binding.tvRelation)
        viewModel.getMasterDropdownNameFromId(data.occupationTypeDetailID, AppEnums.DropdownMasterType.OccupationType,
                binding.tvOccupation)

    }

}