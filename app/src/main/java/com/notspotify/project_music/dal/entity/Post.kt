package com.notspotify.project_music.dal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Post(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "id_account") val id_account: Int?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "left_image") val left_image: String?,
    @ColumnInfo(name = "right_image") val right_image: String?,
    @ColumnInfo(name = "categorie") val categorie: String?
)