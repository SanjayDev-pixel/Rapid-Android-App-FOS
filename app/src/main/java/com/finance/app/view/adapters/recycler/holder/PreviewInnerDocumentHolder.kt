package com.finance.app.view.adapters.recycler.holder

import android.content.Context
import androidx.core.os.persistableBundleOf
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.databinding.PreviewDocumentChecklistItemBinding
import com.finance.app.persistence.model.DocumentCheckList
import com.finance.app.persistence.model.DocumentCheckListDetailModel
import com.finance.app.viewModel.AppDataViewModel

class PreviewInnerDocumentHolder (val binding:PreviewDocumentChecklistItemBinding,val mContext: Context):RecyclerView.ViewHolder(binding.root){
   fun bindItems(list: ArrayList<DocumentCheckListDetailModel>?, pos:Int, viewModel: AppDataViewModel){
       if (!list.isNullOrEmpty()) {
           System.out.println("position>>>>"+pos)
           setValueInCard(list[pos], viewModel,pos)
       }
   }

    private fun setValueInCard(data: DocumentCheckListDetailModel, viewModel: AppDataViewModel,position : Int) {

        binding.questiontext.text =data.description
        binding.qAns.text= data.typeDetailDisplayText
        binding.serialNo.text =""+(position+1)

    }


}


