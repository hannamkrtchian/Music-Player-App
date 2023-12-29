package com.example.musicplayerapp.data.database

import androidx.annotation.WorkerThread
import com.example.musicplayerapp.data.database.entities.Song
import kotlinx.coroutines.flow.Flow

class SongRepository(private val songDao: SongDao) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allSongs: Flow<List<Song>> = songDao.getAllSongs()

    @WorkerThread
    suspend fun insert(song: Song) {
        songDao.insertSong(song)
    }

    @WorkerThread
    suspend fun update(song: Song) {
        songDao.updateSong(song)
    }

    @WorkerThread
    suspend fun delete(song: Song) {
        songDao.deleteSong(song)
    }
}