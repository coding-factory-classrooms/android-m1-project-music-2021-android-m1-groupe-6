package com.notspotify.project_music

import android.app.Application
import com.notspotify.project_music.model.Song
import java.io.File
import java.io.FileOutputStream

interface ISongStorageSystem{
    fun saveSong(song: Song, byteArray:ByteArray): String
    fun loadSong(song: Song): File?
}

class SongStorageSystem(private val maxSize: Int, val application: Application) : ISongStorageSystem {

    private val filesPath = "${application.filesDir.absolutePath}/"

    override fun saveSong(song: Song, byteArray: ByteArray): String {
        val path = getSongPath(song)
        FileOutputStream(path).write(byteArray)
        return path
    }

    override fun loadSong(song: Song): File? {
        val file = File(getSongPath(song))

        if(file.exists()){
            return file
        }
        return null
    }

    private fun getSongPath(song:Song):String{
        return "${filesPath}${song.id}_${song.artist}"
    }

    private fun getSizeDirectory(directory:File):Int{
        return 0
    }
}