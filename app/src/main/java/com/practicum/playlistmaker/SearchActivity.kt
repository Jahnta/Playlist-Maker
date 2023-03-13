package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    }

    private var savedText = ""
    private var lastQuery = ""
    private val retrofit =
        Retrofit.Builder()
            .baseUrl(itunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    private val itunesService = retrofit.create(iTunesApi::class.java)

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

    private val tracks = ArrayList<Track>()
    private var searchHistoryTracks = ArrayList<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistory = SearchHistory(getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE))
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
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        queryInput.addTextChangedListener(simpleTextWatcher)
        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (queryInput.text.isNotEmpty()) {
                    getTracks(queryInput.text.toString())
                }
            }
            false
        }

        queryInput.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryLayout.visibility =
                (if (hasFocus && queryInput.text.isEmpty() && searchHistoryTracks.isNotEmpty()) View.VISIBLE else View.GONE)
        }

        refreshButton.setOnClickListener {
            getTracks(lastQuery)
        }

        trackList.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(tracks) {
            addTrackToHistory(it)
        }
        trackList.adapter = trackAdapter

        clearHistoryButton.setOnClickListener {
            searchHistoryTracks.clear()
            searchHistory.clear()
            searchHistoryLayout.visibility = View.GONE
        }

        searchHistoryList.layoutManager = LinearLayoutManager(this)
        searchHistoryAdapter = TrackAdapter(searchHistoryTracks) {
            // пусто
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
                searchHistoryAdapter.notifyItemMoved(index,0)
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

    private fun getTracks(text: String) {
        itunesService.search(text)
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
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

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showMessage(getString(R.string.no_connection), t.message.toString())
                    lastQuery = queryInput.text.toString()
                }
            })
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
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderLayout.visibility = View.GONE
        }
    }

}