package com.notspotify.project_music.ui.main.player

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.notspotify.project_music.ui.main.player.viewmodel.PlayerViewModel
import com.notspotify.project_music.R
import com.notspotify.project_music.SongStorageFactory
import com.notspotify.project_music.api.RetrofitFactory
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.common.makeToast
import com.notspotify.project_music.dal.DatabaseFactory
import com.notspotify.project_music.factory.PlayerViewModelFactory
import com.notspotify.project_music.model.Song
import com.notspotify.project_music.ui.main.player.viewmodel.PlayerViewModelState
import kotlinx.android.synthetic.main.player_fragment.*
import java.util.concurrent.TimeUnit

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
            PlayerViewModelFactory(
                RetrofitFactory(requireContext()).createService(APIArtist::class.java),
                RetrofitFactory(requireContext()).createService(APISong::class.java),
                activity?.application!!,
                DatabaseFactory.create(requireContext()).songDAO(),
                DatabaseFactory.create(requireContext()).playlistDAO(),
                DatabaseFactory.create(requireContext()).songStateDAO(),
                SongStorageFactory(requireContext(),activity?.application!!).create())
        ).get(PlayerViewModel::class.java)

        viewModel.getStateListSong().observe(viewLifecycleOwner, Observer { updateUI(it) })

        viewModel.getStateActualSong()
            .observe(viewLifecycleOwner, Observer { updateActualSong(it) })


        viewModel.getIsPlaying().observe(viewLifecycleOwner, {
            if(it){
                playIcon.setBackgroundResource(R.drawable.pauseicon)
            }else{
                playIcon.setBackgroundResource(R.drawable.playicon)
            }
        })

        viewModel.getSongProgression().observe(viewLifecycleOwner, {
            seekBar.progress = it
            val durationMillisLong: Long = it.toLong()

            actualSeek.text = String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(durationMillisLong),
                TimeUnit.MILLISECONDS.toSeconds(durationMillisLong) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMillisLong))
            )
        })

        viewModel.getTitle().observe(viewLifecycleOwner,{ playlistTitle.text = it})
        viewModel.getArtist().observe(viewLifecycleOwner,{ Glide.with(requireContext())
            .load(it.album_cover_url)
            .centerCrop()
            .into(playerAlbumCover)
        })


        arguments?.also {

            var song: Song? = null
            var playlistId: Long? = null

            it.get("songId")?.let {
                song = it as Song
            }

            it.getString("playlistId")?.let {
                playlistId = it.toLong()
            }

            playlistId?.let {
                viewModel.getSongsPlaylistId(it,song?.id)
            }?: run{
                song?.let {  viewModel.getArtisSongsBySong(it) }
            }

        } ?: run {
            viewModel.loadLastSongListen()
        }


        playBtn.setOnClickListener {
            viewModel.togglePlay()
        }

        prevBtn.setOnClickListener {
            viewModel.prevSong()
        }
        nextBtn.setOnClickListener {
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
                val durationMillis = actualSong.duration * 1000
                seekBar.max = durationMillis
                val durationMillisLong: Long = durationMillis.toLong()

                maxSeek.text = String.format("%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(durationMillisLong),
                    TimeUnit.MILLISECONDS.toSeconds(durationMillisLong) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMillisLong))
                )
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