package com.notspotify.project_music

import android.app.Application
import android.content.Context

class SongStorageFactory(private val context: Context, private val application: Application) {
    fun create(): SongStorageSystem{

        val maxSize:Int = context.getSharedPreferences("STORAGE",Context.MODE_PRIVATE).getInt("maxSize",50000000)
        return SongStorageSystem(maxSize,application)
    }
}