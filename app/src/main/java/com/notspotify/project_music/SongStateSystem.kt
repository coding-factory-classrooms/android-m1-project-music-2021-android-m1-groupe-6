package com.notspotify.project_music

import com.notspotify.project_music.dal.dao.SongStatDAO
import com.notspotify.project_music.dal.entity.SongStatEntity
import com.notspotify.project_music.model.Song

class SongStateSystem(private val songStatDAO: SongStatDAO) {

    private var song: Song? = null
    private var timeListen: Int = 0

    fun saveStats(){
        song?.let {
            songStatDAO.addCountStat(SongStatEntity(null,it.id,timeListen,1))
        }
    }

    fun setSong(song:Song){
        this.song = song
        timeListen = 0
    }

    fun addTimeListen(){
        timeListen++
    }
}