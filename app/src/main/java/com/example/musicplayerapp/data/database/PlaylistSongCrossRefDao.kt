package com.example.musicplayerapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.musicplayerapp.data.database.entities.PlaylistSongCrossRef

@Dao
interface PlaylistSongCrossRefDao {
    @Insert
    suspend fun insert(playlistSongCrossRef: PlaylistSongCrossRef)

    @Delete
    suspend fun delete(playlistSongCrossRef: PlaylistSongCrossRef)

    // query to get all song IDs associated with a playlist
    @Query("SELECT songId FROM PlaylistSongCrossRef WHERE playlistId = :playlistId")
    suspend fun getSongsForPlaylist(playlistId: Long): List<Long>

    @Query("SELECT playlistId FROM PlaylistSongCrossRef WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun existingEntry(songId: Long, playlistId: Long): List<Long>
}