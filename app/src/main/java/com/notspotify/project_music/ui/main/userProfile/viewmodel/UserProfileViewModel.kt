package com.notspotify.project_music

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.dal.dao.PlaylistDAO
import com.notspotify.project_music.dal.dao.SongStatDAO
import com.notspotify.project_music.model.Artist
import com.notspotify.project_music.model.Song
import com.notspotify.project_music.ui.main.player.viewmodel.PLAYLIST_PREF
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class StatsState{
    data class Success(val totalTimeCount: Int, val totalPlayCount:Int): StatsState()
}

class UserProfileViewModel(private val apiSongs: APISong, private val apiArtist: APIArtist, private val application: Application, private val songStatDAO: SongStatDAO) : ViewModel() {

    private val songs = mutableListOf<Pair<Song, Artist>>()
    private var songsState: MutableLiveData<List<Pair<Song, Artist>>> = MutableLiveData()
    private var statsState: MutableLiveData<StatsState> = MutableLiveData()
    fun getSongsState(): LiveData<List<Pair<Song, Artist>>> = songsState
    fun getStatsState(): LiveData<StatsState> = statsState

    fun loadStats(){
        getTopSongs(10)
        statsState.value = StatsState.Success(songStatDAO.getTotalTimeCount(),songStatDAO.getTotalPlayCount())
    }

    private fun getTopSongs(limit:Int){
        val songsInDB = songStatDAO.loadTopSong(limit)
        val nbRequest =  songsInDB.size
        Log.d("test","songs in DB : ${songsInDB}")

        songsInDB.forEach {
            apiSongs.getSongByID(it.songId).enqueue(object : Callback<Song>{
                override fun onResponse(call: Call<Song>, response: Response<Song>) {
                    response.body()?.let { song ->
                        apiArtist.getArtistsById(song.artist).enqueue(object: Callback<Artist>{
                            override fun onResponse(
                                call: Call<Artist>,
                                response: Response<Artist>
                            ) {
                                response.body()?.let { artist ->
                                    synchronizeList(nbRequest,song,artist)?.let { listSong ->
                                        songsState.value = listSong.sortedBy { songsInDB.map{ song-> song.songId}.indexOf(it.first.id) }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<Artist>, t: Throwable) {

                            }

                        })

                    }
                }

                override fun onFailure(call: Call<Song>, t: Throwable) {

                }

            })
        }
    }

    fun synchronizeList(max:Int, song:Song, artist:Artist) : List<Pair<Song, Artist>>?{
        if(songs.size != max){
            songs.add(Pair(song,artist))
            if(songs.size == max){
                return songs
            }
        }
        return null
    }

    fun changeMaxStorage(value:Int){
        application.getSharedPreferences("STORAGE", Context.MODE_PRIVATE).edit().putInt(
            "maxSize",value * 1000000).apply()
    }

}