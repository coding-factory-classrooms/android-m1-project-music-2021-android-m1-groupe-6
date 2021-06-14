package com.notspotify.project_music.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager

import com.notspotify.project_music.R
import com.notspotify.project_music.enums.VoteType
import com.notspotify.project_music.factory.ProfileViewModelFactory
import com.notspotify.project_music.model.Account
import com.notspotify.project_music.server.RetrofitFactory
import com.notspotify.project_music.server.service.APIArtist
import com.notspotify.project_music.ui.main.home.adapter.PostProfileAdapter
import com.notspotify.project_music.ui.main.home.listener.OnPostClickListener
import com.notspotify.project_music.vo.ArtistJSON
import kotlinx.android.synthetic.main.profile_fragment.*


class ProfileFragment: Fragment() {
    private var listPost = mutableListOf<ArtistJSON>()
    private lateinit var viewModel: ProfileViewModel

    private lateinit var adapter: PostProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val onPostClickListener: OnPostClickListener = object : OnPostClickListener {
            override fun invoke(voteType: VoteType, idPost: Long) {

            }

        }
        adapter = PostProfileAdapter(listPost,onPostClickListener)
        recyclerProfile.adapter = adapter
        recyclerProfile.layoutManager = GridLayoutManager(context, 2)

        viewModel = ViewModelProvider(this, ProfileViewModelFactory(
            RetrofitFactory(requireContext()).createService(
            APIArtist::class.java))
        ).get(ProfileViewModel::class.java)

        viewModel.getStateListArtists().observe(viewLifecycleOwner, Observer { updateUI(it) })


        viewModel.account().observe(viewLifecycleOwner, Observer { updatePostInfo(it) })
    }


    private fun updatePostInfo(account: Account){
        display_name.text = account.name
        name.text = "@${account.name}"
  /*      profile_picture.load(account.profilePictureData.uri["medium"]) {
            transformations(CircleCropTransformation())
        }*/
    }

    private fun updateUI(state: ProfileViewModelState) {
        when (state) {
            is ProfileViewModelState.Succes -> {
                this.listPost.addAll(state.listArtistJSON)
                adapter.notifyDataSetChanged()
            }
            is ProfileViewModelState.Failure -> {
                println("Ca beug ProfileViewModelState")

            }
            is ProfileViewModelState.Loading -> {
                println("Loading")
            }
        }
    }

}
