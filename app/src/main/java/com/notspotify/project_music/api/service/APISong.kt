package com.notspotify.project_music.api.service

import com.notspotify.project_music.model.Song
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface APISong {

    @GET("api/songs/")
    fun getSongsByArtistID(@Query(value = "artist__id") artistID: Long): Call<List<Song>>

    @GET("api/songs/{songId}")
    fun getSongByID(@Path(value = "songId") songId: Long): Call<Song>

    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl:String): Response<ResponseBody>
}