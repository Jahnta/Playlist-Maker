package com.practicum.playlistmaker.ui.search.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.search.HistoryInteractor
import com.practicum.playlistmaker.domain.search.model.SearchActivityState
import com.practicum.playlistmaker.domain.search.SearchInteractor
import com.practicum.playlistmaker.domain.search.model.Track

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchRequest(latestSearchText) }

    private var latestSearchText: String = ""

    private var isClickAllowed = true

    private val _state = MutableLiveData<SearchActivityState>()
    val state: LiveData<SearchActivityState>
        get() = _state

    var savedSearchRequest = ""

    fun searchDebounce(searchText: String) {
        if (searchText.isBlank()) {
            _state.value = SearchActivityState.SearchHistory(getHistory())
        } else {
            this.latestSearchText = searchText
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchActivityState.Loading)

            searchInteractor.searchTracks(newSearchText, object : SearchInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> {
                            renderState(SearchActivityState.ConnectionError)
                        }

                        tracks.isEmpty() -> {
                            renderState(SearchActivityState.Empty)
                        }

                        else -> {
                            renderState(SearchActivityState.Content(trackList = tracks))
                        }
                    }
                }
            })
        }
    }

    private fun renderState(state: SearchActivityState) {
        _state.postValue(state)
    }

    private fun getHistory() = historyInteractor.getAllTracks()

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun saveTrack(track: Track) {
        historyInteractor.saveTrack(track)
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        _state.value = SearchActivityState.SearchHistory(
            emptyList()
        )
    }

    fun showHistory() {
        _state.value = SearchActivityState.SearchHistory(
            getHistory()
        )
    }

    fun stopSearch() {
        handler.removeCallbacks(searchRunnable)
        showHistory()
    }
}