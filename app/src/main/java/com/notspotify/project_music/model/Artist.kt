package com.notspotify.project_music.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Artist(
    val id: Long,
    val name: String,
    val genre_name: String,
    val songs: List<String>?,
    val profilePicture: String?
)
