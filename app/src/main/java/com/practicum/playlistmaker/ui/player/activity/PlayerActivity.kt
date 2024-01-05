package com.practicum.playlistmaker.ui.player.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.player.PlaylistBottomSheetAdapter
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.utils.Tools
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private lateinit var playlistAdapter: PlaylistBottomSheetAdapter

    private val viewModel: PlayerViewModel by viewModel { parametersOf(track) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomSheetContainer = binding.bottomSheet
        val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> =
            BottomSheetBehavior.from(bottomSheetContainer).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        bottomSheetObserver(bottomSheetBehavior, binding.overlay)

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

        binding.addButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistButton.setOnClickListener {
            val pendingIntent = NavDeepLinkBuilder(this.applicationContext)
                .setGraph(R.navigation.main_navigation_graph)
                .setDestination(R.id.newPlaylistFragment)
                .createPendingIntent()
            pendingIntent.send()
        }

        if (!viewModel.playlists.value.isNullOrEmpty()) {
            playlistAdapter = viewModel.playlists.value?.let { it ->
                PlaylistBottomSheetAdapter(it) {
                    addTrackToPlaylist(track, it)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }!!
        } else {
            playlistAdapter = PlaylistBottomSheetAdapter(emptyList()) {}
        }
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = playlistAdapter

        viewModel.getPlaylists().observe(this) { playlistList ->
            if (playlistList.isNullOrEmpty()) return@observe
            binding.recyclerView.adapter = PlaylistBottomSheetAdapter(playlistList) {
                addTrackToPlaylist(track, it)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Log.d("Запись в плейлист", "click!")
            }
        }

    }

    private fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        var trackIsAdded = false
        lifecycleScope.launch {
            viewModel.addTrackToPlaylist(track, playlist)
            delay(300)
            viewModel.isInPlaylist.observe(this@PlayerActivity) { isInPlaylist ->

                if (!trackIsAdded) {
                    if (isInPlaylist) {
                        Tools.showSnackbar(
                            binding.root,
                            getString(R.string.already_in_playlist, playlist.playlistTitle),
                            this@PlayerActivity
                        )
                        Log.d("Запись в плейлист", "Уже есть ")
                        return@observe
                    } else {
                        Tools.showSnackbar(
                            binding.root,
                            getString(R.string.added, playlist.playlistTitle),
                            this@PlayerActivity
                        )
                        Log.d("Запись в плейлист", "Добавлено  $isInPlaylist")
                        return@observe
                    }
                }
            }
        }
    }

    private fun bottomSheetObserver(
        bottomSheetBehavior: BottomSheetBehavior<LinearLayout>,
        overlay: View
    ) {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) overlay.visibility = View.GONE
                else overlay.visibility = View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = slideOffset
            }
        })
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