package com.notspotify.project_music.ui.main.playlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.dal.dao.PlaylistDAO
import com.notspotify.project_music.dal.dao.SongDAO
import com.notspotify.project_music.dal.entity.Playlist
import com.notspotify.project_music.dal.entity.SongEntity
import com.notspotify.project_music.model.Song

sealed class PlaylistState{
    data class Success(val playlistsInfo:List<PlaylistInfo>) : PlaylistState()
    data class Loading(val message:String) : PlaylistState()
}

data class PlaylistInfo(val playlist:Playlist, val songs: List<SongEntity>)

class PlaylistViewModel(private val playlistDAO: PlaylistDAO, private val songDAO: SongDAO) : ViewModel() {

    private var playlistState: MutableLiveData<PlaylistState> = MutableLiveData()

    fun getPlaylistState(): LiveData<PlaylistState> = playlistState

    fun addPlaylist(name:String){
        playlistDAO.insert(Playlist(null, name))
        loadPlaylist()
    }
    fun loadPlaylist(){
        playlistState.value = PlaylistState.Loading("Loading")
        val playlists = playlistDAO.loadAll()
        val playlistInfo = mutableListOf<PlaylistInfo>()

        playlists.forEach {
            playlistInfo.add(PlaylistInfo(it,songDAO.loadSongsByPlaylist(it.id!!)))
        }

        playlistState.value = PlaylistState.Success(playlistInfo)
    }
}