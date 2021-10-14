package com.finance.app.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finance.app.persistence.model.AllDocumentCheckListMaster
@Dao
interface DocumentCheckListDao {
    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun insertDocumentList(documentList : ArrayList<AllDocumentCheckListMaster>)
   //Querty for fetching
    @Query("SELECT * FROM AllDocumentCheckListMaster WHERE productID= :productId LIMIT 1")
    fun getDocumentCheckList(productId: Int?): LiveData<AllDocumentCheckListMaster?>
}