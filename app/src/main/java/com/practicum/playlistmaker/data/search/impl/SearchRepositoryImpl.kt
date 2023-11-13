package com.practicum.playlistmaker.data.search.impl

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.utils.Resource
import com.practicum.playlistmaker.data.search.dto.TrackSearchRequest
import com.practicum.playlistmaker.data.search.dto.TrackSearchResponse
import com.practicum.playlistmaker.data.search.network.NetworkClient
import com.practicum.playlistmaker.data.search.SearchRepository
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    context: Context,
    private val networkClient: NetworkClient
) : SearchRepository {

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val SEARCH_KEY = "search_key"
        const val TRACKLIST_SIZE = 10
    }

    private val savedTracks = mutableListOf<Track>()
    private val sharedPrefs = context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES,
        Context.MODE_PRIVATE
    )
    private val resources = context.resources
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(message = resources.getString(R.string.noConnectionError)))
            }
            200 -> {
                with(response as TrackSearchResponse) {
                    val data = results.map {
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
                    }
                    emit(Resource.Success(data))
                }
            }

            else -> {
                emit(Resource.Error(message = resources.getString(R.string.serverError)))
            }
        }
    }
    override fun saveTrack(track: Track) {
        if (savedTracks.contains(track)) {
            savedTracks.remove(track)
        }
        if (savedTracks.size == TRACKLIST_SIZE) {
            savedTracks.removeLast()
        }
        savedTracks.add(0, track)

        sharedPrefs.edit()
            .putString(
                SEARCH_KEY,
                Gson().toJson(savedTracks.toTypedArray())
            )
            .apply()
    }

    override fun getAllTracks(): List<Track> {
        val tracksString = sharedPrefs.getString(SEARCH_KEY, "")
        if (tracksString?.isNotEmpty() == true) {
            savedTracks.clear()
            savedTracks.addAll(Gson().fromJson(tracksString, Array<Track>::class.java))
        }
        return savedTracks.toList()
    }

    override fun clearHistory() {
        savedTracks.clear()
        sharedPrefs.edit()
            .remove(SEARCH_KEY)
            .apply()
    }

}