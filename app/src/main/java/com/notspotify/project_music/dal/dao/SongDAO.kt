package com.notspotify.project_music.dal.dao

import androidx.room.*
import com.notspotify.project_music.dal.entity.SongEntity

@Dao
interface SongDAO {

    @Query("SELECT * FROM SongEntity LIMIT 1")
    fun loadFirst(): SongEntity?

    @Query("SELECT * FROM SongEntity WHERE playlist=:playlistId")
    fun loadSongsByPlaylist(playlistId: Long): List<SongEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(songEntity: SongEntity): Long

    @Transaction
    fun insertOrUpdateFirst(songEntity: SongEntity) {
        val insertResult = insert(songEntity)

        if (insertResult == -1L)
            update(songEntity)

    }

    @Delete
    fun delete(songEntity: SongEntity)

    @Update
    fun update(songEntity: SongEntity)
}