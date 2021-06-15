package com.notspotify.project_music.ui.main.player.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.model.Song
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class PlayerViewModelState() {

    data class Success(val listSong: List<Song>) : PlayerViewModelState()
    data class Loading(val message: String) : PlayerViewModelState()
    data class Failure(val errorMessage: String) : PlayerViewModelState()

    data class ChangeSong(val actualSong: Song) : PlayerViewModelState()

}

class PlayerViewModel(private val apiSong: APISong) : ViewModel() {

    private val state = MutableLiveData<PlayerViewModelState>()

    private var listSong = mutableListOf<Song>()

    private var actualSongIndex: Int = 0
    private val stateListSong = MutableLiveData<PlayerViewModelState>()

    private val actualSong = MutableLiveData<PlayerViewModelState>()

    fun getState(): LiveData<PlayerViewModelState> = state
    fun getStateListSong(): LiveData<PlayerViewModelState> = stateListSong
    fun getStateActualSong(): LiveData<PlayerViewModelState> = actualSong


    fun nextSong() {
        if (actualSongIndex < listSong.size - 1) {
            changeActualSong(++actualSongIndex)
        }
    }

    fun prevSong() {
        if (actualSongIndex > 0) {
            actualSongIndex -= 1
            changeActualSong(actualSongIndex)
        }
    }


    fun changeActualSong(index: Int) {
        actualSong.value = PlayerViewModelState.ChangeSong(listSong[index])
    }


    fun getArtisSongsBySong(song : Song) {
        stateListSong.value = PlayerViewModelState.Loading("")

        val serviceRequest = apiSong.getSongsByArtistID(song.artist)
        serviceRequest.enqueue(object : Callback<List<Song>> {

            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                stateListSong.value = PlayerViewModelState.Failure("Error")
            }

            override fun onResponse(
                call: Call<List<Song>>,
                response: Response<List<Song>>
            ) {
                response.body()?.also { it ->
                    listSong.addAll(it)

                    if (listSong.size > 0) {
                        actualSongIndex = listSong.indexOfFirst { _song -> _song.id == song.id }
                        changeActualSong(actualSongIndex)
                        stateListSong.value = PlayerViewModelState.Success(listSong)
                    } else {
                        stateListSong.value = PlayerViewModelState.Failure("Empty list")
                    }

                } ?: run {
                    stateListSong.value = PlayerViewModelState.Failure("list null")

                }
            }
        })

    }

    fun getSongById(songId : Long) {

        apiSong.getSongByID(songId).enqueue(object : Callback<Song>{

            override fun onResponse(call: Call<Song>, response: Response<Song>) {
                response.body()?.also { it ->
                    listSong.add(it)
                    actualSongIndex = listSong.size - 1
                    changeActualSong(actualSongIndex)

                    stateListSong.value = PlayerViewModelState.Success(listSong)

                } ?: run {
                    stateListSong.value = PlayerViewModelState.Failure("list null")

                }
            }

            override fun onFailure(call: Call<Song>, t: Throwable) {
                stateListSong.value = PlayerViewModelState.Failure(t.message.toString())
            }

        })
    }

    fun getSongsPlaylistId(playlistId : Long) {

    }

}