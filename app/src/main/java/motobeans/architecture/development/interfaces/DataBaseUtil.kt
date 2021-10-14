package motobeans.architecture.development.interfaces

import com.finance.app.persistence.db.MasterDB

/**
 * Created by munishkumarthakur on 04/11/17.
 */

interface DataBaseUtil {
    fun provideDataBaseSource(): MasterDB
}