package com.finance.app.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finance.app.persistence.model.CoApplicantsList
import com.finance.app.persistence.model.CoApplicantsMaster

@Dao
interface CoApplicantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoApplicants(applicants: CoApplicantsMaster)

    @Query("SELECT * FROM CoApplicantsMaster WHERE leadID=:leadID ")
    fun getCoApplicants(leadID: Int): LiveData<CoApplicantsMaster?>

    @Query("DELETE FROM CoApplicantsMaster")
    fun deletePropertyMaster()

}