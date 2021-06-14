package com.notspotify.project_music.api.service

import com.notspotify.project_music.model.Artist
import retrofit2.Call
import retrofit2.http.*

interface APIArtist {
    @GET("api/data")
    fun getArtists(): Call<List<Artist>>

    @GET("api/artist/{artistID}")
    fun getArtistsById(@Path(value = "artistID") artistID: Long): Call<Artist>

}