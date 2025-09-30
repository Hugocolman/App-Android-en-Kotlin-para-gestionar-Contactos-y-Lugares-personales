package com.example.mislugaresnavegacion

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Update
    suspend fun update(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("SELECT * FROM contact_table ORDER BY contact_name ASC")
    fun getAllContacts(): LiveData<List<Contact>>

    @Query("SELECT * FROM contact_table WHERE is_favorite = 1 ORDER BY contact_name ASC")
    fun getFavoriteContacts(): LiveData<List<Contact>>
}