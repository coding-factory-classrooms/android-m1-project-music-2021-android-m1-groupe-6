package com.notspotify.project_music.dal.dao

import androidx.room.*
import com.notspotify.project_music.dal.entity.AccountInfo

@Dao
interface AccountDAO {

    @Query("SELECT * FROM AccountInfo LIMIT 1")
    fun loadFirst(): AccountInfo?


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(accountInfo: AccountInfo): Long

    @Transaction
    fun insertOrUpdateFirst(accountInfo: AccountInfo) {
        val insertResult = insert(accountInfo)

        if (insertResult == -1L)
            update(accountInfo)

    }

    @Delete
    fun delete(accountInfo: AccountInfo)

    @Update
    fun update(accountInfo: AccountInfo)
}