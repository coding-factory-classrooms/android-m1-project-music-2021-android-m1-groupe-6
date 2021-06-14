package com.notspotify.project_music.ui.main.playlist

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.notspotify.project_music.ui.main.playlist.viewmodel.PlaylistViewModel
import com.notspotify.project_music.R

class Playlist : Fragment() {

    companion object {
        fun newInstance() = Playlist()
    }

    private lateinit var viewModel: PlaylistViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.playlist_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlaylistViewModel::class.java)
        // TODO: Use the ViewModel
    }

}