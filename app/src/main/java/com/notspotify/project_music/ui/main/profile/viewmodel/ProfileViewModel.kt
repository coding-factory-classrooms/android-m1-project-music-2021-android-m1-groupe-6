package com.notspotify.project_music.ui.main.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.dal.dao.PlaylistDAO
import com.notspotify.project_music.dal.dao.SongDAO
import com.notspotify.project_music.dal.entity.Playlist
import com.notspotify.project_music.dal.entity.SongEntity
import com.notspotify.project_music.model.Artist
import com.notspotify.project_music.model.Song
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

sealed class PlaylistState {
    data class Success(val playlist : List<Playlist>, val song:Song): PlaylistState()
    data class Loading(val message : String): PlaylistState()
    data class Failure(val errorMessage: String) : PlaylistState()
}

class ProfileViewModel(private val apiSongs: APISong,private val apiArtist: APIArtist, private val playlistDAO: PlaylistDAO, private val songDAO: SongDAO) : ViewModel(), IProfileViewModel {

    private var artistState: MutableLiveData<ArtistProfileState> = MutableLiveData()
    private var songsState: MutableLiveData<SongsProfileState> = MutableLiveData()
    private var playlistState: MutableLiveData<PlaylistState> = MutableLiveData()

    override fun getArtistState(): LiveData<ArtistProfileState> = artistState
    override fun getSongsState(): LiveData<SongsProfileState> = songsState
    fun getPlaylistState(): LiveData<PlaylistState> = playlistState


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

    fun getPlaylist(song:Song){
        playlistState.value = PlaylistState.Success(playlistDAO.loadAll(),song)
    }

    fun addSongToPlaylist(song:Song,playlistId: Long){
        songDAO.insert(SongEntity(null,song.name,playlistId,song.id,song.file,song.duration,song.created_at,song.artist))
    }

}