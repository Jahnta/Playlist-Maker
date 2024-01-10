package com.practicum.playlistmaker.ui.search.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.domain.search.model.SearchFragmentState
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.search.TrackAdapter
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModel()

    private val tracks = ArrayList<Track>()
    private var searchHistoryTracks = ArrayList<Track>()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            viewModel.saveTrack(track)
            val bundle = Bundle()
            bundle.putParcelable("track", track)
            findNavController().navigate(R.id.playerFragment, bundle)
        }

        trackAdapter = TrackAdapter(
            clickListener = {
                onTrackClickDebounce(it)
            },
            longClickListener = null
        )
        searchHistoryAdapter = TrackAdapter(
            clickListener = {
                onTrackClickDebounce(it)
            },
            longClickListener = null
        )

        binding.trackList.layoutManager = LinearLayoutManager(requireContext())
        trackAdapter.tracks = tracks
        binding.trackList.adapter = trackAdapter

        binding.searchHistoryList.layoutManager = LinearLayoutManager(requireContext())
        searchHistoryAdapter.tracks = searchHistoryTracks
        binding.searchHistoryList.adapter = searchHistoryAdapter

        viewModel.showHistory()

        binding.queryInput.setText(viewModel.savedSearchRequest)
        binding.queryInput.setOnFocusChangeListener { _, hasFocus ->
            binding.searchHistoryLayout.visibility =
                if (hasFocus && binding.queryInput.text.isEmpty() && searchHistoryTracks.isNotEmpty()) View.VISIBLE
                else View.GONE
        }
        binding.queryInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = clearButtonVisibility(s)
                if (s != null) {
                    val searchText = binding.queryInput.text.toString()
                    if (searchText.isNotBlank()) {
                        viewModel.searchDebounce(binding.queryInput.text.toString())
                    } else {
                        viewModel.stopSearch()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.savedSearchRequest = s.toString()
            }
        })
        binding.queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val searchRequest = binding.queryInput.text.toString()
                if (searchRequest.isNotBlank()) {
                    viewModel.searchRequest(searchRequest)
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.clearIcon.setOnClickListener {
            viewModel.stopSearch()
            binding.queryInput.setText("")
            viewModel.savedSearchRequest = ""
            val keyboard =
                requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(
                binding.queryInput.windowToken, 0
            )
            binding.queryInput.clearFocus()
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            viewModel.showHistory()
        }

        binding.refreshButton.setOnClickListener {
            val searchRequest = binding.queryInput.text.toString()
            if (searchRequest.isNotBlank()) {
                viewModel.searchRequest(searchRequest)
            }
        }

        if (searchHistoryTracks.isNotEmpty()) {
            binding.searchHistoryLayout.visibility = View.VISIBLE
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            binding.searchHistoryLayout.visibility = View.GONE
            searchHistoryAdapter.notifyDataSetChanged()
        }
        viewModel.state.observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    private fun renderState(state: SearchFragmentState) {
        when (state) {
            is SearchFragmentState.SearchHistory -> showHistory(state.trackList)
            is SearchFragmentState.Content -> showFoundTracks(state.trackList)
            is SearchFragmentState.Loading -> showLoading()
            is SearchFragmentState.Empty -> showEmpty(getString(R.string.no_data))
            is SearchFragmentState.ConnectionError -> showError(getString(R.string.no_connection))
        }
    }

    private fun showHistory(tracks: List<Track>) {
        binding.placeholderLayout.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.trackList.visibility = View.GONE
        searchHistoryTracks.clear()
        searchHistoryTracks.addAll(tracks)
        if (searchHistoryTracks.isNotEmpty()) {
            binding.searchHistoryLayout.visibility = View.VISIBLE
            searchHistoryAdapter.notifyDataSetChanged()
        }
    }

    private fun showFoundTracks(foundTracks: List<Track>) {
        binding.placeholderLayout.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.trackList.visibility = View.VISIBLE
        binding.searchHistoryLayout.visibility = View.GONE
        tracks.clear()
        tracks.addAll(foundTracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showLoading() {
        binding.placeholderLayout.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.trackList.visibility = View.GONE
        binding.searchHistoryLayout.visibility = View.GONE
    }

    private fun showEmpty(message: String) {
        binding.placeholderLayout.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.trackList.visibility = View.GONE
        binding.searchHistoryLayout.visibility = View.GONE

        binding.placeholderMessage.text = message
        binding.refreshButton.visibility = View.GONE
        binding.placeholderImage.setImageResource(R.drawable.error_no_data)
    }

    private fun showError(errorMessage: String) {
        binding.placeholderLayout.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.trackList.visibility = View.GONE
        binding.searchHistoryLayout.visibility = View.GONE

        binding.placeholderMessage.text = errorMessage
        binding.refreshButton.visibility = View.VISIBLE
        binding.placeholderImage.setImageResource(R.drawable.error_no_connection)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }


}