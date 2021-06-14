package com.notspotify.project_music.server.service

import com.notspotify.project_music.server.model.Auth
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIAccount {
    @POST("/api-token-auth/")
    fun getToken(@Body authRequest: Auth.Request) : Call<Auth.Response>
}