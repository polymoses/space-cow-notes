package com.d11.space_cow_notes

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GPSNote::class], version = 1)
abstract class GPSNoteDatabase : RoomDatabase() {
    abstract fun noteDao(): GPSNoteDao
}