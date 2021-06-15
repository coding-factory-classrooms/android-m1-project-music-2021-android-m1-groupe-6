package com.notspotify.project_music.ui.main.playlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notspotify.project_music.dal.dao.PlaylistDAO
import com.notspotify.project_music.dal.dao.SongDAO
import com.notspotify.project_music.dal.entity.Playlist

sealed class PlaylistState{
    data class Success(val playlists:List<Playlist>) : PlaylistState()
    data class Loading(val message:String) : PlaylistState()
}

class PlaylistViewModel(private val playlistDAO: PlaylistDAO, private val songDAO: SongDAO) : ViewModel() {

    private var playlistState: MutableLiveData<PlaylistState> = MutableLiveData()

    fun getPlaylistState(): LiveData<PlaylistState> = playlistState

    fun addPlaylist(name:String){
        playlistDAO.insert(Playlist(null, name))
        loadPlaylist()
    }
    fun loadPlaylist(){
        playlistState.value = PlaylistState.Loading("Loading")
        playlistState.value = PlaylistState.Success(playlistDAO.loadAll())
    }
}