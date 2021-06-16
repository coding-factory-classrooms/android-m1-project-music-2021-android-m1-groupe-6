package com.notspotify.project_music.dal.dao

import androidx.room.*
import com.notspotify.project_music.dal.entity.Playlist

@Dao
interface PlaylistDAO {

    @Query("SELECT * FROM Playlist")
    fun loadAll(): List<Playlist>

    @Query("SELECT * FROM Playlist WHERE id=:id")
    fun loadById(id:Long): Playlist?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playlist: Playlist): Long

    @Transaction
    fun insertOrUpdateFirst(playlist: Playlist) {
        val insertResult = insert(playlist)

        if (insertResult == -1L)
            update(playlist)

    }

    @Delete
    fun delete(playlist: Playlist)

    @Update
    fun update(playlist: Playlist)
}