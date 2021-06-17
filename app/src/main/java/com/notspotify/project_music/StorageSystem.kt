package com.notspotify.project_music

import android.app.Application
import android.util.Log
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
        if(!canSave(byteArray)){
            throw Exception("cant save file")
        }
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

    fun isSongSaved(song: Song): Boolean {
        return File(getSongPath(song)).exists()
    }

    private fun getSongPath(song:Song):String{
        return "${filesPath}${song.id}_${song.artist}"
    }

    private fun getSizeDirectory(directory:File):Long{
        return directory.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
    }

    private fun canSave(byteArray: ByteArray): Boolean{
        val directory = File(filesPath)
        val directorySize = getSizeDirectory(directory)
        Log.d("test","directorySize : $directorySize")
        Log.d("test"," byteArray.size : ${byteArray.size}")
        Log.d("test"," maxSize : $maxSize")
        return directorySize + byteArray.size <= maxSize
    }
}