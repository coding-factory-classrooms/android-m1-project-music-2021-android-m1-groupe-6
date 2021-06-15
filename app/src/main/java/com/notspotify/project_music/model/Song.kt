package com.notspotify.project_music.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Song(
    val id : Long,
    val name : String,
    val file: String,
    val duration: Int,
    val created_at: String,
    val artist : Long
)
