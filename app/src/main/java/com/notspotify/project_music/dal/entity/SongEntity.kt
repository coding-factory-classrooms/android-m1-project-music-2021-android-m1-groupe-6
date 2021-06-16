package com.notspotify.project_music.dal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SongEntity(
    @PrimaryKey val id: Long?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "playlist") val playlist: Long,
    @ColumnInfo(name = "songId") val songId: Long,
    @ColumnInfo(name = "file") val file: String,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "created_at") val created_at: String,
    @ColumnInfo(name = "artist") val artist : Long,
)