package com.notspotify.project_music

import android.util.Log
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.model.Song
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import java.io.ByteArrayOutputStream
import java.io.InputStream

interface Callback{
    fun onSongDownloaded(song:Song, byteArray: ByteArray)
    fun onAllSongsDownloaded()
}
interface OnDownloadFinish{
    fun invoke(byteArray: ByteArray)
}

class MusicDownloader(private val apiSong: APISong) {

    private var actualJob: Job? = null

    fun startDownload(song: Song, coroutineScope: CoroutineScope, callback: OnDownloadFinish){

        actualJob = coroutineScope.launch(Dispatchers.IO) {

            val responseBody=apiSong.downloadFile(song.file).body()
            val songData : ByteArray = downloadMusic(responseBody)

            withContext(Dispatchers.Main){
                callback.invoke(songData)
            }
        }

    }

    fun startDownload(songs: List<Song>, coroutineScope: CoroutineScope, callback: Callback){

        Log.d("test","start downlaod : ${songs.size}")
        actualJob = coroutineScope.launch(Dispatchers.IO) {

            songs.forEach{
                Log.d("test","start download song : ${it.name}")
                val responseBody=apiSong.downloadFile(it.file).body()
                val songData : ByteArray = downloadMusic(responseBody)

                withContext(Dispatchers.Main){
                    callback.onSongDownloaded(it,songData)
                }
            }
            withContext(Dispatchers.Main){
                callback.onAllSongsDownloaded()
            }
        }
    }

    private fun downloadMusic(responseBody: ResponseBody?):ByteArray{
        val fos = ByteArrayOutputStream()

        responseBody?.let {
            val input: InputStream = it.byteStream()
            try {

                fos.use { output ->
                    val buffer = ByteArray(4 * 1024) // or other buffer size
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                    output.flush()
                }

            }catch (e:Exception){
                Log.e("test","Download error : $e")
            }
            finally {
                input?.close()
            }
        }

        return fos.toByteArray()
    }

    fun cancel(){
        actualJob?.cancel()
    }
}