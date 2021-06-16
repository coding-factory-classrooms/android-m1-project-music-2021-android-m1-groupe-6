package com.notspotify.project_music.dal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SongStatEntity(
    @PrimaryKey val id: Long?,
    @ColumnInfo(name = "songId") val songId: Long,
    @ColumnInfo(name = "timeCount") val timeCount: Int,
    @ColumnInfo(name = "playCount") val playCount: Int,
)