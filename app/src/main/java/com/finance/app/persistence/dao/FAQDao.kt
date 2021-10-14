package com.finance.app.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finance.app.persistence.model.FAQ
import com.finance.app.persistence.model.LoanProductMaster

@Dao
interface FAQDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFAQ(faq: ArrayList<FAQ>)

    @Query("SELECT * FROM FAQ")
    fun getAllFaq(): LiveData<List<FAQ>?>
    // Delete the FAQ table
    @Query("DELETE FROM FAQ")
    fun deleteFAQ()

}