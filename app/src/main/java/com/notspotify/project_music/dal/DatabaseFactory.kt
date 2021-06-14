package com.notspotify.project_music.dal

import android.content.Context
import androidx.room.Room


object DatabaseFactory{
    fun create(context: Context) : AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, DB_NAME
        ).allowMainThreadQueries().build()
    }
}