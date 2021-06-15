package com.notspotify.project_music.ui.main.playlist.viewmodel.adapter

import com.notspotify.project_music.dal.entity.Playlist

interface OnPlaylistClickListener {
    fun invoke(playlist: Playlist)
}