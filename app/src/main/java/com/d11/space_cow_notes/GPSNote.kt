package com.d11.space_cow_notes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gpsnotes")
data class GPSNote (

    @ColumnInfo(name="title") var title: String?,
    @ColumnInfo(name="message") var message: String?,
    @ColumnInfo(name="latitude") var latitude: Double?,
    @ColumnInfo(name="longitude") var longitude: Double?,
    @ColumnInfo(name="image") var image: ByteArray?,
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
)