package com.finance.app.view.adapters.pager

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.finance.app.others.APPLICANT
import com.finance.app.others.CO_APPLICANT
import com.finance.app.persistence.model.AssetLiabilityModel
import com.finance.app.persistence.model.PersonalApplicantsModel
import com.finance.app.view.fragment.loanApplicationFragments.assets_liability.AssetLiabilityFragmentForm

class AssetLiabilityPagerAdapter internal constructor(fm: FragmentManager, val applicantsList: ArrayList<PersonalApplicantsModel>) : FragmentStatePagerAdapter(fm) {

    private val fragmentList = ArrayList<AssetLiabilityFragmentForm>()

    override fun getPageTitle(position: Int): CharSequence? = if (applicantsList[position].isMainApplicant) APPLICANT else "$CO_APPLICANT $position"

    override fun getCount() = applicantsList.size

    override fun getItem(position: Int): AssetLiabilityFragmentForm {
        if (fragmentList.isNotEmpty() && fragmentList.size > position)
            return fragmentList[position]

        val fragmentItem = AssetLiabilityFragmentForm.newInstance(applicantsList[position])
        fragmentList.add(fragmentItem)
        return fragmentItem
    }


    fun getALlAssetsAndLiability(): ArrayList<AssetLiabilityModel> {
        val list = ArrayList<AssetLiabilityModel>()
        fragmentList.forEach { it ->
            list.add(it.getAssetsAndLiability())
        }

        return list
    }

    fun getAllFragments() = fragmentList
}