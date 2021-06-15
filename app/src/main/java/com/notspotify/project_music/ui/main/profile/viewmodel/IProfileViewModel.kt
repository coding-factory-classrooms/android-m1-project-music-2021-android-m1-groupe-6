package com.notspotify.project_music.ui.main.profile.viewmodel

import androidx.lifecycle.LiveData
import com.notspotify.project_music.ui.main.bibliotheque.viewmodel.BibliothequetState

interface IProfileViewModel {
    fun getArtistState() : LiveData<ArtistProfileState>
    fun getSongsState(): LiveData<SongsProfileState>
    fun getSongsFromArtistId(id:Long)
    fun getArtistById(id:Long)
}