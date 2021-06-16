package com.notspotify.project_music.ui.main.playlistinfo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notspotify.project_music.MusicDownloader
import com.notspotify.project_music.OnAllDownloadFinished
import com.notspotify.project_music.OnDownloadFinish
import com.notspotify.project_music.SongStorageSystem
import com.notspotify.project_music.dal.dao.PlaylistDAO
import com.notspotify.project_music.dal.dao.SongDAO
import com.notspotify.project_music.model.Song
import com.notspotify.project_music.ui.main.player.viewmodel.PlayerViewModelState
import kotlinx.coroutines.launch

sealed class SongState{
    data class Success(val songs:List<Song>): SongState()
}

sealed class DownloadState{
    data class Added(val songs:List<Song>): DownloadState()
    data class IsDownloading(val isDownloading:Boolean): DownloadState()
}

class PlaylistInfoViewModel(private val playlistDAO: PlaylistDAO, private val songDAO: SongDAO,private val songStorageSystem: SongStorageSystem,private val musicDownloader: MusicDownloader) : ViewModel() {

    private var listSong = MutableLiveData<List<Song>>()
    private var downloadingState = MutableLiveData<DownloadState>()
    fun getStateListSong(): LiveData<List<Song>> = listSong
    fun getDownloadingState(): LiveData<DownloadState> = downloadingState

    fun getSongsFromPlaylist(playlistId: Long){
        listSong.value = songDAO.loadSongsByPlaylist(playlistId).map { songDAO -> Song(songDAO.songId,songDAO.name,songDAO.file,songDAO.duration,songDAO.created_at,songDAO.artist) }
    }

    fun downloadSongs(){
        downloadingState.value = DownloadState.IsDownloading(true)

        listSong.value?.forEach {
            musicDownloader.startDownload(it,viewModelScope,object : OnDownloadFinish{
                override fun invoke(byteArray: ByteArray) {
                    val path:String = songStorageSystem.saveSong(it,byteArray)

                    listSong.value?.let {
                        downloadingState.value = DownloadState.Added(it)
                    }

                }
            })
        }

        viewModelScope.launch {
            musicDownloader.checkAllDownloadFinished(object : OnAllDownloadFinished{
                override fun invoke() {
                    downloadingState.value = DownloadState.IsDownloading(false)
                }
            })
        }

    }

    fun stopDownload(){
        musicDownloader.cancelAll()
        downloadingState.value = DownloadState.IsDownloading(false)
    }
}