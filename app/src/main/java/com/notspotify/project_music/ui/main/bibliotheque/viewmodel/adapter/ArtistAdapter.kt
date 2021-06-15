package com.notspotify.project_music.ui.main.bibliotheque.viewmodel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.notspotify.project_music.R
import com.notspotify.project_music.databinding.ArtistBinding
import com.notspotify.project_music.model.Artist

class ArtistAdapter (val artists: List<Artist>, private val onArtistClickListener: OnArtistClickListener)  : RecyclerView.Adapter<ArtistAdapter.ViewHolder>()  {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding : ArtistBinding = ArtistBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.artist, parent , false)
        return ViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actualArtist = artists[position]
        holder.binding.artist = actualArtist
        holder.binding.artistContainer.setOnClickListener {
            onArtistClickListener.invoke(actualArtist.id)
        }

        Glide.with(holder.itemView)
            .load(actualArtist.album_cover_url)
            .circleCrop()
            .into(holder.binding.profilePicture)

    }

    override fun getItemCount(): Int { return artists.size }
}