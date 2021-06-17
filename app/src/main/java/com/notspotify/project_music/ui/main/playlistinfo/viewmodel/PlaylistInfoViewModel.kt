package com.notspotify.project_music.ui.main.playlistinfo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notspotify.project_music.*
import com.notspotify.project_music.dal.dao.PlaylistDAO
import com.notspotify.project_music.dal.dao.SongDAO
import com.notspotify.project_music.model.Artist
import com.notspotify.project_music.model.Song
import com.notspotify.project_music.ui.main.player.viewmodel.PlayerViewModelState
import kotlinx.coroutines.launch
import java.io.File

sealed class DownloadState{
    data class Added(val songsAndDownLoad:List<SongAndDownLoad>): DownloadState()
    data class IsDownloading(val isDownloading:Boolean): DownloadState()
    data class Error(val errorMessage:String): DownloadState()
}

data class SongAndDownLoad(val song:Song, var isDownloaded: Boolean)

class PlaylistInfoViewModel(private val playlistDAO: PlaylistDAO, private val songDAO: SongDAO,private val songStorageSystem: SongStorageSystem,private val musicDownloader: MusicDownloader) : ViewModel() {

    private var listSong : MutableLiveData<List<SongAndDownLoad>> = MutableLiveData()
    private var isDownloading : Boolean = false
    private var downloadingState = MutableLiveData<DownloadState>()
    fun getStateListSong(): LiveData<List<SongAndDownLoad>> = listSong
    fun getDownloadingState(): LiveData<DownloadState> = downloadingState

    fun getSongsFromPlaylist(playlistId: Long){
        listSong.value = songDAO.loadSongsByPlaylist(playlistId)
            .map { songDAO -> Song(songDAO.songId,songDAO.name,songDAO.file,songDAO.duration,songDAO.created_at,songDAO.artist) }
            .map{song -> SongAndDownLoad(song,songStorageSystem.isSongSaved(song))}
    }

    private fun downloadSongs(){
        val songToDownLoad: List<Song>? = listSong.value?.filter{ songAndDownLoad -> !songAndDownLoad.isDownloaded }?.map { songAndDownLoad -> songAndDownLoad.song }

        songToDownLoad?.let {
            if(songToDownLoad.isEmpty()) return

            setIsDownloading(true)
            musicDownloader.startDownload(songToDownLoad,viewModelScope,object : Callback{
                override fun onSongDownloaded(song: Song, byteArray: ByteArray) {
                    Log.d("test","song downloaded : $song")
                    try{
                        val path:String = songStorageSystem.saveSong(song,byteArray)
                    }catch (e:Exception){
                        Log.e("test",e.message.toString())
                        downloadingState.value = DownloadState.Error("Storage full")
                        stopDownload()
                        return
                    }


                    listSong.value?.let {
                        it.find { it.song.id == song.id }?.isDownloaded = true
                        downloadingState.value = DownloadState.Added(it)
                    }
                }

                override fun onAllSongsDownloaded() {
                    setIsDownloading(false)
                }

            })
        }

    }

    private fun stopDownload(){
        musicDownloader.cancel()
        setIsDownloading(false)
    }

    fun toggleDownload(){
        if(isDownloading){
            stopDownload()
        }else{
            downloadSongs()
        }
    }

    private fun setIsDownloading(value : Boolean){
        isDownloading = value
        downloadingState.value = DownloadState.IsDownloading(isDownloading)
    }

}