package com.practicum.playlistmaker.ui.player

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistViewLinearBinding
import com.practicum.playlistmaker.domain.media.model.Playlist

class PlaylistBottomSheetViewHolder(
    private val binding: PlaylistViewLinearBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(playlist: Playlist) {
        binding.tvTitle.text = playlist.playlistTitle
        val innerNumber = playlist.playlistTracksCount.toString()
        val text = when {
            innerNumber.toInt() % 10 == 1 && innerNumber.toInt() % 100 != 11 -> " трек"
            innerNumber.toInt() % 10 == 2 && innerNumber.toInt() % 100 != 12 -> " трека"
            innerNumber.toInt() % 10 == 3 && innerNumber.toInt() % 100 != 13 -> " трека"
            innerNumber.toInt() % 10 == 4 && innerNumber.toInt() % 100 != 14 -> " трека"
            else -> " треков"
        }
        val number = "$innerNumber $text"
        binding.tvAmount.text = number


        Glide.with(itemView)
            .load(playlist.playlistCoverPath)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_8)))
            .into(binding.ivCover)
    }
}