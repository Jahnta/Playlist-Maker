package com.practicum.playlistmaker.ui.media.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.search.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistTrackAdapter(
    private val clickListener: ((Track) -> Unit)?,
    private val longClickListener: ((Track) -> Unit)?,
) :
    RecyclerView.Adapter<PlaylistTrackAdapter.TrackViewHolder>() {

    class TrackViewHolder(private val binding: TrackViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Track) {
            with(binding) {
                trackName.text = model.trackName
                artistName.text = model.artistName
                trackTime.text =
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
                Glide.with(itemView)
                    .load(model.artworkUrl60)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(8))
                    .into(trackImage)
            }
        }
    }

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