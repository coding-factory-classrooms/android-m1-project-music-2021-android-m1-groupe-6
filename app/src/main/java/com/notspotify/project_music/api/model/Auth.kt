package com.notspotify.project_music.api.model

import com.squareup.moshi.JsonClass


sealed class Auth{
    @JsonClass(generateAdapter = true)
    data class Request(val username: String = "groupe6",val password:String = "0DyuZL7mI3")

    @JsonClass(generateAdapter = true)
    data class Response(val token: String)
}
