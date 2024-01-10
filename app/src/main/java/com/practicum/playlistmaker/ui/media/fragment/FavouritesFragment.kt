package com.practicum.playlistmaker.ui.media.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavouritesBinding
import com.practicum.playlistmaker.domain.media.model.FavouritesState
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.media.view_model.FavouritesViewModel
import com.practicum.playlistmaker.ui.search.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouritesFragment : Fragment() {


    private val viewModel: FavouritesViewModel by viewModel()

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

        viewModel.state.observe(viewLifecycleOwner) {
            renderState(it)
        }

        trackAdapter = TrackAdapter(
            clickListener = {
                if (viewModel.clickDebounce()) {
                    val bundle = Bundle()
                    bundle.putParcelable("track", it)
                    findNavController().navigate(R.id.playerFragment, bundle)
                }
            },
            longClickListener = null
        )
        trackAdapter.tracks = favouriteTracks
        binding.trackList.layoutManager = LinearLayoutManager(requireContext())
        binding.trackList.adapter = trackAdapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTracks()
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
        fun newInstance(text: String) = FavouritesFragment().apply {
            arguments = Bundle().apply {
                putString(TEXT_KEY, text)
            }
        }
    }
}