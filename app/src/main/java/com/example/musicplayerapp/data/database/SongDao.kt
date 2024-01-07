package com.example.musicplayerapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicplayerapp.data.database.entities.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM song")
    fun getAllSongs(): Flow<List<Song>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(songs: List<Song>)

    @Delete
    suspend fun deleteSong(song: Song)

    @Query("SELECT id FROM song WHERE title = :title AND artist = :artist")
    suspend fun getSongIdByTitleAndArtist(title: String, artist: String): Long?

    @Query("SELECT * FROM song WHERE id = :id")
    suspend fun getSongById(id: Long): Song?
}