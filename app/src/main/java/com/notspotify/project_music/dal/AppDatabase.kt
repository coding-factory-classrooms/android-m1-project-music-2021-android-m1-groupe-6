package com.notspotify.project_music.dal

import androidx.room.Database
import androidx.room.RoomDatabase
import com.notspotify.project_music.dal.dao.AccountDAO
import com.notspotify.project_music.dal.entity.AccountInfo

const val DB_NAME = "lofify.db"
@Database(entities = [AccountInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDAO(): AccountDAO
}