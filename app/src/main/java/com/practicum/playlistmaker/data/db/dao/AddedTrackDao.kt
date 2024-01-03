package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.AddedTrackEntity

@Dao
interface AddedTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAddedTrack(addedTrack: AddedTrackEntity)

}