package com.notspotify.project_music.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notspotify.project_music.model.Account
import com.notspotify.project_music.server.service.APIArtist
import com.notspotify.project_music.vo.ArtistJSON
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


sealed class ProfileViewModelState() {

    data class Succes(val listArtistJSON: List<ArtistJSON>) : ProfileViewModelState()
    data class Loading(val message: String) : ProfileViewModelState()
    data class Failure(val errorMessage: String) : ProfileViewModelState()

}

const val TAG = "ProfileViewModel"

class ProfileViewModel(private val apiArtist: APIArtist) : ViewModel() {

    private val state = MutableLiveData<ProfileViewModelState>()


    private val account = MutableLiveData<Account>()
    fun account(): LiveData<Account> = account
    private var listArtistJSON: ArrayList<ArtistJSON> = ArrayList()

    private val stateListArtists = MutableLiveData<ProfileViewModelState>()
    fun getState(): LiveData<ProfileViewModelState> = state
    fun getStateListArtists(): LiveData<ProfileViewModelState> = stateListArtists


    fun getArtists() {
        Log.d("test","execution de la requete")
        val serviceRequest = apiArtist.getArtists()
        serviceRequest.enqueue(object : Callback<List<ArtistJSON>> {
            override fun onFailure(call: Call<List<ArtistJSON>>, t: Throwable) {
                Log.v("test", "FAILURE $t")
                Log.d("test","erreur execution de la requete $call")
                stateListArtists.value = ProfileViewModelState.Failure("Error")
            }

            override fun onResponse(
                call: Call<List<ArtistJSON>>,
                response: Response<List<ArtistJSON>>
            ) {
                response.body()?.also { it ->
                    stateListArtists.value = ProfileViewModelState.Succes(it)

                    listArtistJSON = it as ArrayList<ArtistJSON>

                    // set actualpost to first post

                    if (listArtistJSON.isNotEmpty()) {
                     Log.d(TAG, "listPostJSON $listArtistJSON")
                    }
                } ?: run {
                    stateListArtists.value = ProfileViewModelState.Failure("list null")
                }
            }
        })

    }
}
