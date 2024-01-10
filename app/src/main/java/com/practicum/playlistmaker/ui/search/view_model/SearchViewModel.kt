package com.practicum.playlistmaker.ui.search.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.search.model.SearchFragmentState
import com.practicum.playlistmaker.domain.search.SearchInteractor
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val interactor: SearchInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchRequest(latestSearchText) }

    private var latestSearchText: String = ""


    private val _state = MutableLiveData<SearchFragmentState>()
    val state: LiveData<SearchFragmentState>
        get() = _state

    var savedSearchRequest = ""

    private val searchTextDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true) { text ->
        searchRequest(text)
    }


    fun searchDebounce(searchText: String) {
        if (searchText.isBlank()) {
            _state.value = SearchFragmentState.SearchHistory(getHistory())
        } else {
            this.latestSearchText = searchText
            searchTextDebounce(searchText)
        }
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchFragmentState.Loading)

            viewModelScope.launch {
                interactor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)

                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                renderState(SearchFragmentState.ConnectionError)
            }

            tracks.isEmpty() -> {
                renderState(SearchFragmentState.Empty)
            }

            else -> {
                renderState(SearchFragmentState.Content(trackList = tracks))
            }
        }
    }

    private fun renderState(state: SearchFragmentState) {
        _state.postValue(state)
    }

    private fun getHistory() = interactor.getAllTracks()


    fun saveTrack(track: Track) {
        interactor.saveTrack(track)
    }

    fun clearHistory() {
        interactor.clearHistory()
        _state.value = SearchFragmentState.SearchHistory(
            emptyList()
        )
    }

    fun showHistory() {
        _state.value = SearchFragmentState.SearchHistory(
            getHistory()
        )
    }

    fun stopSearch() {
        handler.removeCallbacks(searchRunnable)
        showHistory()
    }
}