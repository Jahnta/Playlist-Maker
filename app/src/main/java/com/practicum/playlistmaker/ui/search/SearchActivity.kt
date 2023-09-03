package com.practicum.playlistmaker.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.SearchHistory
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.data.network.ITunesApiService
import com.practicum.playlistmaker.ui.player.PlayerActivity
import com.practicum.playlistmaker.ui.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    companion object {
        const val searchInput = "searchInput"
        const val itunesBaseUrl = "https://itunes.apple.com"
        const val maxSearchHistorySize = 10
        const val CLICK_DEBOUNCE_DELAY = 1_000L
        const val SEARCH_DEBOUNCE_DELAY = 2_000L
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
    }

    private var savedText = ""
    private var lastQuery = ""
    private val retrofit =
        Retrofit.Builder()
            .baseUrl(itunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    private val itunesService = retrofit.create(ITunesApiService::class.java)

    private lateinit var backButton: ImageView
    private lateinit var queryInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var trackList: RecyclerView
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var refreshButton: Button
    private lateinit var searchHistory: SearchHistory
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var searchHistoryList: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var progressBar: ProgressBar

    private val tracks = ArrayList<Track>()
    private var searchHistoryTracks = ArrayList<Track>()

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistory =
            SearchHistory(getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE))
        searchHistoryTracks.addAll(searchHistory.getTracks())

        backButton = findViewById(R.id.back)
        queryInput = findViewById(R.id.queryInput)
        clearButton = findViewById(R.id.clearIcon)
        trackList = findViewById(R.id.trackList)
        placeholderLayout = findViewById(R.id.placeholderLayout)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        refreshButton = findViewById(R.id.refreshButton)
        searchHistoryLayout = findViewById(R.id.searchHistoryLayout)
        searchHistoryList = findViewById(R.id.searchHistoryList)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progressBar)

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            queryInput.setText("")
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderLayout.visibility = View.GONE
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchHistoryLayout.visibility =
                    if (queryInput.hasFocus() && s?.isEmpty() == true && searchHistoryTracks.isNotEmpty()) View.VISIBLE else View.GONE
                savedText = s.toString()
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        queryInput.addTextChangedListener(simpleTextWatcher)


        queryInput.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryLayout.visibility =
                (if (hasFocus && queryInput.text.isEmpty() && searchHistoryTracks.isNotEmpty()) View.VISIBLE else View.GONE)
        }

        refreshButton.setOnClickListener {
            getTracks()
        }

        trackList.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(tracks) {
            addTrackToHistory(it)
            showPlayer(it)
        }
        trackList.adapter = trackAdapter

        clearHistoryButton.setOnClickListener {
            searchHistoryTracks.clear()
            searchHistory.clear()
            searchHistoryLayout.visibility = View.GONE
        }

        searchHistoryList.layoutManager = LinearLayoutManager(this)
        searchHistoryAdapter = TrackAdapter(searchHistoryTracks) {
            showPlayer(it)
        }
        searchHistoryList.adapter = searchHistoryAdapter

    }

    override fun onStop() {
        super.onStop()
        searchHistory.putTracks(searchHistoryTracks)
    }

    private fun addTrackToHistory(track: Track) {
        for (index in searchHistoryTracks.indices) {
            if (track.trackId == searchHistoryTracks[index].trackId) {
                searchHistoryTracks.removeAt(index)
                searchHistoryTracks.add(0, track)
                searchHistoryAdapter.notifyItemMoved(index, 0)
                return
            }
        }

        if (searchHistoryTracks.size < maxSearchHistorySize) {
            searchHistoryTracks.add(0, track)
            searchHistoryAdapter.notifyDataSetChanged()
            searchHistoryAdapter.notifyItemRangeChanged(0, searchHistoryTracks.size)
        } else {
            searchHistoryTracks.removeAt(searchHistoryTracks.size - 1)
            searchHistoryAdapter.notifyDataSetChanged()
            searchHistoryAdapter.notifyItemRangeChanged(
                searchHistoryTracks.size - 1,
                searchHistoryTracks.size
            )
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showPlayer(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.TRACK_DATA, Gson().toJson(track))
            }
            startActivity(intent)
        }
    }


    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(searchInput, savedText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedText = savedInstanceState.getString(searchInput, "")
    }


    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private
    val searchRunnable = Runnable {
        getTracks()
    }

    private fun getTracks() {
        if (queryInput.text.isNotEmpty()) {
            trackList.visibility = View.GONE
            placeholderLayout.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            itunesService.searchTrack(queryInput.text.toString())
                .enqueue(object : Callback<TrackSearchResponse> {
                    override fun onResponse(
                        call: Call<TrackSearchResponse>,
                        response: Response<TrackSearchResponse>
                    ) {
                        progressBar.visibility = View.GONE
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.visibility = View.VISIBLE
                                tracks.addAll(response.body()?.results!!.map {
                                    Track(
                                        it.trackId,
                                        it.trackName,
                                        it.artistName,
                                        it.trackTimeMillis,
                                        it.artworkUrl100,
                                        it.collectionName,
                                        it.country,
                                        it.primaryGenreName,
                                        it.releaseDate,
                                        it.previewUrl
                                    )
                                })
                                trackAdapter.notifyDataSetChanged()
                            }
                            if (tracks.isEmpty()) {
                                showMessage(getString(R.string.no_data), "")
                            } else {
                                showMessage("", "")
                            }
                        } else {
                            showMessage(getString(R.string.no_connection), "")
                            lastQuery = queryInput.text.toString()
                        }
                    }

                    override fun onFailure(
                        call: Call<TrackSearchResponse>,
                        t: Throwable
                    ) {
                        progressBar.visibility = View.GONE
                        showMessage(
                            getString(R.string.no_connection),
                            t.message.toString()
                        )
                        lastQuery = queryInput.text.toString()
                    }
                })
        }
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderLayout.visibility = View.VISIBLE
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderMessage.text = text
            if (text == getString(R.string.no_data)) {
                placeholderImage.setImageResource(R.drawable.error_no_data)
                refreshButton.visibility = View.GONE
            } else if (text == getString(R.string.no_connection)) {
                placeholderImage.setImageResource(R.drawable.error_no_connection)
                refreshButton.visibility = View.VISIBLE
            }
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(
                    applicationContext,
                    additionalMessage,
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        } else {
            placeholderLayout.visibility = View.GONE
        }
    }

}