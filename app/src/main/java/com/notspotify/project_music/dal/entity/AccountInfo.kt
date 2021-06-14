package com.notspotify.project_music.dal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountInfo(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "id_account") val token: String?
)