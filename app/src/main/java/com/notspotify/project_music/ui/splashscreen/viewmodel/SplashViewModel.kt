package com.notspotify.project_music.ui.splashscreen.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.notspotify.project_music.api.service.APIAccount

class SplashViewModel(application: Application, private val apiAccount: APIAccount) : AndroidViewModel(application) {
    private var _isConnected : MutableLiveData<Boolean> = MutableLiveData()
    fun getConnected(): LiveData<Boolean> = _isConnected

    fun isConnected(){
        val token : String? = getApplication<Application>().getSharedPreferences("ACCOUNT",Context.MODE_PRIVATE).getString("authToken",null)
        _isConnected.postValue(token != null)
    }
}