package com.finance.app.eventBusModel

import com.finance.app.others.AppEnums
import com.optcrm.optreporting.app.workers.UtilWorkersTaskLeadSync
import org.greenrobot.eventbus.EventBus

class AppEvents {

    enum class BackGroundSyncEvent {
        LEAD_SYNC
    }

    companion object {
        fun fireEventLoanAppChangeNavFragmentNext() {
            val enumChangeLoanAppNavFragment = AppEnums.EnumEventChangeLoanApplicationFragmentNavigation.NEXT
            val event = AppEventsClasses.EnumChangeLoanAppNavFragment(enumChangeLoanAppNavFragment = enumChangeLoanAppNavFragment)
            EventBus.getDefault().post(event)
        }

        fun fireEventLoanAppChangeNavFragmentPrevious() {
            val enumChangeLoanAppNavFragment = AppEnums.EnumEventChangeLoanApplicationFragmentNavigation.PREVIOUS
            val event = AppEventsClasses.EnumChangeLoanAppNavFragment(enumChangeLoanAppNavFragment = enumChangeLoanAppNavFragment)
            EventBus.getDefault().post(event)
        }

        fun fireEventBackgroundSync(syncEnum: BackGroundSyncEvent) {
            EventBus.getDefault().post(syncEnum)
        }
    }

    fun initiateBackgroundSync(syncEnum: BackGroundSyncEvent) {
        when (syncEnum) {
            BackGroundSyncEvent.LEAD_SYNC -> UtilWorkersTaskLeadSync().execute()
        }
    }
}