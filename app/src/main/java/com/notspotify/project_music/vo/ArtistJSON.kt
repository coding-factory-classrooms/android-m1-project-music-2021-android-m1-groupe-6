package com.notspotify.project_music.vo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistJSON(
    val id: Long,
    val name: String,
    val genre: String,
    val songs: List<String>,
    val profilePicture: String
)
