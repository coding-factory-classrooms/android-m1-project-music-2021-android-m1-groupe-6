package com.notspotify.project_music

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.notspotify.project_music.api.RetrofitFactory
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.dal.DatabaseFactory
import com.notspotify.project_music.factory.UserProfileViewModelFactory
import com.notspotify.project_music.model.Song
import com.notspotify.project_music.ui.main.profile.viewmodel.adapter.OnSongClickListener
import com.notspotify.project_music.ui.main.profile.viewmodel.adapter.SongsAdapter
import kotlinx.android.synthetic.main.user_profile_fragment.*


class UserProfile : Fragment() {

    private var listSongs = mutableListOf<Song>()
    private lateinit var songsAdapter: SongsAdapter

    private lateinit var viewModel: UserProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(viewModelStore, UserProfileViewModelFactory(
            RetrofitFactory(requireContext())
                .createService(APISong::class.java), RetrofitFactory(requireContext())
                .createService(APIArtist::class.java),
            DatabaseFactory.create(requireContext()).playlistDAO(),
            DatabaseFactory.create(requireContext()).songStateDAO())
        ).get(UserProfileViewModel::class.java)

        val onSongClickListener: OnSongClickListener = object : OnSongClickListener {
            override fun invoke(song: Song) {
                findNavController().navigate(
                    R.id.action_profile_to_player,
                    bundleOf(Pair("songId", song))
                )
            }

            override fun addPlaylist(song: Song) {

            }
        }

        songsAdapter = SongsAdapter(listSongs,onSongClickListener)
        topSongsRecycler.adapter = songsAdapter
        topSongsRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        viewModel.loadStats()

        viewModel.getSongsState().observe(viewLifecycleOwner,{
            Log.d("test","list : $it")
            listSongs.clear()
            listSongs.addAll(it)
            songsAdapter.notifyDataSetChanged()
        })

        viewModel.getStatsState().observe(viewLifecycleOwner,{
            when(it){
                is StatsState.Success -> {
                    timeListen.text = it.totalTimeCount.toString()
                    nbSongListen.text = it.totalPlayCount.toString()
                }
            }
        })

    }

}