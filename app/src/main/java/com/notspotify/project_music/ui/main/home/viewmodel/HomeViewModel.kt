package com.notspotify.project_music.ui.main.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notspotify.project_music.server.service.APIArtist
import com.notspotify.project_music.vo.ArtistJSON
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class HomeViewModelState() {

    data class Succes(val listArtistJSON: List<ArtistJSON>) : HomeViewModelState()
    data class Loading(val message: String) : HomeViewModelState()
    data class Failure(val errorMessage: String) : HomeViewModelState()

}

class HomeViewModel(private val apiArtist: APIArtist) : ViewModel() {

    private val state = MutableLiveData<HomeViewModelState>()

    private var actualPage = 0;

    private val actualPost = MutableLiveData<ArtistJSON>()
    fun actualPost(): LiveData<ArtistJSON> = actualPost
    private var listArtistJSON: ArrayList<ArtistJSON> = ArrayList()

    private val stateListPost = MutableLiveData<HomeViewModelState>()
    fun getState(): LiveData<HomeViewModelState> = state
    fun getStateListPost(): LiveData<HomeViewModelState> = stateListPost




    fun getArtists() {
        stateListPost.value = HomeViewModelState.Loading("Chargement : ")
        val serviceRequest = apiArtist.getArtists()
        serviceRequest.enqueue(object : Callback<List<ArtistJSON>> {
            override fun onFailure(call: Call<List<ArtistJSON>>, t: Throwable) {
                Log.v("TEST", "FAILURE $t")
                stateListPost.value = HomeViewModelState.Failure("Error")
            }

            override fun onResponse(
                call: Call<List<ArtistJSON>>,
                response: Response<List<ArtistJSON>>
            ) {
                response.body()?.also { it ->

                    Log.d("RESPONSE", "POST_LIST $it")

                    stateListPost.value = HomeViewModelState.Succes(it)

                    listArtistJSON.addAll(it)

                    // set actualpost to first post

                    if (listArtistJSON.isNotEmpty()) {
                        actualPost.postValue(listArtistJSON[0])
                    }
                    actualPage += 1
                } ?: run {
                    stateListPost.value = HomeViewModelState.Failure("list null")
                }
            }
        })

    }


    fun changeActualPost(position: Int) {
        actualPost.postValue(listArtistJSON[position])
        if (position > (listArtistJSON.size - 5)) getArtists()
    }

}




