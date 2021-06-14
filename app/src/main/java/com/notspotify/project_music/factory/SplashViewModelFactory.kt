package com.notspotify.project_music.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.notspotify.project_music.server.service.APIAccount
import com.notspotify.project_music.ui.main.home.viewmodel.SplashViewModel

@Suppress("UNCHECKED_CAST")
class SplashViewModelFactory(private val application: Application, private val apiAccount: APIAccount) : ViewModelProvider.NewInstanceFactory(){

   override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SplashViewModel(application,apiAccount) as T
    }
}