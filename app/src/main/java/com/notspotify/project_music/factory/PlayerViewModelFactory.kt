package com.notspotify.project_music.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.notspotify.project_music.SongStorageSystem
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.dal.dao.PlaylistDAO
import com.notspotify.project_music.dal.dao.SongDAO
import com.notspotify.project_music.dal.dao.SongStatDAO
import com.notspotify.project_music.ui.main.player.viewmodel.PlayerViewModel

@Suppress("UNCHECKED_CAST")
class PlayerViewModelFactory(
    private val apiArtist: APIArtist,
    private val apiSong: APISong,
    private val application: Application,
    private val songDAO: SongDAO,
    private val playlistDAO: PlaylistDAO,
    private val songStatDAO: SongStatDAO,
    private val songStorageSystem: SongStorageSystem,

    ) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayerViewModel(apiArtist,apiSong,application,songDAO,playlistDAO,songStatDAO,songStorageSystem) as T
    }
}