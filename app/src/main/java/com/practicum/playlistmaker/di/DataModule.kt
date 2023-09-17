package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.practicum.playlistmaker.data.player.PlayerRepository
import com.practicum.playlistmaker.data.player.impl.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.search.network.ITunesApiService
import com.practicum.playlistmaker.data.search.network.NetworkClient
import com.practicum.playlistmaker.data.search.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.sharing.ContentProvider
import com.practicum.playlistmaker.data.sharing.ExternalNavigator
import com.practicum.playlistmaker.data.sharing.impl.ContentProviderImpl
import com.practicum.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.domain.search.model.Track
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("playlist_maker_preferences", Context.MODE_PRIVATE)
    }

    single<NetworkClient> { RetrofitNetworkClient(get(), androidContext()) }
    single<ExternalNavigator> { ExternalNavigatorImpl(androidContext()) }
    single<ContentProvider> { ContentProviderImpl(androidContext()) }
    factory { MediaPlayer() }

    factory<PlayerRepository> { (track: Track) -> PlayerRepositoryImpl(track, get()) }
}
