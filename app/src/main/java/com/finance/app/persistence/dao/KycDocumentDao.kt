package com.finance.app.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finance.app.persistence.model.KycDocumentModel

@Dao
interface KycDocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(kycDocumentModel: KycDocumentModel)

   /* @Query("SELECT * FROM KycDocumentModel WHERE Active=:activeStatus")
    //fun get(activeStatus: Int): LiveData<KycDocumentModel?>
    fun get(activeStatus : Int) :List<KycDocumentModel?>*/

    @Query("SELECT * FROM KycDocumentModel")
    fun get(): List<KycDocumentModel?>

    @Query("SELECT * FROM KycDocumentModel WHERE leadID=:leadID LIMIT 1")
    fun getObserever(leadID: Int): LiveData<KycDocumentModel?>

    @Query("SELECT * FROM KycDocumentModel")
    fun getObserver(): LiveData<List<KycDocumentModel?>>

//    @Query("SELECT * FROM KycDocumentModel WHERE isSyncWithServer=:syncStatus")
//    fun getSyncObserver(syncStatus: Boolean): LiveData<List<KycDocumentModel?>>
//
//    @Query("SELECT * FROM KycDocumentModel WHERE isSyncWithServer=:syncStatus")
//    fun getSync(syncStatus: Boolean): List<KycDocumentModel?>

    @Query("DELETE FROM KycDocumentModel WHERE id=:id")
    fun delete(id: Int)

    @Query("DELETE FROM KycDocumentModel")
    fun truncate()
    /**
     * Updating only Sync status
     * By lead id
     */
    /*@Query("UPDATE KycDocumentModel SET Active = :status WHERE leadApplicantNumber =:leadApplicantNumber")
    fun updateKycSyncStatus(status: Int? , leadApplicantNumber: String?)*/

}

