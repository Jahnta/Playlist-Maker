package com.practicum.playlistmaker.data.search

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.search.model.Track

const val SEARCHHISTORY_KEY = "key_for_searchHistory"

class SearchHistory(val sharedPreferences: SharedPreferences) {

    fun getTracks() : Array<Track> {
        val jsonTrackList = sharedPreferences.getString(SEARCHHISTORY_KEY, null) ?: return emptyArray()
        return Gson().fromJson(jsonTrackList, Array<Track>::class.java)
    }

    fun putTracks(trackList: ArrayList<Track>) {
        val jsonTrackList = Gson().toJson(trackList)
        sharedPreferences.edit().putString(SEARCHHISTORY_KEY, jsonTrackList).apply()
    }

    fun clear() {
        sharedPreferences.edit().remove(SEARCHHISTORY_KEY).apply()
    }

}