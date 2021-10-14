package motobeans.architecture.development.implementation

import android.content.Context
import com.finance.app.persistence.db.MasterDB
import motobeans.architecture.development.interfaces.DataBaseUtil

/**
 * Created by munishkumarthakur on 04/11/17.
 */

class DaaBaseUtilImpl(private val context: Context) : DataBaseUtil {

    override fun provideDataBaseSource(): MasterDB {
        return MasterDB.getInstance(context)
    }
}