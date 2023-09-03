package com.practicum.playlistmaker.ui.search.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.creator.Creator

class SearchViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(
                searchInteractor = Creator.provideSearchInteractor(context),
                historyInteractor = Creator.provideHistoryInteractor(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}