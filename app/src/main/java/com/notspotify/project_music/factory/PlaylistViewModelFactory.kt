package com.notspotify.project_music.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.dal.dao.PlaylistDAO
import com.notspotify.project_music.dal.dao.SongDAO
import com.notspotify.project_music.ui.main.player.viewmodel.PlayerViewModel
import com.notspotify.project_music.ui.main.playlist.viewmodel.PlaylistViewModel
import com.notspotify.project_music.ui.main.profile.viewmodel.ProfileViewModel

@Suppress("UNCHECKED_CAST")
class PlaylistViewModelFactory(private val playlistDAO: PlaylistDAO, private val songDAO: SongDAO) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlaylistViewModel(playlistDAO,songDAO) as T
    }
}