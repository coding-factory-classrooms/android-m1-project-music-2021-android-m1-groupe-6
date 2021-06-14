package com.notspotify.project_music.ui.sign.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.notspotify.project_music.server.service.APIArtist
import com.notspotify.project_music.vo.ArtistJSON
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PostViewModel(private val apiArtist: APIArtist) : ViewModel() {

    fun getArtists(){
        val serviceRequest = apiArtist.getArtists()
        serviceRequest.enqueue(object : Callback<List<ArtistJSON>> {
            override fun onFailure(call: Call<List<ArtistJSON>>, t: Throwable) {
                Log.v("TEST","FAILURE $t")
                //stateListPost.value = HomeViewModelState.Failure("Error")
            }
            override fun onResponse(call: Call<List<ArtistJSON>>, response: Response<List<ArtistJSON>>) {
                Log.v("TEST","SUCCES $response")
                if(response.body() != null){
                    //stateListPost.value = HomeViewModelState.Succes(response.body()!!)
                }else{
                    //stateListPost.value = HomeViewModelState.Failure("list null")
                }

            }
        })

    }

}