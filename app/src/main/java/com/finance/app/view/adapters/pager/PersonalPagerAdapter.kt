package com.finance.app.view.adapters.pager

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.finance.app.others.APPLICANT
import com.finance.app.others.CO_APPLICANT
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.view.fragment.loanApplicationFragments.personal.PersonalFormFragmentNew

class PersonalPagerAdapter internal constructor(fm: FragmentManager, val applicantsList: ArrayList<PersonalApplicantsModel>) : FragmentStatePagerAdapter(fm) {

    private val fragmentList = ArrayList<PersonalFormFragmentNew>()

    init {
        if (applicantsList.isEmpty()) {
            applicantsList.add(PersonalApplicantsModel()) //Default applicant in case of empty...
        }
    }

    override fun getCount() = applicantsList.size

    override fun getPageTitle(position: Int) = if (applicantsList[position].isMainApplicant || position == 0) APPLICANT else "$CO_APPLICANT $position"

    override fun getItem(position: Int): PersonalFormFragmentNew {
        if (fragmentList.isNotEmpty() && fragmentList.size > position)
            return fragmentList[position]

        val fragment = PersonalFormFragmentNew.newInstance(applicantsList[position], position)

            fragmentList.add(fragment)

        return fragment
    }

    fun addItem() {
        applicantsList.add(PersonalApplicantsModel())
        notifyDataSetChanged()
    }
    fun deleteItem(position: Int) {
        System.out.println("ApplucantListSize>>>>"+applicantsList.size)
        if (position >= 0) {

            applicantsList.removeAt(position)
            fragmentList.removeAt(position)
            notifyDataSetChanged()
        }
        System.out.println("ApplucantListSize>>>>"+applicantsList.size)
    }

    fun isApplicantDetailsValid(): Boolean {
        fragmentList.forEach {
            if (it.isApplicantDetailsValid().not())
                return false
        }
        return true
    }

    fun getApplicantDetails(): ArrayList<PersonalApplicantsModel> {
        val list = ArrayList<PersonalApplicantsModel>()
        fragmentList.forEach { item ->
            list.add(item.getApplicant())

        }
        return list
    }

}