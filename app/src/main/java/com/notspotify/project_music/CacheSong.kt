package com.notspotify.project_music

import android.app.Application
import android.util.Log
import com.notspotify.project_music.model.Song
import kotlinx.coroutines.*
import java.io.File
import java.lang.Exception

interface OnSaveFinish{
    fun invoke(file:File)
}
class CacheSong(val application: Application) {

    val songsDirectory: File = File(application.cacheDir, "songs")
    var job: Job? = null

    init {
        if(!songsDirectory.isDirectory){
            songsDirectory.mkdir()
        }
    }

    fun save(song:Song,byteArray: ByteArray, coroutineScope: CoroutineScope, onSaveFinish: OnSaveFinish ){
        job = coroutineScope.launch {
            coroutineScope.launch(Dispatchers.IO) {

                val file = File(getFileName(song))
                try {
                    file.writeBytes(byteArray)
                }catch (e:Exception){
                    Log.d("test","error : $e")
                }


                withContext(Dispatchers.Main){
                    if(file.exists() && file.readBytes().isNotEmpty()){
                        onSaveFinish.invoke(file)
                    }

                }
            }
        }
    }

    fun load(song: Song): File?{
        val file = File(getFileName(song))
        if(file.exists()){
            return file
        }
        return null
    }

    fun cancel(){
       job?.cancel()
    }


    fun destroy(){
        cancel()
        deleteTempFiles(songsDirectory)
    }

    private fun deleteTempFiles(file: File): Boolean {
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (f in files) {
                    try{
                        f.delete()
                    }catch (e:Exception){
                        Log.d("test","error dekete : $e")
                    }
                }
            }
        }
        return file.delete()
    }

    private fun getFileName(song:Song): String{
        return "${songsDirectory}/${song.id}_${song.artist}"
    }
}