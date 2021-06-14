package com.notspotify.project_music.ui.main.bibliotheque

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.notspotify.project_music.ui.main.bibliotheque.viewmodel.BibliothequeViewModel
import com.notspotify.project_music.R

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
        viewModel = ViewModelProvider(this).get(BibliothequeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}