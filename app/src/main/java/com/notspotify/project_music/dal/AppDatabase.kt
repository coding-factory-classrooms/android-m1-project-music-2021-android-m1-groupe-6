package com.notspotify.project_music.dal

import androidx.room.Database
import androidx.room.RoomDatabase
import com.notspotify.project_music.dal.dao.PlaylistDAO
import com.notspotify.project_music.dal.dao.SongDAO
import com.notspotify.project_music.dal.entity.Playlist
import com.notspotify.project_music.dal.entity.SongEntity

const val DB_NAME = "lofify.db"
@Database(entities = [Playlist::class, SongEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDAO(): PlaylistDAO
    abstract fun songDAO(): SongDAO
}