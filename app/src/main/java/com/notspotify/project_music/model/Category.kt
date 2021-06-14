package com.notspotify.project_music.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Category(
    var name: String = "",
    var description: String = "",
    var image: String = ""
)