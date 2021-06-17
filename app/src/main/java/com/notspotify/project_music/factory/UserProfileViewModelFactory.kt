package com.notspotify.project_music.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.notspotify.project_music.UserProfileViewModel
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.dal.dao.PlaylistDAO
import com.notspotify.project_music.dal.dao.SongDAO
import com.notspotify.project_music.dal.dao.SongStatDAO
import com.notspotify.project_music.ui.main.profile.viewmodel.ProfileViewModel

@Suppress("UNCHECKED_CAST")
class UserProfileViewModelFactory(private val apiSong: APISong, private val apiArtist: APIArtist, private val application: Application, private val songStatDAO: SongStatDAO) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserProfileViewModel(apiSong,apiArtist,application,songStatDAO) as T
    }
}