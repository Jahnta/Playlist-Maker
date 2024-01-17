package com.practicum.playlistmaker.ui.player.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.player.PlaylistBottomSheetAdapter
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.utils.Tools
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {
    private lateinit var binding: FragmentPlayerBinding
    private var track: Track? = null
    private lateinit var playlistAdapter: PlaylistBottomSheetAdapter
    private val viewModel: PlayerViewModel by viewModel { parametersOf(track) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetContainer = binding.bottomSheet
        val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> =
            BottomSheetBehavior.from(bottomSheetContainer).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.visibility = View.GONE
                            Log.d("D", "GONE")
                        }

                        else -> {
                            binding.overlay.visibility = View.VISIBLE
                            Log.d("D", "VISIBLE")
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.overlay.alpha = 0.7f + slideOffset
                }
            }
        )

        track = arguments?.getParcelable<Track>(TRACK_KEY)

        Glide.with(this)
            .load(track?.artworkUrl100?.replaceAfterLast("/", "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .transform(
                RoundedCorners(
                    resources.getDimensionPixelSize(R.dimen.corner_radius_8)
                )
            )
            .into(binding.trackCover)
        binding.trackName.text = track?.trackName
        binding.artistName.text = track?.artistName
        binding.trackDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track?.trackTimeMillis)
        binding.trackAlbum.text = track?.collectionName
        binding.trackYear.text = track?.releaseDate?.substring(0, 4)
        binding.trackGenre.text = track?.primaryGenreName
        binding.trackCountry.text = track?.country

        viewModel.playerInfo.observe(viewLifecycleOwner) {
            when (it.playerState) {
                PlayerState.STATE_PLAYING -> startPlayer()
                PlayerState.STATE_PAUSED, PlayerState.STATE_PREPARED -> pausePlayer()
                else -> {}
            }
            binding.elapsedTime.text = it.elapsedTime
        }

        viewModel.isFavourite.observe(viewLifecycleOwner) {
            when (it) {
                false -> binding.likeButton.setImageResource(R.drawable.like_button)
                true -> binding.likeButton.setImageResource(R.drawable.favourite_button)
            }
        }

        binding.back.setOnClickListener { findNavController().popBackStack() }
        binding.playButton.setOnClickListener { viewModel.playbackControl() }
        binding.likeButton.setOnClickListener { viewModel.processFavouriteButtonClicked() }

        binding.addButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.newPlaylistFragment)
        }


        if (!viewModel.playlists.value.isNullOrEmpty()) {
            playlistAdapter = viewModel.playlists.value?.let { it ->
                PlaylistBottomSheetAdapter(it) {
                    addTrackToPlaylist(track!!, it)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }!!
        } else {
            playlistAdapter = PlaylistBottomSheetAdapter(emptyList()) {}
        }
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.adapter = playlistAdapter

        viewModel.getPlaylists().observe(viewLifecycleOwner) { playlistList ->
            if (playlistList.isNullOrEmpty()) return@observe
            binding.recyclerView.adapter = PlaylistBottomSheetAdapter(playlistList) {
                addTrackToPlaylist(track!!, it)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }


    private fun startPlayer() {
        binding.playButton.setImageResource(R.drawable.pause_button)
    }

    private fun pausePlayer() {
        binding.playButton.setImageResource(R.drawable.play_button)
    }

    private fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        lifecycleScope.launch {
            viewModel.addTrackToPlaylist(track, playlist)
            viewModel.isInPlaylist.observe(requireActivity()) { isInPlaylist ->
                if (isInPlaylist) {
                    Tools.showSnackbar(
                        binding.root,
                        getString(R.string.already_in_playlist, playlist.playlistTitle),
                        requireActivity()
                    )
                } else {
                    Tools.showSnackbar(
                        binding.root,
                        getString(R.string.added, playlist.playlistTitle),
                        requireActivity()
                    )
                }

            }
        }
    }

    companion object {
        private const val TRACK_KEY = "track"
    }

}
