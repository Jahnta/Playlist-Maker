package com.practicum.playlistmaker.ui.search

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.domain.search.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(private val binding: TrackViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Track) {
        with(binding) {
            trackName.text = model.trackName
            artistName.text = model.artistName
            trackTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
            Glide.with(itemView)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(8))
                .into(trackImage)
        }
    }
}