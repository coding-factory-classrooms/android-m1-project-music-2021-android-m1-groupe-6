package com.notspotify.project_music.ui.main.player.viewmodel

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.model.Song
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.Application
import android.content.Context
import com.notspotify.project_music.*
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.dal.dao.PlaylistDAO
import com.notspotify.project_music.dal.dao.SongDAO
import com.notspotify.project_music.dal.dao.SongStatDAO
import com.notspotify.project_music.dal.entity.SongStatEntity
import com.notspotify.project_music.model.Artist
import java.io.File
import java.lang.Runnable

sealed class PlayerViewModelState() {

    data class Success(val listSong: List<Song>) : PlayerViewModelState()
    data class Loading(val message: String) : PlayerViewModelState()
    data class Failure(val errorMessage: String) : PlayerViewModelState()

    data class ChangeSong(val actualSong: Song) : PlayerViewModelState()

}


const val ARTIST_PREF = "artistId"
const val SONG_PREF = "songId"
const val PLAYLIST_PREF = "playlistId"

class PlayerViewModel(private val apiArtist: APIArtist, private val apiSong: APISong, val application: Application, private val songDAO: SongDAO, private val playlistDAO: PlaylistDAO, private val songStatDAO: SongStatDAO, private val songStorageSystem: SongStorageSystem ) : ViewModel() {

    private val mediaPlayer = MediaPlayer()
    private val progressHandler = Handler(Looper.getMainLooper())
    private val progressHandlerRunable: Runnable

    private var listSong = mutableListOf<Song>()
    private var actualSongIndex: Int = 0

    private var playlistName: String? = null

    private val stateListSong = MutableLiveData<PlayerViewModelState>()
    private val isPlaying = MutableLiveData<Boolean>()
    private val songProgression = MutableLiveData<Int>()
    private val actualSongState = MutableLiveData<PlayerViewModelState>()
    private val artist = MutableLiveData<Artist>()
    private val title = MutableLiveData<String>()

    fun getIsPlaying(): LiveData<Boolean> = isPlaying
    fun getSongProgression(): LiveData<Int> = songProgression
    fun getStateListSong(): LiveData<PlayerViewModelState> = stateListSong
    fun getStateActualSong(): LiveData<PlayerViewModelState> = actualSongState
    fun getArtist(): LiveData<Artist> = artist
    fun getTitle(): LiveData<String> = title

    private val musicDownloader: MusicDownloader = MusicDownloader(apiSong)
    private val cacheSong: CacheSong = CacheSong(application)
    private val songStateSystem: SongStateSystem = SongStateSystem(songStatDAO)

    init {

        mediaPlayer.setOnCompletionListener {
            //nextSong()
        }

        progressHandlerRunable = Runnable{
            updateSongProgression()
        }
    }

    // Song list

    fun nextSong() {
        if (actualSongIndex < listSong.size - 1) {
            changeActualSong(actualSongIndex + 1)
        }
    }

    fun prevSong() {
        if (actualSongIndex > 0) {
            changeActualSong(actualSongIndex - 1)
        }
    }

    fun changeActualSong(index: Int) {
        songStateSystem.saveStats()

        actualSongIndex = index
        val song = listSong[index]
        songStateSystem.setSong(song)

        changeArtist(song.artist)

        actualSongState.value = PlayerViewModelState.ChangeSong(song)
        isPlaying.value = false

        try {
            mediaPlayer.reset()
        }catch (e:Exception){

        }


        val songFile : File? = cacheSong.load(song)

        songFile?.also {
            addSongToMediaplayer(it)
        }?: run{
            songStorageSystem.loadSong(song)?.also {
                addSongToMediaplayer(it)
            }?: run {
                musicDownloader.startDownload(song,viewModelScope,object : OnDownloadFinish{
                    override fun invoke(byteArray: ByteArray) {
                        cacheSong.save(song,byteArray,viewModelScope,object : OnSaveFinish{
                            override fun invoke(file: File) {
                                addSongToMediaplayer(file)
                            }
                        })
                    }
                })
            }

        }

        saveLastSongListen()
    }

    // Requests

    fun getArtisSongsBySong(song : Song) {
        stateListSong.value = PlayerViewModelState.Loading("")

        val serviceRequest = apiSong.getSongsByArtistID(song.artist)
        serviceRequest.enqueue(object : Callback<List<Song>> {

            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                stateListSong.value = PlayerViewModelState.Failure("Error")
            }

            override fun onResponse(
                call: Call<List<Song>>,
                response: Response<List<Song>>
            ) {
                response.body()?.also { it ->
                    listSong.addAll(it)

                    if (listSong.size > 0) {
                        actualSongIndex = listSong.indexOfFirst { _song -> _song.id == song.id }
                        changeActualSong(actualSongIndex)
                        stateListSong.value = PlayerViewModelState.Success(listSong)
                        application.getSharedPreferences("LAST_SONG", Context.MODE_PRIVATE).edit().putLong(
                            PLAYLIST_PREF,-1).apply()
                        application.getSharedPreferences("LAST_SONG", Context.MODE_PRIVATE).edit().putLong(
                            ARTIST_PREF,song.artist).apply()
                    } else {
                        stateListSong.value = PlayerViewModelState.Failure("Empty list")
                    }

                } ?: run {
                    stateListSong.value = PlayerViewModelState.Failure("list null")

                }
            }
        })

    }

    fun getSongsPlaylistId(playlistId : Long,songId : Long? = null) {
        listSong.addAll(songDAO.loadSongsByPlaylist(playlistId).map { songDAO -> Song(songDAO.songId,songDAO.name,songDAO.file,songDAO.duration,songDAO.created_at,songDAO.artist) })
        stateListSong.value = PlayerViewModelState.Success(listSong)

         playlistDAO.loadById(playlistId)?.let {
             playlistName = it.name
             title.value = it.name
        }

        application.getSharedPreferences("LAST_SONG", Context.MODE_PRIVATE).edit().putLong(
            PLAYLIST_PREF,playlistId).apply()
        application.getSharedPreferences("LAST_SONG", Context.MODE_PRIVATE).edit().putLong(
            ARTIST_PREF,-1).apply()

        if(listSong.isNotEmpty()){
            val indexOfSong = listSong.indexOfFirst { _song -> _song.id == songId };
            if(indexOfSong == -1){
                changeActualSong(0)
            }else{
                changeActualSong(indexOfSong)
            }
        }

    }

    private fun changeArtist(artistId:Long){
        apiArtist.getArtistsById(artistId).enqueue(object : Callback<Artist>{
            override fun onResponse(call: Call<Artist>, response: Response<Artist>) {
                response.body()?.let {
                    artist.value = it
                    if(playlistName == null){
                        title.value = it.name
                    }
                }
            }

            override fun onFailure(call: Call<Artist>, t: Throwable) {

            }

        })
    }

    // Media Control

    fun togglePlay(){
        if(mediaPlayer.isPlaying){
            pauseSong()
        }else{
            addSongToMediaplayer()
        }
    }

    private fun pauseSong(){
        mediaPlayer.pause()
        isPlaying.value = false
        progressHandler.removeCallbacks(progressHandlerRunable)
    }

    private fun addSongToMediaplayer(){
        mediaPlayer.start()
        if(mediaPlayer.isPlaying){
            progressHandler.post(progressHandlerRunable)
            isPlaying.value = true
        }

    }

    fun destroyPlayer(){
        progressHandler.removeCallbacks(progressHandlerRunable)
        try {
            mediaPlayer.reset()
            mediaPlayer.stop()
            mediaPlayer.release()

        } catch (e: Exception) {
            Log.d("test", "Erreur mediaplayer : $e")
        }
        songStateSystem.saveStats()
        cacheSong.destroy()
    }

    fun changeSongTimeStamp(time:Int){
        mediaPlayer.seekTo(time)
        songProgression.value = time
    }

    fun updateSongProgression(){
        songProgression.value = mediaPlayer.currentPosition
        songStateSystem.addTimeListen()
        progressHandler.postDelayed(progressHandlerRunable,1000)
    }

    // Songs File

    private fun saveLastSongListen(){
        application.getSharedPreferences("LAST_SONG", Context.MODE_PRIVATE).edit().putLong(SONG_PREF,listSong[actualSongIndex].id).apply()
    }

    fun loadLastSongListen(){
        val lastSongId:Long = application.getSharedPreferences("LAST_SONG",Context.MODE_PRIVATE).getLong(
            SONG_PREF,-1)
        val artistId:Long = application.getSharedPreferences("LAST_SONG",Context.MODE_PRIVATE).getLong(
            ARTIST_PREF,-1)
        val playlistId:Long = application.getSharedPreferences("LAST_SONG",Context.MODE_PRIVATE).getLong(PLAYLIST_PREF,-1)

         if(lastSongId == -1L) return

         if(artistId != -1L){
             getArtisSongsBySong(Song(lastSongId,"","",0,"",artistId))
         }else if (playlistId != -1L){
             getSongsPlaylistId(playlistId, lastSongId)
         }
    }

    private fun addSongToMediaplayer(file:File){
        mediaPlayer.setDataSource(file.absolutePath)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            addSongToMediaplayer()
        }

        songProgression.value = mediaPlayer.currentPosition
    }
}