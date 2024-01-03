package com.practicum.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.data.db.dao.AddedTrackDao
import com.practicum.playlistmaker.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.data.db.dao.TrackDao
import com.practicum.playlistmaker.data.db.entity.AddedTrackEntity
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.data.db.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class, AddedTrackEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun addedTrackDao(): AddedTrackDao
}