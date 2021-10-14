package com.finance.app.view.adapters.recycler.adapter

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.finance.app.others.APPLICANT
import com.finance.app.others.CO_APPLICANT
import com.finance.app.persistence.model.AssetLiabilityModel
import com.finance.app.persistence.model.DocumentCheckList
import com.finance.app.persistence.model.DocumentCheckListModel
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.view.fragment.loanApplicationFragments.document_checklist.DocumentChecklistForm

class DocumentCheckLIstPagerAdapter  internal constructor(fm: FragmentManager, val applicantsList: ArrayList<PersonalApplicantsModel>) : FragmentStatePagerAdapter(fm) {

    private val fragmentList = ArrayList<DocumentChecklistForm>()

    override fun getPageTitle(position: Int) = if (applicantsList[position].isMainApplicant) APPLICANT else "$CO_APPLICANT $position"

    override fun getCount() = applicantsList.size

    override fun getItem(position: Int): DocumentChecklistForm {
        if (fragmentList.isNotEmpty() && fragmentList.size > position)
            return fragmentList[position]
        val fragmentItem = DocumentChecklistForm.newInstance(applicantsList[position])
        fragmentList.add(fragmentItem)
        return fragmentItem
    }
//model change to Documentchecklist model
    fun getAllChecklistDetail(): ArrayList<DocumentCheckList> {
        val list = ArrayList<DocumentCheckList>()
        fragmentList.forEach { it ->
            list.add(it.getDocumentChecklist())
        }
        return list
    }
   fun isDocumentDetailsValid(): Boolean {
        fragmentList.forEach {
            if (it.isDocumentDetailsValid().not())
                return false
        }
        return true
    }
    fun getAllFragments() = fragmentList
}