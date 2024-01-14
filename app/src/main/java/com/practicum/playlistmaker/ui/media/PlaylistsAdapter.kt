package com.practicum.playlistmaker.ui.media

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistViewBinding
import com.practicum.playlistmaker.domain.media.model.Playlist

class PlaylistsAdapter(private var clickListener: ((Playlist) -> Unit)?) :
    RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHolder>() {
    class PlaylistViewHolder(private val binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.playlistTitle.text = playlist.playlistTitle
            binding.playlistTracksCount.text = itemView.context.resources.getQuantityString(
                R.plurals.tracks, playlist.playlistTracksCount, playlist.playlistTracksCount
            )
            Glide.with(itemView)
                .load(playlist.playlistCoverPath)
                .placeholder(R.drawable.placeholder)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(8)))
                .into(binding.playlistCover)
        }
    }

    var playlists = ArrayList<Playlist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(PlaylistViewBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            clickListener?.invoke(playlists[position])
            notifyDataSetChanged()
        }
    }

    fun setItems(items: List<Playlist>) {
        playlists.clear()
        playlists.addAll(items)
        notifyDataSetChanged()
    }
}