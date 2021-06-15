package com.notspotify.project_music.api.service

import com.notspotify.project_music.model.Artist
import com.notspotify.project_music.model.Song
import retrofit2.Call
import retrofit2.http.*

interface APISong {

    @GET("api/songs/")
    fun getSongsByArtistID(@Query(value = "artist__id") artistID: Long): Call<List<Song>>
}