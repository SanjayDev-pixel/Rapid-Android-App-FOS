package motobeans.architecture.development.implementation

import motobeans.architecture.development.interfaces.AppUtil
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.SharedPreferencesUtil

/**
 * Created by munishkumarthakur on 04/11/17.
 */

class AppUtilImpl(val databaseUtil: DataBaseUtil, val sharedPreferencesUtil: SharedPreferencesUtil) : AppUtil {
  override fun resetApp() {
    resetAppDatabase()
    resetAppSharedPreference()
  }

  override fun reconfigureApp() {
    reconfigureAppDatabase()
  }

  fun reconfigureAppDatabase() {
    databaseUtil.provideDataBaseSource().reconfigDataFromDBASync()
  }


  fun resetAppDatabase() {
    databaseUtil.provideDataBaseSource().deleteAllTableDataFromDBAsycn()
  }

  fun resetAppSharedPreference() {
    sharedPreferencesUtil.clearAll()
  }
}