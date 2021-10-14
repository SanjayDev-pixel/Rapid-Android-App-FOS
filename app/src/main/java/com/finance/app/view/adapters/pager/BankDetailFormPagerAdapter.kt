package com.finance.app.view.adapters.pager

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.finance.app.others.APPLICANT
import com.finance.app.others.CO_APPLICANT
import com.finance.app.persistence.model.BankDetailModel
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.view.fragment.loanApplicationFragments.bank.BankDetailFormFragment

class BankDetailFormPagerAdapter(fm: FragmentManager, private val applicantsList: ArrayList<PersonalApplicantsModel>) : FragmentStatePagerAdapter(fm) {

    private val fragmentList = ArrayList<BankDetailFormFragment>()

    override fun getCount() = applicantsList.size

    override fun getPageTitle(position: Int) = if (applicantsList[position].isMainApplicant) APPLICANT else "$CO_APPLICANT $position"

    override fun getItem(position: Int): BankDetailFormFragment {
        if (fragmentList.isNotEmpty() && fragmentList.size > position)
            return fragmentList[position]

        val fragment = BankDetailFormFragment.newInstance(applicantsList[position])
        fragmentList.add(fragment)
        return fragment //        return BankDetailFormFragment.newInstance(applicantsList[position])
    }


    fun isBankDetailListAvailable(): Boolean {
        fragmentList.forEachIndexed { index, _ ->
            if (getItem(index).isBankDetailRequiredForApplicant()) {
                if (getItem(index).isBankDetailAvailable().not()) {
                    return false
                }
            }
        }

        return true // Need to Improvise..
    }

    fun getBankDetailsList(): ArrayList<BankDetailModel> {
        val list = ArrayList<BankDetailModel>()
        fragmentList.forEachIndexed { index, _ ->
            list.add(getItem(index).getApplicantBankDetails())
        }
        return list
    }
}