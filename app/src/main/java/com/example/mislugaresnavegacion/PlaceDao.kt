package com.example.mislugaresnavegacion

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: Place)

    @Query("SELECT * FROM place_table ORDER BY name ASC")
    fun getAllPlaces(): LiveData<List<Place>>
}