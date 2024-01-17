package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.AddedTrackEntity

@Dao
interface AddedTrackDao {
    @Insert(entity = AddedTrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertAddedTrack(addedTrack: AddedTrackEntity)

    @Delete()
    fun deleteAddedTrack(addedTrack: AddedTrackEntity)

    @Query("SELECT * FROM added_track_table")
    suspend fun getAddedTracks(): List<AddedTrackEntity>

    @Query("SELECT * FROM added_track_table WHERE trackId=:trackId")
    suspend fun getAddedTrack(trackId: String):AddedTrackEntity

}