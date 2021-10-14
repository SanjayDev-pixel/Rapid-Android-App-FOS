package com.finance.app.view.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.app.databinding.FragmentNavMenuBinding
import com.finance.app.eventBusModel.AppEventsClasses
import com.finance.app.others.AppEnums
import com.finance.app.view.adapters.recycler.adapter.NavMenuAdapter
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class NavMenuFragment : Fragment() {
    private lateinit var binding: FragmentNavMenuBinding
    private lateinit var navMenuAdapter: NavMenuAdapter
    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    private var menuList: List<AppEnums.ScreenLoanApp>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNavMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        ArchitectureApp.instance.component.inject(this)
        menuList = sharedPreferences.getNavMenuItem()
        binding.rcNavMenu.layoutManager = LinearLayoutManager(requireContext())
        navMenuAdapter = NavMenuAdapter(requireContext(), menuList!!)
        binding.rcNavMenu.adapter = navMenuAdapter
    }

    fun toggleMenu() {
        navMenuAdapter.setMenuExpanded()
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun getEventMasterDataReconfigured(syncObject: AppEventsClasses.EnumChangeLoanAppNavFragment) {

        when(syncObject.enumChangeLoanAppNavFragment) {
            AppEnums.EnumEventChangeLoanApplicationFragmentNavigation.NEXT -> navMenuAdapter.nextFragment()
            AppEnums.EnumEventChangeLoanApplicationFragmentNavigation.PREVIOUS -> navMenuAdapter.previousFragment()
        }
    }
}