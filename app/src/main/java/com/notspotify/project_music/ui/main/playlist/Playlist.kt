package com.notspotify.project_music.ui.main.playlist

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
import com.notspotify.project_music.ui.main.playlist.viewmodel.PlaylistViewModel
import com.notspotify.project_music.R
import com.notspotify.project_music.common.makeToast
import com.notspotify.project_music.dal.DatabaseFactory
import com.notspotify.project_music.dal.entity.Playlist
import com.notspotify.project_music.factory.PlaylistViewModelFactory
import com.notspotify.project_music.ui.main.playlist.viewmodel.PlaylistState
import com.notspotify.project_music.ui.main.playlist.viewmodel.adapter.OnPlaylistClickListener
import com.notspotify.project_music.ui.main.playlist.viewmodel.adapter.PlaylistAdapter
import kotlinx.android.synthetic.main.playlist_fragment.*

class Playlist : Fragment() {
    private lateinit var viewModel: PlaylistViewModel

    private var listPlaylist = mutableListOf<Playlist>()
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.playlist_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            PlaylistViewModelFactory(DatabaseFactory.create(requireContext()).playlistDAO(),DatabaseFactory.create(requireContext()).songDAO())
        ).get(PlaylistViewModel::class.java)

        val onPlaylistClickListener: OnPlaylistClickListener = object : OnPlaylistClickListener {
            override fun invoke(playlist: Playlist) {
                findNavController().navigate(R.id.action_playlist_to_playlistInfoFragment,
                    bundleOf(Pair("playlistId", playlist.id))
                )
            }

        }

        playlistAdapter = PlaylistAdapter(listPlaylist,onPlaylistClickListener)
        playlistRecycler.adapter = playlistAdapter
        playlistRecycler.layoutManager  = LinearLayoutManager(this.context)

        addBtn.setOnClickListener {
            viewModel.addPlaylist(playlistName.text.trim().toString())
        }

        viewModel.getPlaylistState().observe(viewLifecycleOwner,{updateUI(it)})

        viewModel.loadPlaylist()
    }

    private fun updateUI(state:PlaylistState){
        when(state){
            is PlaylistState.Loading -> {
                makeToast(state.message)
            }
            is PlaylistState.Success -> {
                listPlaylist.clear()
                listPlaylist.addAll(state.playlists)
                playlistAdapter.notifyDataSetChanged()
            }
        }
    }

}