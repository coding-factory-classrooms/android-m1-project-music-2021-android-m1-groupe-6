package com.notspotify.project_music.ui.login.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.notspotify.project_music.api.model.Auth
import com.notspotify.project_music.api.service.APIAccount
import com.notspotify.project_music.ui.login.viewmodel.interfaces.ILoginViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


sealed class LoginFragmentState {

    data class Success(val message : String): LoginFragmentState()
    data class Loading(val message : String): LoginFragmentState()
    data class Failure(val errorMessage: String) : LoginFragmentState()

}

class LoginViewModel(private val apiAccount: APIAccount, application: Application) : AndroidViewModel(application), ILoginViewModel {

    private var state: MutableLiveData<LoginFragmentState> = MutableLiveData()

    override fun getState(): LiveData<LoginFragmentState> = state

    override fun connection(userName : String, password: String){

        state.postValue(LoginFragmentState.Loading(""))
        apiAccount.getToken(Auth.Request(userName,password)).enqueue(object : Callback<Auth.Response>{
            override fun onFailure(call: Call<Auth.Response>, t: Throwable) {
                state.postValue(LoginFragmentState.Failure("${t.message}"))
                Log.v("test","error : ${t.message}")
            }

            @SuppressLint("CommitPrefEdits")
            override fun onResponse(call: Call<Auth.Response>, response: Response<Auth.Response>) {
                if(response.code() != 200){
                    state.postValue(LoginFragmentState.Failure(response.code().toString()))
                }else{
                    getApplication<Application>().getSharedPreferences("ACCOUNT",Context.MODE_PRIVATE).edit().putString("authToken",response.body()?.token).apply()
                    Log.d("test", "token ${response.body()?.token}");
                    state.postValue(LoginFragmentState.Success(response.body()?.token.toString()))
                }
            }

        })
    }
}

