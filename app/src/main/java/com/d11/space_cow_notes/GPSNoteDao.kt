package com.d11.space_cow_notes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GPSNoteDao {

    @Query("SELECT * FROM gpsnotes")
    fun getAll(): List<GPSNote>

    @Insert
    fun insertAll(vararg gpsnotes: GPSNote)

    @Delete
    fun delete(gpsnote: GPSNote)

    @Update
    fun update(gpsnote: GPSNote)

    @Query("SELECT * FROM gpsnotes WHERE id IS (:id)")
    fun loadAllByIds(id: Int): List<GPSNote>
}