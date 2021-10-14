package com.finance.app.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finance.app.persistence.model.TempModel

/**
 * Created by munishkumarthakur on 09/12/17.
 */
@Dao
interface TempDao {

    @Query("SELECT * FROM TempModel")
    fun getAllVisualTimeTrack(): LiveData<List<TempModel>?>

    @Query("SELECT * FROM TempModel  WHERE localId = :id LIMIT 1")
    fun getVisualTimeTrackById(id: String): LiveData<TempModel?>

    @Query("SELECT * FROM TempModel WHERE val3 = :isSync")
    fun getAllVisualTimeSync(isSync: Boolean): LiveData<List<TempModel>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVisualTimeTrack(item: TempModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVisualTimeTracks(items: List<TempModel>)

    @Query("Select COUNT(localId) from TempModel")
    fun getVisualTimeTrackCount(): LiveData<Int>

    @Query("DELETE FROM TempModel")
    fun deleteAllVisualTimeTrack()
}