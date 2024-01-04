package com.practicum.playlistmaker.ui.media

import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistViewBinding
import com.practicum.playlistmaker.domain.media.model.Playlist

class PlaylistsAdapter(private var onClicked: ((Playlist) -> Unit)? = null) :
    RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHolder>() {
    class PlaylistViewHolder(private val binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.playlistTitle.text = playlist.playlistTitle
            val innerNumber = playlist.playlistTracksCount.toString()
            val text = when {
                innerNumber.toInt() % 10 == 1 && innerNumber.toInt() % 100 != 11 -> "трек"
                innerNumber.toInt() % 10 == 2 && innerNumber.toInt() % 100 != 12 -> "трека"
                innerNumber.toInt() % 10 == 3 && innerNumber.toInt() % 100 != 13 -> "трека"
                innerNumber.toInt() % 10 == 4 && innerNumber.toInt() % 100 != 14 -> "трека"
                else -> "треков"
            }
            binding.playlistTracksCount.text = "$innerNumber $text"
            Glide.with(itemView)
                .load(playlist.playlistCoverPath)
                .placeholder(R.drawable.placeholder)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(8)))
                .into(binding.playlistCover)
        }
    }

    var playlists = ArrayList<Playlist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = PlaylistViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onClicked?.invoke(playlists[position])
        }
    }
}