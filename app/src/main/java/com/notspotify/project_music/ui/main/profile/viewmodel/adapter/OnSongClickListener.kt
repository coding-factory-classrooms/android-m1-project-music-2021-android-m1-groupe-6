package com.notspotify.project_music.ui.main.profile.viewmodel.adapter

import com.notspotify.project_music.model.Song

interface OnSongClickListener {
    fun invoke(song: Song)
}