package com.finance.app.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finance.app.persistence.model.LocationTrackerModel

@Dao
interface LocationTrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(locationTrackerModel: LocationTrackerModel)

    @Query("SELECT * FROM LocationTrackerModel")
    fun get(): List<LocationTrackerModel>

    @Query("DELETE FROM LocationTrackerModel")
    fun truncate()

}