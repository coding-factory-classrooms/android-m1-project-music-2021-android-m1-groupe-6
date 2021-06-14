package com.notspotify.project_music.ui.main.bibliotheque

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.notspotify.project_music.ui.main.bibliotheque.viewmodel.BibliothequeViewModel
import com.notspotify.project_music.R
import com.notspotify.project_music.api.RetrofitFactory
import com.notspotify.project_music.api.service.APIAccount
import com.notspotify.project_music.api.service.APIArtist
import com.notspotify.project_music.common.makeToast
import com.notspotify.project_music.factory.BibliothequeViewModelFactory
import com.notspotify.project_music.factory.LoginViewModelFactory
import com.notspotify.project_music.ui.login.viewmodel.LoginFragmentState
import com.notspotify.project_music.ui.login.viewmodel.LoginViewModel
import com.notspotify.project_music.ui.main.bibliotheque.viewmodel.BibliothequetState

class Bibliotheque : Fragment() {

    companion object {
        fun newInstance() = Bibliotheque()
    }

    private lateinit var viewModel: BibliothequeViewModel

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
            }
        }
    }

}