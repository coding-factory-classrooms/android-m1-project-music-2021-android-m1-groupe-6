package com.notspotify.project_music.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.notspotify.project_music.server.service.APIArtist
import com.notspotify.project_music.ui.main.ProfileViewModel

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(private val apiArtist: APIArtist) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(apiArtist) as T
    }
}