package com.notspotify.project_music.ui.main.userProfile

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
import com.notspotify.project_music.R
import com.notspotify.project_music.StatsState
import com.notspotify.project_music.UserProfileViewModel
import com.notspotify.project_music.api.RetrofitFactory
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.dal.DatabaseFactory
import com.notspotify.project_music.factory.UserProfileViewModelFactory
import com.notspotify.project_music.model.Artist
import com.notspotify.project_music.model.Song
import com.notspotify.project_music.ui.main.profile.viewmodel.adapter.OnSongClickListener
import com.notspotify.project_music.ui.main.profile.viewmodel.adapter.SongsAdapter
import com.notspotify.project_music.ui.main.userProfile.viewmodel.adapter.SongBoxAdapter
import kotlinx.android.synthetic.main.user_profile_fragment.*


class UserProfile : Fragment() {

    private var listSongs = mutableListOf<Pair<Song,Artist>>()
    private lateinit var songsAdapter: SongBoxAdapter

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
            activity?.application!!,
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

        songsAdapter = SongBoxAdapter(listSongs)
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

        maxStorageBtn.setOnClickListener {
            viewModel.changeMaxStorage(maxStorage.text.toString().toInt())
        }

    }

}