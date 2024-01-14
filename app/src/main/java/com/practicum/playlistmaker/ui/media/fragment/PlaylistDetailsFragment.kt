package com.practicum.playlistmaker.ui.media.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.media.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.utils.Tools
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistDetailsBinding
    private lateinit var bottomNavigator: BottomNavigationView
    private var playlist: Playlist? = null
    private lateinit var trackAdapter: PlaylistTrackAdapter
    private val viewModel: PlaylistDetailsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        bottomNavigator = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavigator.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistTracksBottomSheetContainer = binding.playlistTracksBottomSheet
        val playlistTracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout> =
            BottomSheetBehavior.from(playlistTracksBottomSheetContainer).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
        playlistTracksBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset
            }
        })

        val playlistMenuBottomSheetContainer = binding.playlistMenuBottomSheet
        val playlistMenuBottomSheetBehavior: BottomSheetBehavior<LinearLayout> =
            BottomSheetBehavior.from(playlistMenuBottomSheetContainer).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        playlistMenuBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = 0.7f + slideOffset
            }
        })

        playlist = arguments?.getParcelable(PLAYLIST_KEY)

        viewModel.getPlaylist(playlist!!)

        viewModel.updatedPlaylist.observe(viewLifecycleOwner) {
            playlist = it
            renderPlaylist(it)
        }

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.playlistShareButton.setOnClickListener {
            sharePlaylist(playlist!!)
        }

        binding.playlistMenuButton.setOnClickListener {
            playlistMenuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.playlistMenuShareButton.setOnClickListener {
            sharePlaylist(playlist!!)
            playlistMenuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.playlistMenuEditButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(PLAYLIST_KEY, playlist)
            findNavController().navigate(R.id.editPlaylistFragment, bundle)
        }

        binding.playlistMenuDeleteButton.setOnClickListener {
            showDeletePlaylistDialog(playlist!!)
        }

    }

    fun sharePlaylist(playlist: Playlist) {
        val playlistTitleText = playlist.playlistTitle
        val playlistDescriptionText = playlist.playlistDescription
        val playlistTracksCountText = resources.getQuantityString(
            R.plurals.tracks, playlist.playlistTracksCount, playlist.playlistTracksCount
        )
        if (playlist.playlistTracksCount == 0) {
            Tools.showSnackbar(
                binding.root,
                getString(R.string.no_data_to_share, playlist.playlistTitle),
                requireContext()
            )
            return
        }
        var playlistData =
            "$playlistTitleText \n$playlistDescriptionText \n$playlistTracksCountText"
        val tracks: List<Track> = viewModel.tracks.value!!
        var i = 0
        tracks.forEach { track ->
            i += 1
            val artistName = track.artistName
            val trackName = track.trackName
            val trackDuration =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            playlistData += "\n$i. $artistName - $trackName ($trackDuration)"
        }
        val intentSend = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, playlistData)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Intent.createChooser(this, null)
        }
        requireContext().startActivity(intentSend, null)
    }

    fun renderTracks(playlist: Playlist) {

        trackAdapter = PlaylistTrackAdapter(clickListener = {
            val bundle = Bundle()
            bundle.putParcelable(TRACK_KEY, it)
            findNavController().navigate(R.id.playerFragment, bundle)
        }, longClickListener = {
            showDeleteTrackDialog(it)
        })

        binding.playlistTracksRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistTracksRecycler.adapter = trackAdapter

        viewModel.getTracks(playlist!!)
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNullOrEmpty()) {
                Log.d("T", "empty")
                binding.placeholderNoTracks.visibility = View.VISIBLE
                trackAdapter.setItems(tracks)
                binding.playlistTracksDuration.text = getString(R.string.zero_tracks)
                binding.playlistTracksCount.text = getString(R.string.zero_tracks)
                binding.playlistMenuTrackCount.text = getString(R.string.zero_tracks)
            } else {
                Log.d("T", "not empty")
                binding.placeholderNoTracks.visibility = View.GONE
                trackAdapter.setItems(tracks)
                val playlistDuration =
                    SimpleDateFormat("m", Locale.getDefault()).format(countPlaylistDuration(tracks))
                        .toInt()
                binding.playlistTracksDuration.text = resources.getQuantityString(
                    R.plurals.minutes, playlistDuration, playlistDuration
                )
                val playlistTracksCount = tracks.size
                binding.playlistTracksCount.text = resources.getQuantityString(
                    R.plurals.tracks, playlistTracksCount, playlistTracksCount
                )
                binding.playlistMenuTrackCount.text = resources.getQuantityString(
                    R.plurals.tracks, playlistTracksCount, playlistTracksCount
                )
            }
        }
    }

    fun renderPlaylist(playlist: Playlist) {

        Glide.with(this).load(playlist?.playlistCoverPath).placeholder(R.drawable.placeholder)
            .transform(CenterCrop()).into(binding.playlistCover)
        binding.playlistTitle.text = playlist?.playlistTitle
        binding.playlistDesc.text = playlist?.playlistDescription
        binding.playlistTracksDuration.text = getString(R.string.zero_duration)
        binding.playlistTracksCount.text = getString(R.string.zero_tracks)

        Glide.with(this).load(playlist?.playlistCoverPath).placeholder(R.drawable.placeholder)
            .transform(CenterCrop()).into(binding.playlistMenuCover)
        binding.playlistMenuTitle.text = playlist?.playlistTitle
        binding.playlistMenuTrackCount.text = getString(R.string.zero_tracks)

        renderTracks(playlist)

    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.Theme_PlaylistMaker_Dialog_Alert)
            .setTitle(R.string.delete_track_question)
            .setNegativeButton(R.string.no) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ ->
                lifecycleScope.launch {
                    viewModel.deleteTrackFromPlaylist(track, playlist!!)
                }
            }
            .show()
    }

    private fun showDeletePlaylistDialog(playlist: Playlist) {
        MaterialAlertDialogBuilder(requireContext(), R.style.Theme_PlaylistMaker_Dialog_Alert)
            .setTitle(R.string.delete_playlist_question)
            .setNegativeButton(R.string.no) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ ->
                lifecycleScope.launch {
                    viewModel.deletePlaylist(playlist)
                    findNavController().navigateUp()
                }
            }
            .show()

    }

    fun countPlaylistDuration(tracks: List<Track>): Long {
        var result = 0L
        tracks.forEach {
            result += it.trackTimeMillis
        }
        return result
    }

    override fun onDetach() {
        super.onDetach()
        bottomNavigator.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        bottomNavigator.visibility = View.GONE
    }

    companion object {
        private const val PLAYLIST_KEY = "playlist"
        private const val TRACK_KEY = "track"
    }

}