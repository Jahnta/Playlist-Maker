package com.practicum.playlistmaker.ui.player.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val TRACK_DATA = "track_data"
        fun newIntent(context: Context, track: Track): Intent {
            return Intent(context, PlayerActivity::class.java).apply {
                putExtra(TRACK_DATA, Gson().toJson(track))
            }
        }
    }

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track

    private val viewModel: PlayerViewModel by viewModel { parametersOf(track) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = Gson().fromJson((intent.getStringExtra(TRACK_DATA)), Track::class.java)

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .transform(
                RoundedCorners(
                    resources.getDimensionPixelSize(R.dimen.corner_radius_8)
                )
            )
            .into(binding.trackCover)
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        binding.trackAlbum.text = track.collectionName
        binding.trackYear.text = track.releaseDate?.substring(0, 4)
        binding.trackGenre.text = track.primaryGenreName
        binding.trackCountry.text = track.country

        viewModel.playerInfo.observe(this) {
            when (it.playerState) {
                PlayerState.STATE_PLAYING -> startPlayer()
                PlayerState.STATE_PAUSED, PlayerState.STATE_PREPARED -> pausePlayer()
                else -> {}
            }
            binding.elapsedTime.text = it.elapsedTime
        }

        viewModel.isFavourite.observe(this) {
            when (it) {
                false -> binding.likeButton.setImageResource(R.drawable.like_button)
                true -> binding.likeButton.setImageResource(R.drawable.favourite_button)
            }
        }

        binding.back.setOnClickListener { finish() }
        binding.playButton.setOnClickListener { viewModel.playbackControl() }
        binding.likeButton.setOnClickListener { viewModel.processFavouriteButtonClicked() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroyPlayer()
    }

    private fun startPlayer() {
        binding.playButton.setImageResource(R.drawable.pause_button)
    }

    private fun pausePlayer() {
        binding.playButton.setImageResource(R.drawable.play_button)
    }

}