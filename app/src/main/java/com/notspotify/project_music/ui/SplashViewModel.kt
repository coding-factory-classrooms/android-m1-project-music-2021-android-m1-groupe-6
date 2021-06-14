package com.notspotify.project_music.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.notspotify.project_music.server.model.Auth
import com.notspotify.project_music.server.service.APIAccount
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashViewModel(application: Application, private val apiAccount: APIAccount) : AndroidViewModel(application) {
    private var _isConnected : MutableLiveData<Boolean> = MutableLiveData()
    fun getConnected(): LiveData<Boolean> = _isConnected

    fun isConnected(){
        val token : String? = getApplication<Application>().getSharedPreferences("ACCOUNT",Context.MODE_PRIVATE).getString("authToken",null)
        if(token == null){
            Log.v("test","token $token")
             apiAccount.getToken(Auth.Request(username = "groupe4", password = "a2Tq2D7lg1")).enqueue(object  : Callback<Auth.Response>{
                 override fun onFailure(call: Call<Auth.Response>, t: Throwable) {
                     Log.v("test","NOT CONNECTED")
                     _isConnected.postValue(false)
                 }

                 override fun onResponse(call: Call<Auth.Response>,response: Response<Auth.Response>) {
                     Log.v("test","code ${response.code()} value : ${response.body()?.token}")
                     _isConnected.postValue(false)
                 }
             })
         }else{
            Log.v("test","token is not null : $token")
             _isConnected.postValue(true)
         }


    }
}