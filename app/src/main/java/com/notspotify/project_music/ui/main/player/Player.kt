package com.notspotify.project_music.ui.main.player

import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.Observer
import com.notspotify.project_music.ui.main.player.viewmodel.PlayerViewModel
import com.notspotify.project_music.R
import com.notspotify.project_music.api.RetrofitFactory
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.common.makeToast
import com.notspotify.project_music.dal.DatabaseFactory
import com.notspotify.project_music.factory.PlayerViewModelFactory
import com.notspotify.project_music.model.Song
import com.notspotify.project_music.ui.main.player.viewmodel.PlayerViewModelState
import kotlinx.android.synthetic.main.player_fragment.*
import kotlin.math.log

class Player : Fragment() {

    private lateinit var viewModel: PlayerViewModel

    private var listSong = mutableListOf<Song>()

    private lateinit var actualSong: Song

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.player_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            PlayerViewModelFactory(RetrofitFactory(requireContext()).createService(APISong::class.java),activity?.application!!,DatabaseFactory.create(requireContext()).songDAO())
        ).get(PlayerViewModel::class.java)

        viewModel.getStateListSong().observe(viewLifecycleOwner, Observer { updateUI(it) })

        viewModel.getStateActualSong()
            .observe(viewLifecycleOwner, Observer { updateActualSong(it) })


        viewModel.getIsPlaying().observe(viewLifecycleOwner, {

            if(it){
                play.text = "Pause"
            }else{
                play.text = "Play"
            }
        })

        viewModel.getSongProgression().observe(viewLifecycleOwner, {
            seekBar.progress = it
        })


        arguments?.also {

            it.get("songId")?.let {
                val song: Song = it as Song
                viewModel.getArtisSongsBySong(song)
            }

            it.getLong("playlistId")?.let {
                viewModel.getSongsPlaylistId(it)
            }

        } ?: run {
            viewModel.loadLastSongListen()
        }


        play.setOnClickListener {
            viewModel.togglePlay()
        }

        previous.setOnClickListener {
            viewModel.prevSong()
        }
        next.setOnClickListener {
            viewModel.nextSong()
        }



        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.changeSongTimeStamp(progress)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
    }

    override fun onPause() {
        super.onPause()
        viewModel.destroyPlayer()
    }

    private fun updateActualSong(state: PlayerViewModelState?) {
        when (state) {
            is PlayerViewModelState.ChangeSong -> {
                actualSong = state.actualSong
                songName.text = actualSong.name
                seekBar.max = actualSong.duration * 1000
            }
            is PlayerViewModelState.Failure -> {
                makeToast(state.errorMessage)
            }
            is PlayerViewModelState.Loading -> {
                makeToast(state.message)
            }
        }

    }

    private fun updateUI(state: PlayerViewModelState?) {
        when (state) {
            is PlayerViewModelState.Success -> {
                this.listSong.addAll(state.listSong)
            }
            is PlayerViewModelState.Failure -> {
                makeToast(state.errorMessage)
            }
            is PlayerViewModelState.Loading -> {
                makeToast(state.message)
            }
        }
    }

}