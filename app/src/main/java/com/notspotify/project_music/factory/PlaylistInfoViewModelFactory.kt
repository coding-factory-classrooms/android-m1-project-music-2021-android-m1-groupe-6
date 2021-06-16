package com.notspotify.project_music.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.notspotify.project_music.MusicDownloader
import com.notspotify.project_music.SongStorageSystem
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.dal.dao.PlaylistDAO
import com.notspotify.project_music.dal.dao.SongDAO
import com.notspotify.project_music.ui.main.player.viewmodel.PlayerViewModel
import com.notspotify.project_music.ui.main.playlist.viewmodel.PlaylistViewModel
import com.notspotify.project_music.ui.main.playlistinfo.viewmodel.PlaylistInfoViewModel
import com.notspotify.project_music.ui.main.profile.viewmodel.ProfileViewModel

@Suppress("UNCHECKED_CAST")
class PlaylistInfoViewModelFactory(private val playlistDAO: PlaylistDAO, private val songDAO: SongDAO,private val songStorageSystem: SongStorageSystem, private val musicDownloader: MusicDownloader) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlaylistInfoViewModel(playlistDAO,songDAO,songStorageSystem,musicDownloader) as T
    }
}