package com.practicum.playlistmaker.data.search.impl

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.utils.Resource
import com.practicum.playlistmaker.data.search.dto.TrackSearchRequest
import com.practicum.playlistmaker.data.search.dto.TrackSearchResponse
import com.practicum.playlistmaker.data.search.network.NetworkClient
import com.practicum.playlistmaker.data.search.SearchRepository
import com.practicum.playlistmaker.domain.search.model.Track

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
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error(message = "Проверьте подключение к интернету")
            }

            200 -> {
                Resource.Success(
                    data = (response as TrackSearchResponse).results.map {
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
            }

            else -> {
                Resource.Error(message = "Ошибка сервера")
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