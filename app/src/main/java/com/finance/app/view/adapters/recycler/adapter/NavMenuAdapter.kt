package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemNavBinding
import com.finance.app.others.AppEnums
import com.finance.app.view.adapters.recycler.holder.NavDrawerViewHolder
import com.finance.app.view.fragment.loanApplicationFragments.*
import com.finance.app.view.fragment.loanApplicationFragments.assets_liability.AssetLiabilityFragmentNew
import com.finance.app.view.fragment.loanApplicationFragments.bank.BankDetailFragmentNew
import com.finance.app.view.fragment.loanApplicationFragments.document_checklist.DocumentCheckListFragmentNew
import com.finance.app.view.fragment.loanApplicationFragments.employment.EmploymentInfoFragmentNew
import com.finance.app.view.fragment.loanApplicationFragments.personal.PersonalInfoFragmentNew
import java.lang.Exception

interface NavMenuConnector {
    fun isMenuExpanded(): Boolean
    fun getSelectionScreenName(): String
    fun rootViewClicked(position: Int, navItem: AppEnums.ScreenLoanApp)
    fun rootViewClicked(position: Int)
    fun nextFragment()
    fun previousFragment()
}

class NavMenuAdapter(private val mContext: Context, private val navListItem: List<AppEnums.ScreenLoanApp>) : RecyclerView.Adapter<NavDrawerViewHolder>(), NavMenuConnector {

    private lateinit var binding: ItemNavBinding
    private var isExpanded = false

    private var selectedNavPosition = 0
    private var selectedNavString = ""

    init {
        when (navListItem.isNotEmpty()) {
            true -> selectedNavString = navListItem[0].screenName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavDrawerViewHolder {
        val layoutInflater = LayoutInflater.from(mContext)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_nav, parent, false)
        return NavDrawerViewHolder(binding, mContext, this)
    }

    override fun getItemCount() = navListItem.size

    override fun onBindViewHolder(holder: NavDrawerViewHolder, position: Int) {
        val navItem = navListItem[position]
        holder.bindItems(position = position, navItem = navItem)
    }

    override fun nextFragment() {
        val position = (selectedNavPosition + 1)
        val nextPosition = when(navListItem.size > position) {
            true -> position
            false -> navListItem.size - 1
        }
        rootViewClicked(nextPosition)
    }

    override fun previousFragment() {
        val position = (selectedNavPosition - 1)
        val previousPosition = when((position) >= 0) {
            true -> position
            false -> 0
        }
        rootViewClicked(previousPosition)
    }

    override fun rootViewClicked(position: Int) {
        try {
            val navItem = navListItem[position]
            rootViewClicked(position = position, navItem = navItem)
        } catch (e: Exception) {

        }
    }

    override fun rootViewClicked(position: Int, navItem: AppEnums.ScreenLoanApp) {
        selectedNavPosition = position
        selectedNavString = navItem.screenName
        navigateToAnotherFragmentOnIconCLick(navItem)
        if(isMenuExpanded()) {
            toggleMenu()
        }
        notifyDataSetChanged()
    }

    private fun navigateToAnotherFragmentOnIconCLick(navData: AppEnums.ScreenLoanApp) {
        val fragment = when (navData) {
            AppEnums.ScreenLoanApp.LOAN_INFORMATION -> LoanInfoFragmentNew.newInstance()
            AppEnums.ScreenLoanApp.PERSONAL -> PersonalInfoFragmentNew.newInstance()
            AppEnums.ScreenLoanApp.EMPLOYMENT -> EmploymentInfoFragmentNew.newInstance()
            AppEnums.ScreenLoanApp.BANK_DETAIL -> BankDetailFragmentNew.newInstance()
            AppEnums.ScreenLoanApp.LIABILITY_AND_ASSET -> AssetLiabilityFragmentNew.newInstance()
            AppEnums.ScreenLoanApp.REFERENCE -> ReferenceFragmentNew.newInstance()
            AppEnums.ScreenLoanApp.PROPERTY -> PropertyFragmentNew.newInstance()
            AppEnums.ScreenLoanApp.DOCUMENT_CHECKLIST -> DocumentCheckListFragmentNew.newInstance()
            else -> return
        }

        updateSecondaryFragment(fragment = fragment)
    }

    private fun updateSecondaryFragment(fragment: Fragment) {
        val ft = (mContext as AppCompatActivity).supportFragmentManager.beginTransaction().apply {
            replace(R.id.secondaryFragmentContainer, fragment)
            addToBackStack(null)
        }
        ft.commit()
    }

    override fun getSelectionScreenName(): String {
        return selectedNavString
    }

    override fun isMenuExpanded(): Boolean {
        return isExpanded
    }

    fun setMenuExpanded() {
        toggleMenu()
    }

    private fun toggleMenu() {
        isExpanded = !isExpanded
        notifyDataSetChanged()
    }
}
