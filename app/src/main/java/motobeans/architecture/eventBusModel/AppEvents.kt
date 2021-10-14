package motobeans.architecture.eventBusModel

import com.optcrm.optreporting.app.workers.UtilWorkersTaskTemp
import motobeans.architecture.eventBusModel.AppEvents.BackGroundSyncEvent.TEMP_EVENT
import org.greenrobot.eventbus.EventBus

/**
 * Created by munishkumarthakur on 18/11/17.
 */
class AppEvents {

  enum class BackGroundSyncEvent {
    TEMP_EVENT, END_DAY, DCR, ORDER, VISUAL, VISUAL_GALLERY, LOCAL_IMAGE_ADDED, RCPA, DAY_PLANNER
  }

  fun fireEventBackgroundSync(syncEnum: BackGroundSyncEvent) {
    EventBus.getDefault().post(syncEnum)
  }

  fun inititateBackgroundSync(syncEnum: BackGroundSyncEvent) {
    when (syncEnum) {
      TEMP_EVENT -> UtilWorkersTaskTemp().execute()
    }
  }
}