package com.finance.app.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finance.app.persistence.model.AllLeadMaster
import com.finance.app.persistence.model.StatesMaster

@Dao
interface StatesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStates(product: ArrayList<StatesMaster>)

    @Query("SELECT * FROM StatesMaster")
    fun getAllStates(): LiveData<List<StatesMaster>?>

    @Query("SELECT * FROM StatesMaster WHERE stateID=:stateId LIMIT 1")
    fun getState(stateId: Int): LiveData<StatesMaster?>

    @Query("DELETE FROM StatesMaster")
    fun deleteAllStates()
}