package com.notspotify.project_music.ui.main.profile.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.model.Artist
import com.notspotify.project_music.model.Song
import com.notspotify.project_music.ui.main.bibliotheque.viewmodel.BibliothequetState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class ArtistProfileState {
    data class Success(val artist : Artist): ArtistProfileState()
    data class Loading(val message : String): ArtistProfileState()
    data class Failure(val errorMessage: String) : ArtistProfileState()
}

sealed class SongsProfileState {
    data class Success(val songs : List<Song>): SongsProfileState()
    data class Loading(val message : String): SongsProfileState()
    data class Failure(val errorMessage: String) : SongsProfileState()
}

class ProfileViewModel(private val apiSongs: APISong,private val apiArtist: APIArtist) : ViewModel(), IProfileViewModel {

    private var artistState: MutableLiveData<ArtistProfileState> = MutableLiveData()
    private var songsState: MutableLiveData<SongsProfileState> = MutableLiveData()

    override fun getArtistState(): LiveData<ArtistProfileState> = artistState
    override fun getSongsState(): LiveData<SongsProfileState> = songsState


    override fun getSongsFromArtistId(id: Long) {

        apiSongs.getSongsByArtistID(id).enqueue(object : Callback<List<Song>>{
            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                response.body()?.let { songsState.postValue(SongsProfileState.Success(it)) }
                    ?: run {  songsState.postValue(SongsProfileState.Failure("Error")) }
            }

            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                songsState.postValue(SongsProfileState.Failure("Error"))
            }

        })
    }

    override fun getArtistById(id: Long) {
        apiArtist.getArtistsById(id).enqueue(object : Callback<Artist>{
            override fun onResponse(call: Call<Artist>, response: Response<Artist>) {
                response.body()?.let { artistState.postValue(ArtistProfileState.Success(it)) }
                    ?: run {  artistState.postValue(ArtistProfileState.Failure(response.message())) }
            }

            override fun onFailure(call: Call<Artist>, t: Throwable) {
                artistState.postValue(ArtistProfileState.Failure(t.message.toString()))
            }

        })
    }

}