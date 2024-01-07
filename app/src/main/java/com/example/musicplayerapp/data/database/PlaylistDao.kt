package com.example.musicplayerapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicplayerapp.data.database.entities.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlist")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist)

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("SELECT name FROM playlist WHERE id = :id")
    suspend fun getPlaylistNameById(id: Long) : String

    @Query("SELECT name FROM playlist WHERE name = :name")
    suspend fun existingPlaylist(name: String) : List<String>

    @Query("SELECT * FROM playlist WHERE id = :id")
    suspend fun getPlaylist(id: Long) : Playlist
}