package com.notspotify.project_music.dal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "name") val name: String?,
)