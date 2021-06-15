package com.notspotify.project_music.ui.main.profile.viewmodel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.notspotify.project_music.R
import com.notspotify.project_music.databinding.SongBinding
import com.notspotify.project_music.model.Song



class SongsAdapter (val songs: List<Song>,private val onSongClickListener: OnSongClickListener)  : RecyclerView.Adapter<SongsAdapter.ViewHolder>() {
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

        holder.binding.songContainer.setOnClickListener {
            onSongClickListener.invoke(actualSong)
        }

        holder.binding.addToPlaylist.setOnClickListener {
            onSongClickListener.addPlaylist(actualSong)
        }

        Glide.with(holder.itemView)
            .load(albumCover)
            .into(holder.binding.songPicture)
    }

    override fun getItemCount(): Int { return songs.size }
}