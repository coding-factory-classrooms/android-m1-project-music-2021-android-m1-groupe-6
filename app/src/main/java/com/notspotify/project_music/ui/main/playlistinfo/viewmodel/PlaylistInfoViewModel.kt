package com.notspotify.project_music.ui.main.playlistinfo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notspotify.project_music.dal.dao.PlaylistDAO
import com.notspotify.project_music.dal.dao.SongDAO
import com.notspotify.project_music.model.Song
import com.notspotify.project_music.ui.main.player.viewmodel.PlayerViewModelState

sealed class SongState{
    data class Success(val songs:List<Song>): SongState()
}
class PlaylistInfoViewModel(private val playlistDAO: PlaylistDAO, private val songDAO: SongDAO) : ViewModel() {

    private var listSong = MutableLiveData<List<Song>>()
    fun getStateListSong(): LiveData<List<Song>> = listSong

    fun getSongsFromPlaylist(playlistId: Long){
        listSong.value = songDAO.loadSongsByPlaylist(playlistId).map { songDAO -> Song(songDAO.songId,songDAO.name,songDAO.file,songDAO.duration,songDAO.created_at,songDAO.artist) }
    }
}