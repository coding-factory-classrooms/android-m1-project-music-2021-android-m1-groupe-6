package com.notspotify.project_music.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.ui.main.player.viewmodel.PlayerViewModel

@Suppress("UNCHECKED_CAST")
class PlayerViewModelFactory(private val apiSong: APISong, private val application: Application) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayerViewModel(apiSong,application) as T
    }
}