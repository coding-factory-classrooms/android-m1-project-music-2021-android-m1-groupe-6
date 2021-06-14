package com.notspotify.project_music.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Image(
    val name : String,
    val uri: Map<String,String>
)
