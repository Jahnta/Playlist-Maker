package com.practicum.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.search.model.Track

class TrackAdapter(
    private var clickListener: ((Track) -> Unit)?,
    private var longClickListener: ((Track) -> Unit)?,
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            clickListener?.invoke(tracks[position])
            notifyDataSetChanged()
        }
        holder.itemView.setOnLongClickListener {
            longClickListener?.invoke(tracks[position])
            notifyDataSetChanged()
            return@setOnLongClickListener true
        }
    }
    fun setItems(items: List<Track>) {
        tracks.clear()
        tracks.addAll(items)
        notifyDataSetChanged()
    }

}