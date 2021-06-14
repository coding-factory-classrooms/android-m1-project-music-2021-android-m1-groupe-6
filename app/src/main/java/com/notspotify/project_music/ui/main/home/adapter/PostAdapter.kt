package com.notspotify.project_music.ui.main.home.adapter


import android.annotation.SuppressLint
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.notspotify.project_music.R
import com.notspotify.project_music.databinding.PostBinding
import com.notspotify.project_music.ui.main.home.listener.OnPostClickListener
import com.notspotify.project_music.vo.ArtistJSON


class PostAdapter (private val artists: List<ArtistJSON>, private val onPostClickListener: OnPostClickListener) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var binding : PostBinding = PostBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.post, parent , false)
        return ViewHolder(layoutInflater)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actualArtist : ArtistJSON = artists[position]
        holder.binding.post = actualArtist

        Glide.with(holder.itemView)
            .load(actualArtist.profilePicture)
            .into(holder.binding.profilePicture)

        holder.binding.artistName.text = actualArtist.name


        holder.binding.genre.text = "Genre: ${actualArtist.genre}"
        holder.binding.songNumber.text = "Nombre de chanson(s) : ${actualArtist.songs.size}"

    }


    override fun getItemCount(): Int { return artists.size }
}