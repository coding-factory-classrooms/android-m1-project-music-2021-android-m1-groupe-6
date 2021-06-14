package com.notspotify.project_music.server.service

import com.notspotify.project_music.vo.ArtistJSON
import retrofit2.Call
import retrofit2.http.*

interface APIArtist {
    @GET("api/data")
    fun getArtists(): Call<List<ArtistJSON>>

    @GET("api/artist/{artistID}")
    fun getArtistsById(@Path(value = "artistID") artistID: Long): Call<ArtistJSON>

}