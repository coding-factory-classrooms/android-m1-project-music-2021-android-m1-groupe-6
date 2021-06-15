package com.notspotify.project_music.ui.main.bibliotheque

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.notspotify.project_music.ui.main.bibliotheque.viewmodel.BibliothequeViewModel
import com.notspotify.project_music.R
import com.notspotify.project_music.api.RetrofitFactory
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.common.makeToast
import com.notspotify.project_music.factory.BibliothequeViewModelFactory
import com.notspotify.project_music.model.Artist
import com.notspotify.project_music.ui.main.bibliotheque.viewmodel.BibliothequetState
import com.notspotify.project_music.ui.main.bibliotheque.viewmodel.adapter.ArtistAdapter
import com.notspotify.project_music.ui.main.bibliotheque.viewmodel.adapter.OnArtistClickListener
import kotlinx.android.synthetic.main.bibliotheque_fragment.*

class Bibliotheque : Fragment() {
    private lateinit var viewModel: BibliothequeViewModel

    private lateinit var adapter: ArtistAdapter
    private var listArtists = mutableListOf<Artist>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bibliotheque_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(viewModelStore, BibliothequeViewModelFactory(
            RetrofitFactory(requireContext())
            .createService(APIArtist::class.java), activity?.application!!
        )
        ).get(BibliothequeViewModel::class.java)

        viewModel.getArtists();

        val onArtistClickListener: OnArtistClickListener = object : OnArtistClickListener {
            override fun invoke(artistId: Long) {
                findNavController().navigate(R.id.action_bibliotheque_to_profile,bundleOf(Pair("artistId", artistId)))
            }

        }

        adapter = ArtistAdapter(listArtists,onArtistClickListener)

        recyclerArtists.adapter = adapter;
        recyclerArtists.layoutManager  = LinearLayoutManager(this.context)

        viewModel.getState().observe(viewLifecycleOwner, Observer { updateUI(it) })


    }

    private fun updateUI(state: BibliothequetState) {
        when(state){
            is BibliothequetState.Failure ->{
                makeToast("Error")
            }
            is BibliothequetState.Loading ->{
                makeToast("Loading")
            }
            is BibliothequetState.Success ->{
                Log.d("test","Add all artists ${state.artistes}")
                listArtists.clear()
                listArtists.addAll(state.artistes)
                adapter.notifyDataSetChanged()
            }
        }
    }

}