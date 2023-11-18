package com.practicum.playlistmaker.ui.media.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavouritesBinding
import com.practicum.playlistmaker.domain.media.model.FavouritesState
import com.practicum.playlistmaker.domain.search.model.SearchFragmentState
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.media.view_model.MediaFavouritesViewModel
import com.practicum.playlistmaker.ui.player.activity.PlayerActivity
import com.practicum.playlistmaker.ui.search.TrackAdapter
import com.practicum.playlistmaker.ui.search.fragment.SearchFragment
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFavouritesFragment : Fragment() {


    private val favouritesViewModel: MediaFavouritesViewModel by viewModel()

    private lateinit var binding: FragmentFavouritesBinding

    private var favouriteTracks = ArrayList<Track>()
    private lateinit var trackAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favouritesViewModel.state.observe(viewLifecycleOwner) {
            renderState(it)
        }

        trackAdapter = TrackAdapter {
            if (favouritesViewModel.clickDebounce()) {
                PlayerActivity.newIntent(requireContext(), it)
                    .apply { startActivity(this) }
            }
        }
        binding.trackList.layoutManager = LinearLayoutManager(requireContext())
        trackAdapter.tracks = favouriteTracks
        binding.trackList.adapter = trackAdapter
    }

    override fun onResume() {
        super.onResume()
        favouritesViewModel.getTracks()
    }

    private fun renderState(state: FavouritesState) {
        when (state) {
            is FavouritesState.Empty -> showEmpty()
            is FavouritesState.Content -> showTracks(state.tracks)
            else -> {}
        }
    }

    private fun showEmpty() {
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.placeholderMessage.text = requireArguments().getString(TEXT_KEY)
        trackAdapter.tracks = ArrayList(emptyList())
        trackAdapter.notifyDataSetChanged()
    }

    private fun showTracks(tracks: List<Track>) {
        binding.placeholderImage.visibility = View.INVISIBLE
        binding.placeholderMessage.visibility = View.INVISIBLE
        binding.trackList.visibility = View.VISIBLE
        trackAdapter.tracks = ArrayList(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    companion object {

        private const val TEXT_KEY = "text_key"
        fun newInstance(text: String) = MediaFavouritesFragment().apply {
            arguments = Bundle().apply {
                putString(TEXT_KEY, text)
            }
        }
    }
}