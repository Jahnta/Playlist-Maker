package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TrackAdapter(
    private val tracks: ArrayList<Track>, private val clickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    class TrackViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val trackNameView: TextView
        private val artistNameView: TextView
        private val trackTimeView: TextView
        private val trackImage: ImageView

        init {
            trackNameView = itemView.findViewById(R.id.trackName)
            artistNameView = itemView.findViewById(R.id.artistName)
            trackTimeView = itemView.findViewById(R.id.trackTime)
            trackImage = itemView.findViewById(R.id.trackImage)
        }

        fun bind(model: Track) {
            trackNameView.text = model.trackName
            artistNameView.text = model.artistName
            trackTimeView.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
            Glide.with(itemView).load(model.artworkUrl100).placeholder(R.drawable.placeholder)
                .centerCrop().transform(RoundedCorners(8)).into(trackImage)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            clickListener(tracks[position])
        }
    }

}