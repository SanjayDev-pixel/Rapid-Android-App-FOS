package com.finance.app.persistence.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.finance.app.persistence.converters.ConverterArrayList
import com.finance.app.persistence.converters.Converters
import com.finance.app.persistence.dao.*
import com.finance.app.persistence.model.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

@Database(
        entities = [AllMasterDropDown::class , LoanProductMaster::class , AllLeadMaster::class , StatesMaster::class ,
            CoApplicantsMaster::class , KycDocumentModel::class , AllDocumentCheckListMaster::class , LocationTrackerModel::class,FAQ::class] , version = 2
)

@TypeConverters(value = [Converters::class , ConverterArrayList::class])
abstract class MasterDB : RoomDatabase() {

    companion object {
        /** The only instance */
        @Volatile
        private var INSTANCE: MasterDB? = null

        /**
         * Gets the singleton instance of MasterDB.
         *
         * @param context The context.
         * @return The singleton instance of MasterDB.
         */
        fun getInstance(context: Context): MasterDB =
                INSTANCE ?: synchronized(this) {
                    INSTANCE
                            ?: buildDatabase(
                                    context
                            ).also { INSTANCE = it }
                }

        /**
         * Switches the internal implementation with an empty in-memory database.
         *
         * @param context The context.
         */
        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(
                        context.applicationContext ,
                        MasterDB::class.java , "MasterOPT.db"
                )
                        .build()
    }

    @SuppressWarnings("WeakerAccess")
    abstract fun allMasterDropDownDao(): AllMasterDropDownDao

    @SuppressWarnings("WeakerAccess")
    abstract fun loanProductDao(): LoanProductDao

    @SuppressWarnings("WeakerAccess")
    abstract fun faqDao(): FAQDao

    @SuppressWarnings("WeakerAccess")
    abstract fun allLeadsDao(): AllLeadsDao

    @SuppressWarnings("WeakerAccess")
    abstract fun statesDao(): StatesDao

    @SuppressWarnings("WeakerAccess")
    abstract fun coApplicantsDao(): CoApplicantDao

    @SuppressWarnings("WeakerAccess")
    abstract fun allDocumentDao(): DocumentCheckListDao

    @SuppressWarnings("WeakerAccess")
    abstract fun kycDocumentDao(): KycDocumentDao

    @SuppressWarnings("WeakerAccess")
    abstract fun locationTrackerDao(): LocationTrackerDao

    fun reconfigDataFromDBASync() {

    }

    fun deleteAllTableDataFromDBAsycn() = GlobalScope.async {
        resetDatabase()
    }

    private fun resetDatabase() {
        allMasterDropDownDao().deleteAllMasterDropdownValue()
        loanProductDao().deleteLoanProduct()
    }
}