package com.notspotify.project_music.ui.main.userProfile.viewmodel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.notspotify.project_music.R
import com.notspotify.project_music.databinding.SongboxBinding
import com.notspotify.project_music.model.Artist
import com.notspotify.project_music.model.Song


class SongBoxAdapter(val songs: List<Pair<Song,Artist>>) : RecyclerView.Adapter<SongBoxAdapter.ViewHolder>()  {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding : SongboxBinding = SongboxBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.songbox, parent , false)
        return ViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actualSong : Song = songs[position].first
        val actualArtist : Artist = songs[position].second
        holder.binding.song = actualSong
        holder.binding.artist = actualArtist

        Glide.with(holder.itemView)
            .load(actualArtist.album_cover_url)
            .into(holder.binding.songPicture)
    }

    override fun getItemCount(): Int { return songs.size }
}