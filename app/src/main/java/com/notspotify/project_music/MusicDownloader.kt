package com.notspotify.project_music

import android.util.Log
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.model.Song
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import java.io.ByteArrayOutputStream
import java.io.InputStream

interface OnDownloadFinish{
    fun invoke(byteArray: ByteArray)
}

interface OnAllDownloadFinished{
    fun invoke()
}

class MusicDownloader(private val apiSong: APISong) {

    private val jobs: MutableList<Job> = mutableListOf()

    fun startDownload(song: Song, coroutineScope: CoroutineScope, onDownloadFinish: OnDownloadFinish) : Job{

        val job: Job = coroutineScope.launch(Dispatchers.IO) {
            val responseBody=apiSong.downloadFile(song.file).body()
            val songData : ByteArray = downloadMusic(responseBody)

            withContext(Dispatchers.Main){
                onDownloadFinish.invoke(songData)
            }
        }
        jobs.add(job)
        return job
    }

    private fun removeUnActiveJobs(){
        jobs.forEach {
            if(!it.isActive){
                jobs.remove(it)
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

    fun cancel(job:Job){
        val jobFind:Job? = jobs.find { _job -> _job == job }
        jobFind?.let {
            if(it.isActive){
                it.cancel()
                jobs.remove(it)
            }
        }
    }

    fun cancelAll(){
        jobs.forEach { job ->
            cancel(job)
        }
        removeUnActiveJobs()
    }

    suspend fun checkAllDownloadFinished(onAllDownloadFinished: OnAllDownloadFinished){
        jobs.forEach {
            if(it.isActive){
                it.join()
            }
        }
        onAllDownloadFinished.invoke()
    }
}