package com.finance.app.view.adapters.pager

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.finance.app.others.APPLICANT
import com.finance.app.others.CO_APPLICANT
import com.finance.app.persistence.model.EmploymentApplicantsModel
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.view.fragment.loanApplicationFragments.employment.EmploymentFormFragmentNew

class EmploymentFormPagerAdapter(fm: FragmentManager, private val applicantsList: ArrayList<PersonalApplicantsModel>) : FragmentStatePagerAdapter(fm) {

    private val fragmentList = ArrayList<EmploymentFormFragmentNew>()

    override fun getPageTitle(position: Int) = if (applicantsList[position].isMainApplicant) APPLICANT else "$CO_APPLICANT $position"

    override fun getCount() = applicantsList.size

    override fun getItem(position: Int): EmploymentFormFragmentNew {
        if (fragmentList.isNotEmpty() && fragmentList.size > position)
            return fragmentList[position]

        val fragment = EmploymentFormFragmentNew.newInstance(applicantsList[position])
        fragmentList.add(fragment)
        return fragment
    }

    fun isEmploymentDetailsValid(): Boolean {
        fragmentList.forEach { item ->
            if (item.isEmploymentDetailsValid().not())
                return false
        }
        return true
    }

    fun getEmploymentDetails(): ArrayList<EmploymentApplicantsModel> {
        val list = ArrayList<EmploymentApplicantsModel>()
        fragmentList.forEach { item ->
            item.getApplicantEmploymentDetails().let { list.add(it) }
        }
        return list
    }


}