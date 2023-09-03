package com.practicum.playlistmaker.ui.player

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.player.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.player.model.PlayerStates
import com.practicum.playlistmaker.domain.search.model.Track
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK_DATA = "track_data"
        fun newIntent(context: Context, track: Track): Intent {
            return Intent(context, PlayerActivity::class.java).apply {
                putExtra(TRACK_DATA, Gson().toJson(track))
            }
        }
    }

    private lateinit var backButton: ImageView
    private lateinit var coverImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var addButton: ImageButton
    private lateinit var playButton: ImageButton
    private lateinit var likeButton: ImageButton
    private lateinit var playTime: TextView

    private lateinit var track: Track
    private lateinit var interactor: MediaPlayerInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        backButton = findViewById(R.id.back)
        coverImage = findViewById(R.id.trackCover)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackDuration = findViewById(R.id.trackDuration)
        trackAlbum = findViewById(R.id.trackAlbum)
        trackYear = findViewById(R.id.trackYear)
        trackGenre = findViewById(R.id.trackGenre)
        trackCountry = findViewById(R.id.trackCountry)
        addButton = findViewById(R.id.add_button)
        playButton = findViewById(R.id.play_button)
        likeButton = findViewById(R.id.like_button)
        playTime = findViewById(R.id.playTime)

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
        trackDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        trackAlbum.text = track.collectionName
        trackYear.text = track.releaseDate?.substring(0, 4) ?: ""
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country

        interactor = Creator.provideMediaPlayerInteractor()

        interactor.preparePlayer(track)

        playButton.setOnClickListener {
            playbackControl()
        }

    }

    override fun onPause() {
        super.onPause()
        interactor.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        interactor.destroyPlayer()
    }


    private fun startPlayer() {
        interactor.startPlayer()
        playButton.setImageResource(R.drawable.pause_button)
    }

    private fun pausePlayer() {
        interactor.pausePlayer()
        playButton.setImageResource(R.drawable.play_button)
    }

    private fun playbackControl() {
        when (interactor.getPlayerState()) {
            PlayerStates.STATE_PLAYING -> {
                pausePlayer()
            }

            PlayerStates.STATE_PREPARED, PlayerStates.STATE_PAUSED -> {
                startPlayer()
            }

            else -> {}
        }
    }

}