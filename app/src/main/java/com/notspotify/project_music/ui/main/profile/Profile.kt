package com.notspotify.project_music.ui.main.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.notspotify.project_music.ui.main.profile.viewmodel.ProfileViewModel
import com.notspotify.project_music.R
import com.notspotify.project_music.api.RetrofitFactory
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.dal.DatabaseFactory
import com.notspotify.project_music.dal.entity.Playlist
import com.notspotify.project_music.factory.ProfileViewModelFactory
import com.notspotify.project_music.model.Artist
import com.notspotify.project_music.model.Song
import com.notspotify.project_music.ui.main.profile.viewmodel.ArtistProfileState
import com.notspotify.project_music.ui.main.profile.viewmodel.PlaylistState
import com.notspotify.project_music.ui.main.profile.viewmodel.SongsProfileState
import com.notspotify.project_music.ui.main.profile.viewmodel.adapter.OnSongClickListener
import com.notspotify.project_music.ui.main.profile.viewmodel.adapter.SongsAdapter
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.song.*
import java.util.ArrayList

class Profile : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private var listSongs = mutableListOf<Song>()
    private lateinit var artist: Artist
    private lateinit var songsAdapter: SongsAdapter
    private var idArtist: Long = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments.let {
            Log.d("test", "onCreateView")
            it?.getLong("artistId")?.let {
                idArtist = it
            }
        }
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(viewModelStore, ProfileViewModelFactory(
            RetrofitFactory(requireContext())
                .createService(APISong::class.java), RetrofitFactory(requireContext())
                .createService(APIArtist::class.java),DatabaseFactory.create(requireContext()).playlistDAO(),DatabaseFactory.create(requireContext()).songDAO())
        ).get(ProfileViewModel::class.java)

        val onSongClickListener: OnSongClickListener = object : OnSongClickListener {
            override fun invoke(song: Song) {
                findNavController().navigate(R.id.action_profile_to_player,bundleOf(Pair("songId", song)))
            }

            override fun addPlaylist(song: Song) {
                viewModel.getPlaylist(song)
            }

        }

        songsAdapter = SongsAdapter(listSongs,onSongClickListener)

        recyclerSongs.adapter = songsAdapter;
        recyclerSongs.layoutManager  = LinearLayoutManager(this.context)

        viewModel.getArtistById(idArtist)
        viewModel.getSongsFromArtistId(idArtist)

        viewModel.getArtistState().observe(viewLifecycleOwner, {updateArtistUI(it)})
        viewModel.getSongsState().observe(viewLifecycleOwner, {updateSongsUI(it)})
        viewModel.getPlaylistState().observe(viewLifecycleOwner, {onPlaylistStateChange(it)})

        btnReturn.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun onPlaylistStateChange(state:PlaylistState){
        when(state){
            is PlaylistState.Failure -> {

            }
            is PlaylistState.Loading -> {

            }
            is PlaylistState.Success -> {
                openPlaylistDialog(state.playlist,state.song)
            }
        }
    }

    private fun updateArtistUI(state:ArtistProfileState){
        when(state){
            is ArtistProfileState.Failure -> {
                Log.d("test","Artist UI fail : ${state.errorMessage}")
            }
            is ArtistProfileState.Loading -> {

            }
            is ArtistProfileState.Success -> {
                Log.d("test","Artist UI : ${state.artist}")
                artist = state.artist

                // Pour avoir les album cover sur les song
                songsAdapter.albumCover = artist.album_cover_url.toString()
                songsAdapter.notifyDataSetChanged()

                this.context?.let {
                    Glide.with(it)
                    .load(artist.album_cover_url)
                    .into(albumCover)
                }
            }
        }
    }

    private fun updateSongsUI(state:SongsProfileState){
        when(state){
            is SongsProfileState.Failure -> {

            }
            is SongsProfileState.Loading -> {

            }
            is SongsProfileState.Success -> {
                Log.d("test","Songs UI : ${state.songs}")
                listSongs.clear()
                listSongs.addAll(state.songs)
                songsAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun openPlaylistDialog(playlists:List<Playlist>,song:Song){
        val playlistNames = playlists.map { playlist -> playlist.name }.toTypedArray()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Playlists")
            .setItems(playlistNames) { dialog, which ->
                Log.d("test","which : ${playlists[which]}")
                playlists[which].id?.let {
                    viewModel.addSongToPlaylist(song,it)
                }
            }
            .show()
    }

}