package com.notspotify.project_music.ui.main.playlistinfo

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
import com.notspotify.project_music.MusicDownloader
import com.notspotify.project_music.R
import com.notspotify.project_music.SongStorageSystem
import com.notspotify.project_music.api.RetrofitFactory
import com.notspotify.project_music.api.service.APISong
import com.notspotify.project_music.dal.DatabaseFactory
import com.notspotify.project_music.factory.PlaylistInfoViewModelFactory
import com.notspotify.project_music.factory.PlaylistViewModelFactory
import com.notspotify.project_music.model.Artist
import com.notspotify.project_music.model.Song
import com.notspotify.project_music.ui.main.playlist.viewmodel.PlaylistViewModel
import com.notspotify.project_music.ui.main.playlistinfo.viewmodel.DownloadState
import com.notspotify.project_music.ui.main.playlistinfo.viewmodel.PlaylistInfoViewModel
import com.notspotify.project_music.ui.main.profile.viewmodel.adapter.OnSongClickListener
import com.notspotify.project_music.ui.main.profile.viewmodel.adapter.SongsAdapter
import kotlinx.android.synthetic.main.playlist_info_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.btnReturn
import kotlinx.android.synthetic.main.profile_fragment.recyclerSongs

class PlaylistInfoFragment : Fragment() {

    private lateinit var viewModel: PlaylistInfoViewModel
    private var listSongs = mutableListOf<Song>()
    private lateinit var songsAdapter: SongsAdapter
    private var playlistId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.playlist_info_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            PlaylistInfoViewModelFactory(
                DatabaseFactory.create(requireContext()).playlistDAO(),
                DatabaseFactory.create(requireContext()).songDAO(),
                SongStorageSystem(0,activity?.application!!),
                MusicDownloader(RetrofitFactory(requireContext()).createService(APISong::class.java))
            )
        ).get(PlaylistInfoViewModel::class.java)


        val onSongClickListener: OnSongClickListener = object : OnSongClickListener {
            override fun invoke(song: Song) {
                val bundle: Bundle= Bundle()
                bundle.putParcelable("songId",song)
                bundle.putString("playlistId",playlistId.toString())
                findNavController().navigate(R.id.action_playlistInfoFragment_to_player,bundle)
            }

            override fun addPlaylist(song: Song) {

            }

        }

        songsAdapter = SongsAdapter(listSongs,onSongClickListener)

        recyclerSongs.adapter = songsAdapter;
        recyclerSongs.layoutManager  = LinearLayoutManager(this.context)

        viewModel.getStateListSong().observe(viewLifecycleOwner,{updateSongList(it)})
        viewModel.getDownloadingState().observe(viewLifecycleOwner,{
            when(it){
                is DownloadState.Added -> {
                    Log.d("test","ADDED")
                }
                is DownloadState.IsDownloading -> {
                    Log.d("test","is downloading : ${it.isDownloading}")
                }
            }
        })

        arguments?.also {

            it.getLong("playlistId").let {
                viewModel.getSongsFromPlaylist(it)
                playlistId = it
            }
        }

        btnReturn.setOnClickListener{
            findNavController().popBackStack()
        }

        downloadBtn.setOnClickListener {
            viewModel.downloadSongs()
        }

    }
    private fun updateSongList(songs:List<Song>){
        listSongs.clear()
        listSongs.addAll(songs)
        songsAdapter.notifyDataSetChanged()
    }

}