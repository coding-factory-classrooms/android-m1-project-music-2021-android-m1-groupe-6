package com.notspotify.project_music.ui.main.profile.viewmodel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.notspotify.project_music.R
import com.notspotify.project_music.databinding.SongBinding
import com.notspotify.project_music.model.Song
import kotlinx.android.synthetic.main.profile_fragment.*

class SongsAdapter (val songs: List<Song>)  : RecyclerView.Adapter<SongsAdapter.ViewHolder>() {
    var albumCover = "";

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding : SongBinding = SongBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.song, parent , false)
        return ViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actualSong = songs[position]
        holder.binding.song = actualSong

        Glide.with(holder.itemView)
            .load(albumCover)
            .into(holder.binding.songPicture)
    }

    override fun getItemCount(): Int { return songs.size }
}