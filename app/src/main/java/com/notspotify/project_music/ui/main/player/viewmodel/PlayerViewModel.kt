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
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileOutputStream
import java.io.InputStream
import android.app.Application
import kotlinx.coroutines.*
import okhttp3.internal.wait
import java.io.File
import java.lang.Runnable

sealed class PlayerViewModelState() {

    data class Success(val listSong: List<Song>) : PlayerViewModelState()
    data class Loading(val message: String) : PlayerViewModelState()
    data class Failure(val errorMessage: String) : PlayerViewModelState()

    data class ChangeSong(val actualSong: Song) : PlayerViewModelState()

}

class PlayerViewModel(private val apiSong: APISong, val application: Application) : ViewModel() {

    private val state = MutableLiveData<PlayerViewModelState>()
    private val mediaPlayer = MediaPlayer()
    private val progressHandler = Handler(Looper.getMainLooper())
    private val progressHandlerRunable: Runnable

    private lateinit var downloadCoroutine: Job

    private val filesPath = "${application.filesDir.absolutePath}/"

    private var listSong = mutableListOf<Song>()

    private var actualSongIndex: Int = 0
    private val stateListSong = MutableLiveData<PlayerViewModelState>()
    private val isPlaying = MutableLiveData<Boolean>()
    private val songProgression = MutableLiveData<Int>()

    private val actualSong = MutableLiveData<PlayerViewModelState>()

    fun getState(): LiveData<PlayerViewModelState> = state
    fun getIsPlaying(): LiveData<Boolean> = isPlaying
    fun getSongProgression(): LiveData<Int> = songProgression
    fun getStateListSong(): LiveData<PlayerViewModelState> = stateListSong
    fun getStateActualSong(): LiveData<PlayerViewModelState> = actualSong

    init {

        mediaPlayer.setOnCompletionListener {
            Log.d("test","Auto change")
            //nextSong()
        }

        progressHandlerRunable = Runnable{
            updateSongProgression()
        }
    }

    fun updateSongProgression(){
        songProgression.value = mediaPlayer.currentPosition
        progressHandler.postDelayed(progressHandlerRunable,1000)
    }

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
        actualSongIndex = index
        val song = listSong[index]

        actualSong.value = PlayerViewModelState.ChangeSong(song)
        isPlaying.value = false

        mediaPlayer.reset()

        StopDownload()

        val file = File(getSongPath(song))

        if(file.exists()){
            runSongFilePath(file.absolutePath)
        }else{
            loadSongCoroutine(song)
        }

    }

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
                    } else {
                        stateListSong.value = PlayerViewModelState.Failure("Empty list")
                    }

                } ?: run {
                    stateListSong.value = PlayerViewModelState.Failure("list null")

                }
            }
        })

    }

    fun getSongById(songId : Long) {

        apiSong.getSongByID(songId).enqueue(object : Callback<Song>{

            override fun onResponse(call: Call<Song>, response: Response<Song>) {
                response.body()?.also { it ->
                    listSong.add(it)
                    actualSongIndex = listSong.size - 1
                    changeActualSong(actualSongIndex)

                    stateListSong.value = PlayerViewModelState.Success(listSong)

                } ?: run {
                    stateListSong.value = PlayerViewModelState.Failure("list null")

                }
            }

            override fun onFailure(call: Call<Song>, t: Throwable) {
                stateListSong.value = PlayerViewModelState.Failure(t.message.toString())
            }

        })
    }

    fun getSongsPlaylistId(playlistId : Long) {

    }

    fun togglePlay(){
        if(mediaPlayer.isPlaying){
            pauseSong()
        }else{
            playSong()
        }
    }

    private fun pauseSong(){
        mediaPlayer.pause()
        isPlaying.value = false
        progressHandler.removeCallbacks(progressHandlerRunable)
    }

    private fun playSong(){
        mediaPlayer.start()
        progressHandler.post(progressHandlerRunable)
        isPlaying.value = true
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
    }

    fun changeSongTimeStamp(time:Int){
        mediaPlayer.seekTo(time)
    }

    fun saveFile(body: ResponseBody?, path: String):String{
        if (body==null)
            return ""
        var input: InputStream? = null
        try {
            input = body.byteStream()

            val fos = FileOutputStream(path)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return path
        }catch (e:Exception){
            Log.e("saveFile",e.toString())
        }
        finally {
            input?.close()
        }
        return ""
    }

    private fun getSongPath(song:Song):String{
        return "${filesPath}${song.id}_${song.artist}"
    }

    private fun runSongFilePath(path: String){
        mediaPlayer.setDataSource(path)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playSong()
        }

        songProgression.value = mediaPlayer.currentPosition
    }

    private fun loadSongCoroutine(song:Song){
        downloadCoroutine = viewModelScope.launch(Dispatchers.IO) {
            val responseBody=apiSong.downloadFile(song.file).body()
            val path = saveFile(responseBody,getSongPath(song))
            withContext(Dispatchers.Main){
                runSongFilePath(path)
            }
        }
    }

    private fun StopDownload(){
        if(!this::downloadCoroutine.isInitialized) return

        if(downloadCoroutine.isActive){
            downloadCoroutine.cancel()
        }
    }
}