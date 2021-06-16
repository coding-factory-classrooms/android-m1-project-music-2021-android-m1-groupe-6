package com.notspotify.project_music.dal.dao

import android.util.Log
import androidx.room.*
import com.notspotify.project_music.dal.entity.SongStatEntity

@Dao
interface SongStatDAO {

    @Query("SELECT * FROM SongStatEntity LIMIT 1")
    fun loadFirst(): SongStatEntity?

    @Query("SELECT * FROM SongStatEntity WHERE songId=:songId")
    fun loadBySongId(songId:Long): SongStatEntity?

    @Query("SELECT * FROM SongStatEntity ORDER BY timeCount DESC, playCount DESC LIMIT :limit")
    fun loadTopSong(limit:Int): List<SongStatEntity>

    @Query("SELECT Sum(timeCount) FROM SongStatEntity")
    fun getTotalTimeCount(): Int

    @Query("SELECT Sum(playCount) FROM SongStatEntity")
    fun getTotalPlayCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(songStatEntity: SongStatEntity): Long

    @Transaction
    fun insertOrUpdateFirst(songStatEntity: SongStatEntity) {
        val insertResult = insert(songStatEntity)

        if (insertResult == -1L)
            update(songStatEntity)

    }

    @Transaction
    fun addCountStat(songStatEntity: SongStatEntity) {
        val songInDB = loadBySongId(songStatEntity.songId)
        Log.d("test","song entity : ${songStatEntity} / song db : $songInDB")
        songInDB?.also {
            val songChange = SongStatEntity(songInDB.id,songInDB.songId,songInDB.timeCount + songStatEntity.timeCount,songInDB.playCount + songStatEntity.playCount)
            update(songChange)
        }?: run{
            insert(songStatEntity)
        }

    }

    @Delete
    fun delete(songStatEntity: SongStatEntity)

    @Update
    fun update(songStatEntity: SongStatEntity)
}