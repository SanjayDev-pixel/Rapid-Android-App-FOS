package com.finance.app.view.adapters.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.finance.app.others.APPLICANT
import com.finance.app.others.CO_APPLICANT
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.view.fragment.loanApplicationFragments.document_upload_kyc.DocumentFormFragment

class DocumentUploadPagerAdapter(fm: FragmentManager , private val applicantsList: ArrayList<PersonalApplicantsModel>) : FragmentStatePagerAdapter(fm) {
    private val fragmentList = ArrayList<DocumentFormFragment>()

    override fun getPageTitle(position: Int) = if (applicantsList[position].isMainApplicant) APPLICANT else "$CO_APPLICANT $position"

    override fun getCount() = applicantsList.size

    override fun getItem(position: Int): DocumentFormFragment {
        if (fragmentList.isNotEmpty() && fragmentList.size > position)
            return fragmentList[position]

        val fragment = DocumentFormFragment.newInstance(applicantsList[position])
        fragmentList.add(fragment)
        return fragment
    }
}