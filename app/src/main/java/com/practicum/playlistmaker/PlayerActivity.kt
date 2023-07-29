package com.practicum.playlistmaker

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
        const val DELAY = 500L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
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

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())


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
        trackDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        trackAlbum.text = track.collectionName
        trackYear.text = track.releaseDate.substring(0, 4)
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country

        preparePlayer()

        playButton.setOnClickListener {
            playbackControl()
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(createUpdateTimerTask())
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play_button)
            handler.removeCallbacks(createUpdateTimerTask())
            playTime.text = "00:00"
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.pause_button)
        handler.post(createUpdateTimerTask())
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play_button)
        handler.removeCallbacks(createUpdateTimerTask())
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    playTime.text = SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayer.currentPosition)
                    handler.postDelayed(this, DELAY)
                }
            }
        }
    }
}