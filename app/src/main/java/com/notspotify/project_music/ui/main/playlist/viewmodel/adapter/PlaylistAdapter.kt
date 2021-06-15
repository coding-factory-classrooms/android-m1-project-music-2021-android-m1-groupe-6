package com.notspotify.project_music.ui.main.playlist.viewmodel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.notspotify.project_music.R
import com.notspotify.project_music.dal.entity.Playlist
import com.notspotify.project_music.databinding.PlaylistBinding

class PlaylistAdapter (private val playlist: List<Playlist>, private val onPlaylistClickListener: OnPlaylistClickListener)  : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding : PlaylistBinding = PlaylistBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.playlist, parent , false)
        return ViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actualPlaylist = playlist[position]
        holder.binding.playlist = actualPlaylist
        holder.binding.playlistContainer.setOnClickListener {
            onPlaylistClickListener.invoke(actualPlaylist)
        }
    }

    override fun getItemCount(): Int { return playlist.size }
}