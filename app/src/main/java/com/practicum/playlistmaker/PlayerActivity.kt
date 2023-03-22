package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK_DATA = "track_data"
    }

    private lateinit var backButton: ImageView
    private lateinit var coverImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var duration: TextView
    private lateinit var albumName: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var addButton: ImageButton
    private lateinit var playButton: ImageButton
    private lateinit var likeButton: ImageButton

    private lateinit var track: Track


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        backButton = findViewById(R.id.back)
        coverImage = findViewById(R.id.trackCover)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        duration = findViewById(R.id.trackDuration)
        albumName = findViewById(R.id.changeable_album)
        year = findViewById(R.id.changeable_year)
        genre = findViewById(R.id.changeable_genre)
        country = findViewById(R.id.changeable_country)
        addButton = findViewById(R.id.add_button)
        playButton = findViewById(R.id.play_button)
        likeButton = findViewById(R.id.like_button)

        track = Gson().fromJson((intent.getStringExtra(TRACK_DATA)), Track::class.java)

        backButton.setOnClickListener {
            finish()
        }

        val cornerRadius = this.resources.getDimensionPixelSize(R.dimen.corner_radius_8)

        Glide.with(this).load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder).centerCrop()
            .transform(RoundedCorners(cornerRadius)).into(coverImage)
        trackName.text = track.trackName
        artistName.text = track.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        albumName.text = track.collectionName
        year.text = track.releaseDate.substring(0, 4)
        genre.text = track.primaryGenreName
        country.text = track.country

    }
}