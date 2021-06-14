package com.notspotify.project_music.vo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageJSON(
    val name : String,
    val uri: Map<String,String>
)
