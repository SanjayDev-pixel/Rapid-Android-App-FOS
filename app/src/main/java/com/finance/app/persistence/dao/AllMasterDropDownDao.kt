package com.finance.app.persistence.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finance.app.persistence.model.AllMasterDropDown

@Dao
interface  AllMasterDropDownDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMasterDropDownValue(product: AllMasterDropDown)

    @Query("SELECT * FROM AllMasterDropDown LIMIT 1")
    fun getMasterDropdownValue(): LiveData<AllMasterDropDown?>

    @Query("DELETE FROM AllMasterDropDown")
    fun deleteAllMasterDropdownValue()
    //Searching Value with in  list with the help of id


}